// src/pages/instructor/CreateCourse.tsx
import { useState, useEffect } from "react";
import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Plus, Trash2 } from "lucide-react";
import { Switch } from "@/components/ui/switch";
import { useToast } from "@/hooks/use-toast";
import { useNavigate, useParams } from "react-router-dom";
import { saveFullCourse, getCourseDetail, deleteChapter, deleteLesson, getCategories } from "@/api/courseApi";
import { getDiscountList, attachCourses, DiscountDto } from "@/api/discountApi";
import { Dialog, DialogContent } from "@/components/ui/dialog";
import { Loader2 } from "lucide-react";

interface Lesson {
  localId: number;
  lessonId: number | null;
  title: string;
  videoPath: string;
  slidePath: string;
  typeDocument: string;
  sortOrder: number;
}

interface Chapter {
  localId: number;
  chapterId: number | null;
  title: string;
  orderChapter: number;
  lessons: Lesson[];
}

interface CategoryNode {
  id: number;
  name: string;
  sortOrder: number;
  status: boolean;
  parentId: number | null;
  children: CategoryNode[];
}

interface CategoryOption {
  value: string;
  label: string;
}

interface VoucherOption {
  value: string;
  label: string;
  percent?: number;
  amount?: number;
}

const flattenCategories = (nodes: CategoryNode[], prefix: string = ''): CategoryOption[] => {
  let options: CategoryOption[] = [];
  for (let node of nodes) {
    options.push({ value: node.id.toString(), label: `${prefix}${node.name}` });
    if (node.children.length > 0) {
      options = options.concat(flattenCategories(node.children, prefix + '— '));
    }
  }
  return options;
};

export default function CreateCourse() {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [content, setContent] = useState("");
  const [image, setImage] = useState("");
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [price, setPrice] = useState(0);
  const [promotionPrice, setPromotionPrice] = useState(0);
  const [categoryId, setCategoryId] = useState<number | null>(null);
  const [selectedVoucherId, setSelectedVoucherId] = useState<number | null>(null);
  const [publish, setPublish] = useState(false);
  const [chapters, setChapters] = useState<Chapter[]>([]);
  const [categories, setCategories] = useState<CategoryOption[]>([]);
  const [vouchers, setVouchers] = useState<VoucherOption[]>([]);
  const [loading, setLoading] = useState(false);
  const { toast } = useToast();
  const navigate = useNavigate();
  const { courseId } = useParams<{ courseId?: string }>();

  const isEdit = !!courseId;

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = await getCategories();
        setCategories(flattenCategories(data));
      } catch (error) {
        toast({
          title: "Error loading categories",
          description: "Could not fetch categories.",
          variant: "destructive",
        });
      }
    };
    fetchCategories();
  }, [toast]);

  useEffect(() => {
    const fetchVouchers = async () => {
      try {
        const data: DiscountDto[] = await getDiscountList();
        const activeVouchers = data.filter(v => v.active);
        setVouchers(activeVouchers.map(v => ({
          value: v.discountId.toString(),
          label: `${v.code} (${v.percent ? `${v.percent}%` : `$${v.amount}`})`,
          percent: v.percent,
          amount: v.amount
        })));
      } catch (error) {
        toast({
          title: "Error loading vouchers",
          description: "Could not fetch active vouchers.",
          variant: "destructive",
        });
      }
    };
    fetchVouchers();
  }, [toast]);

  useEffect(() => {
    if (isEdit) {
      const fetchData = async () => {
        try {
          setLoading(true);
          const data = await getCourseDetail(parseInt(courseId!));
          setTitle(data.name || "");
          setDescription(data.description || "");
          setContent(data.content || "");
          setImage(data.image || "");
          setPrice(data.price || 0);
          setPromotionPrice(data.promotionPrice || 0);
          setCategoryId(data.categoryId);
          setPublish(!!data.status);
          setChapters(
            data.chapters.map((ch: any, chIndex: number) => ({
              localId: Date.now() + chIndex,
              chapterId: ch.id,
              title: ch.nameChapter,
              orderChapter: ch.orderChapter,
              lessons: ch.lessons.map((ls: any, lsIndex: number) => ({
                localId: Date.now() + chIndex + lsIndex,
                lessonId: ls.id,
                title: ls.name,
                videoPath: ls.videoPath,
                slidePath: ls.slidePath,
                typeDocument: ls.typeDocument || "video",
                sortOrder: ls.sortOrder,
              })),
            }))
          );
        } catch (error) {
          toast({
            title: "Error loading course",
            description: "Could not fetch course details.",
            variant: "destructive",
          });
        } finally {
          setLoading(false);
        }
      };
      fetchData();
    }
  }, [courseId, isEdit, toast]);

  // Tự động tính lại promotionPrice khi thay đổi price hoặc selectedVoucherId
  useEffect(() => {
    if (selectedVoucherId === null) {
      setPromotionPrice(0); // Hoặc có thể giữ nguyên giá trị hiện tại nếu không chọn voucher
      return;
    }

    const selectedVoucher = vouchers.find(opt => parseInt(opt.value) === selectedVoucherId);
    if (!selectedVoucher) return;

    let newPromo = price;
    if (selectedVoucher.percent !== undefined && selectedVoucher.percent > 0) {
      newPromo = price * (1 - selectedVoucher.percent / 100);
    } else if (selectedVoucher.amount !== undefined && selectedVoucher.amount > 0) {
      newPromo = price - selectedVoucher.amount;
    }

    // Làm tròn đến 2 chữ số thập phân và đảm bảo không âm
    setPromotionPrice(Math.max(0, parseFloat(newPromo.toFixed(2))));
  }, [price, selectedVoucherId, vouchers]);

  const addChapter = () => {
    setChapters([
      ...chapters,
      {
        localId: Date.now(),
        chapterId: null,
        title: "",
        orderChapter: chapters.length + 1,
        lessons: [],
      },
    ]);
  };

  const updateChapter = (localId: number, field: string, value: any) => {
    setChapters(
      chapters.map((ch) =>
        ch.localId === localId ? { ...ch, [field]: value } : ch
      )
    );
  };

  const addLesson = (chapterLocalId: number) => {
    setChapters(
      chapters.map((chapter) =>
        chapter.localId === chapterLocalId
          ? {
              ...chapter,
              lessons: [
                ...chapter.lessons,
                {
                  localId: Date.now(),
                  lessonId: null,
                  title: "",
                  videoPath: "",
                  slidePath: "",
                  typeDocument: "",
                  sortOrder: chapter.lessons.length + 1,
                },
              ],
            }
          : chapter
      )
    );
  };

  const updateLesson = (chapterLocalId: number, lessonLocalId: number, field: string, value: any) => {
    setChapters(
      chapters.map((chapter) =>
        chapter.localId === chapterLocalId
          ? {
              ...chapter,
              lessons: chapter.lessons.map((lesson) =>
                lesson.localId === lessonLocalId ? { ...lesson, [field]: value } : lesson
              ),
            }
          : chapter
      )
    );
  };

  const removeChapter = async (chapterLocalId: number, chapterId: number | null) => {
    if (chapterId) {
      try {
        await deleteChapter(chapterId);
      } catch (error) {
        toast({
          title: "Error deleting chapter",
          variant: "destructive",
        });
        return;
      }
    }
    setChapters(chapters.filter((c) => c.localId !== chapterLocalId));
  };

  const removeLesson = async (chapterLocalId: number, lessonLocalId: number, lessonId: number | null) => {
    if (lessonId) {
      try {
        await deleteLesson(lessonId);
      } catch (error) {
        toast({
          title: "Error deleting lesson",
          variant: "destructive",
        });
        return;
      }
    }
    setChapters(
      chapters.map((chapter) =>
        chapter.localId === chapterLocalId
          ? {
              ...chapter,
              lessons: chapter.lessons.filter((l) => l.localId !== lessonLocalId),
            }
          : chapter
      )
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      const fullReq = {
        courseId: isEdit ? parseInt(courseId!) : null,
        name: title,
        description,
        image,
        content,
        price,
        promotionPrice,
        categoryId,
        publish,
        chapters: chapters.map((chapter, chIndex) => ({
          chapterId: chapter.chapterId,
          nameChapter: chapter.title,
          orderChapter: chIndex + 1,
          lessons: chapter.lessons.map((lesson, lsIndex) => ({
            courseLessonId: lesson.lessonId,
            name: lesson.title,
            videoPath: lesson.videoPath,
            slidePath: lesson.slidePath,
            typeDocument: lesson.typeDocument,
            sortOrder: lsIndex + 1,
          })),
        })),
        discountIds: selectedVoucherId ? [selectedVoucherId] : null,
      };

      const savedCourseId = await saveFullCourse(fullReq, imageFile);

      if (selectedVoucherId) {
        await attachCourses({ discountId: selectedVoucherId, courseIds: [savedCourseId] });
      }

      toast({
        title: `${isEdit ? "Updated" : "Created"} successfully`,
        description: "Your course has been saved.",
      });
      navigate("/instructor/courses");
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to save the course.",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <DashboardLayout role="instructor">
      <div className="max-w-4xl animate-fade-in">
        <div className="mb-6">
          <h2 className="text-3xl font-bold tracking-tight">{isEdit ? "Edit Course" : "Create New Course"}</h2>
          <p className="text-muted-foreground">Fill in the course details and curriculum</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Basic Information</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <Label htmlFor="title">Course Title *</Label>
                <Input id="title" placeholder="e.g., React Advanced Patterns" required value={title} onChange={(e) => setTitle(e.target.value)} />
              </div>
              <div>
                <Label htmlFor="description">Description *</Label>
                <Textarea
                  id="description"
                  placeholder="Describe what students will learn..."
                  rows={4}
                  required
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
              </div>
              <div>
                <Label htmlFor="content">Content</Label>
                <Textarea
                  id="content"
                  placeholder="Detailed content..."
                  rows={6}
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="category">Category *</Label>
                  <Select value={categoryId?.toString() || ''} onValueChange={(v) => setCategoryId(v ? parseInt(v) : null)} required>
                    <SelectTrigger>
                      <SelectValue placeholder="Select category" />
                    </SelectTrigger>
                    <SelectContent>
                      {categories.map((opt) => (
                        <SelectItem key={opt.value} value={opt.value}>
                          {opt.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <Label htmlFor="price">Price ($) *</Label>
                  <Input id="price" type="number" placeholder="99.99" step="0.01" required value={price} onChange={(e) => setPrice(parseFloat(e.target.value))} />
                </div>
              </div>
              <div>
                <Label htmlFor="promotionPrice">Promotion Price ($)</Label>
                <Input id="promotionPrice" type="number" placeholder="79.99" step="0.01" value={promotionPrice} onChange={(e) => setPromotionPrice(parseFloat(e.target.value))} />
              </div>
              <div>
                <Label htmlFor="voucher">Apply Voucher (Optional)</Label>
                <Select value={selectedVoucherId?.toString() || ''} onValueChange={(v) => setSelectedVoucherId(v ? parseInt(v) : null)}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select active voucher" />
                  </SelectTrigger>
                  <SelectContent>
                    {vouchers.map((opt) => (
                      <SelectItem key={opt.value} value={opt.value}>
                        {opt.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Label htmlFor="image">Course Image</Label>
                {image && (
                  <div className="mb-2">
                    <img src={image} alt="Current course image" className="w-48 h-32 object-cover rounded" />
                  </div>
                )}
                <Input
                  id="imageFile"
                  type="file"
                  accept="image/*"
                  onChange={(e) => setImageFile(e.target.files?.[0] || null)}
                />
                <p className="text-sm text-muted-foreground mt-1">Upload a new image to replace the current one (max 5MB).</p>
              </div>
              <div className="flex items-center space-x-2">
                <Switch id="publish" checked={publish} onCheckedChange={setPublish} />
                <Label htmlFor="publish">Publish Course</Label>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Course Curriculum</CardTitle>
              <Button type="button" onClick={addChapter} variant="outline" size="sm">
                <Plus className="mr-2 h-4 w-4" />
                Add Chapter
              </Button>
            </CardHeader>
            <CardContent className="space-y-6">
              {chapters.length === 0 ? (
                <div className="text-center py-12 text-muted-foreground">
                  No chapters yet. Click "Add Chapter" to start building your curriculum.
                </div>
              ) : (
                chapters.map((chapter, chapterIndex) => (
                  <div key={chapter.localId} className="p-4 border rounded-lg space-y-4">
                    <div className="flex items-start gap-4">
                      <div className="flex-1">
                        <Label>Chapter {chapterIndex + 1} Title</Label>
                        <Input
                          placeholder="e.g., Introduction to React Hooks"
                          className="mt-2"
                          value={chapter.title}
                          onChange={(e) => updateChapter(chapter.localId, "title", e.target.value)}
                        />
                      </div>
                      <Button
                        type="button"
                        variant="ghost"
                        size="icon"
                        onClick={() => removeChapter(chapter.localId, chapter.chapterId)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>

                    <div className="pl-4 space-y-3">
                      {chapter.lessons.map((lesson, lessonIndex) => (
                        <div key={lesson.localId} className="flex items-center gap-2">
                          <div className="flex-1 grid grid-cols-4 gap-2">
                            <Input
                              placeholder={`Lesson ${lessonIndex + 1} title`}
                              value={lesson.title}
                              onChange={(e) => updateLesson(chapter.localId, lesson.localId, "title", e.target.value)}
                            />
                            <Input
                              placeholder="Video URL"
                              value={lesson.videoPath}
                              onChange={(e) => updateLesson(chapter.localId, lesson.localId, "videoPath", e.target.value)}
                            />
                            <Input
                              placeholder="Slide URL"
                              value={lesson.slidePath}
                              onChange={(e) => updateLesson(chapter.localId, lesson.localId, "slidePath", e.target.value)}
                            />
                            <Input
                              placeholder="Type (e.g., video)"
                              value={lesson.typeDocument}
                              onChange={(e) => updateLesson(chapter.localId, lesson.localId, "typeDocument", e.target.value)}
                            />
                          </div>
                          <Button
                            type="button"
                            variant="ghost"
                            size="icon"
                            onClick={() => removeLesson(chapter.localId, lesson.localId, lesson.lessonId)}
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      ))}
                      <Button
                        type="button"
                        variant="outline"
                        size="sm"
                        onClick={() => addLesson(chapter.localId)}
                      >
                        <Plus className="mr-2 h-4 w-4" />
                        Add Lesson
                      </Button>
                    </div>
                  </div>
                ))
              )}
            </CardContent>
          </Card>

          <div className="flex justify-end gap-4">
            <Button type="button" variant="outline" onClick={() => navigate("/instructor/courses")}>
              Cancel
            </Button>
            <Button type="submit" disabled={loading}>
              {isEdit ? "Update" : "Create"} Course {publish && "and Publish"}
            </Button>
          </div>
        </form>
      </div>

      <Dialog open={loading}>
        <DialogContent className="sm:max-w-md">
          <div className="flex flex-col items-center justify-center py-8">
            <Loader2 className="h-8 w-8 animate-spin text-primary mb-4" />
            <h3 className="text-lg font-semibold">Saving Course...</h3>
            <p className="text-sm text-muted-foreground mt-2">Please wait while we process your request.</p>
          </div>
        </DialogContent>
      </Dialog>
    </DashboardLayout>
  );
}