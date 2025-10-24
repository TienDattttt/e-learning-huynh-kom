import { useState } from "react";
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
import { Plus, Trash2, Upload } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { useNavigate } from "react-router-dom";

interface Lesson {
  id: number;
  title: string;
  videoUrl: string;
}

interface Chapter {
  id: number;
  title: string;
  lessons: Lesson[];
}

export default function CreateCourse() {
  const [chapters, setChapters] = useState<Chapter[]>([]);
  const { toast } = useToast();
  const navigate = useNavigate();

  const addChapter = () => {
    setChapters([
      ...chapters,
      {
        id: Date.now(),
        title: "",
        lessons: [],
      },
    ]);
  };

  const addLesson = (chapterId: number) => {
    setChapters(
      chapters.map((chapter) =>
        chapter.id === chapterId
          ? {
              ...chapter,
              lessons: [
                ...chapter.lessons,
                { id: Date.now(), title: "", videoUrl: "" },
              ],
            }
          : chapter
      )
    );
  };

  const removeChapter = (chapterId: number) => {
    setChapters(chapters.filter((c) => c.id !== chapterId));
  };

  const removeLesson = (chapterId: number, lessonId: number) => {
    setChapters(
      chapters.map((chapter) =>
        chapter.id === chapterId
          ? {
              ...chapter,
              lessons: chapter.lessons.filter((l) => l.id !== lessonId),
            }
          : chapter
      )
    );
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    toast({
      title: "Course created successfully",
      description: "Your course has been published and is now available to students.",
    });
    navigate("/instructor/courses");
  };

  return (
    <DashboardLayout role="instructor">
      <div className="max-w-4xl animate-fade-in">
        <div className="mb-6">
          <h2 className="text-3xl font-bold tracking-tight">Create New Course</h2>
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
                <Input id="title" placeholder="e.g., React Advanced Patterns" required />
              </div>
              <div>
                <Label htmlFor="description">Description *</Label>
                <Textarea
                  id="description"
                  placeholder="Describe what students will learn..."
                  rows={4}
                  required
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="category">Category *</Label>
                  <Select required>
                    <SelectTrigger>
                      <SelectValue placeholder="Select category" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="programming">Programming</SelectItem>
                      <SelectItem value="design">Design</SelectItem>
                      <SelectItem value="business">Business</SelectItem>
                      <SelectItem value="marketing">Marketing</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <Label htmlFor="price">Price ($) *</Label>
                  <Input id="price" type="number" placeholder="99.99" step="0.01" required />
                </div>
              </div>
              <div>
                <Label htmlFor="thumbnail">Course Thumbnail</Label>
                <div className="mt-2 flex items-center gap-4">
                  <Button type="button" variant="outline">
                    <Upload className="mr-2 h-4 w-4" />
                    Upload Image
                  </Button>
                  <span className="text-sm text-muted-foreground">
                    Recommended: 1280x720px, max 5MB
                  </span>
                </div>
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
                  <div key={chapter.id} className="p-4 border rounded-lg space-y-4">
                    <div className="flex items-start gap-4">
                      <div className="flex-1">
                        <Label>Chapter {chapterIndex + 1} Title</Label>
                        <Input
                          placeholder="e.g., Introduction to React Hooks"
                          className="mt-2"
                        />
                      </div>
                      <Button
                        type="button"
                        variant="ghost"
                        size="icon"
                        onClick={() => removeChapter(chapter.id)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>

                    <div className="pl-4 space-y-3">
                      {chapter.lessons.map((lesson, lessonIndex) => (
                        <div key={lesson.id} className="flex items-center gap-2">
                          <div className="flex-1 grid grid-cols-2 gap-2">
                            <Input
                              placeholder={`Lesson ${lessonIndex + 1} title`}
                            />
                            <div className="flex gap-2">
                              <Input placeholder="Video URL" />
                              <Button
                                type="button"
                                variant="outline"
                                size="sm"
                              >
                                <Upload className="h-4 w-4" />
                              </Button>
                            </div>
                          </div>
                          <Button
                            type="button"
                            variant="ghost"
                            size="icon"
                            onClick={() => removeLesson(chapter.id, lesson.id)}
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      ))}
                      <Button
                        type="button"
                        variant="outline"
                        size="sm"
                        onClick={() => addLesson(chapter.id)}
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
            <Button type="submit">Publish Course</Button>
          </div>
        </form>
      </div>
    </DashboardLayout>
  );
}
