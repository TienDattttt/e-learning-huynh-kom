// src/pages/admin/Categories.tsx
import { useEffect, useState } from "react";
import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Plus, Edit, Trash2 } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { getCategoryTree, saveCategory, CategoryNode, SaveCategoryRequest } from "@/api/categoryApi";

interface FlatCategory {
  id: number;
  name: string;
  displayName: string;
  parentCategory: string | null;
  parentId: number | null;
  sortOrder: number | null;
  status: boolean;
}

interface SelectOption {
  value: string;
  label: string;
}

const formSchema = z.object({
  name: z.string().min(1, { message: "Category name is required" }),
  parentId: z.string().optional(),
  sortOrder: z.number().optional(),
});

export default function Categories() {
  const [tree, setTree] = useState<CategoryNode[]>([]);
  const [categories, setCategories] = useState<FlatCategory[]>([]);
  const [options, setOptions] = useState<SelectOption[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [currentCategory, setCurrentCategory] = useState<FlatCategory | null>(null);
  const { toast } = useToast();

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: "",
      parentId: "none",
      sortOrder: 0,
    },
  });

  const fetchTree = async () => {
    try {
      const data = await getCategoryTree();
      setTree(data);
      const flat = flattenCategories(data);
      setCategories(flat);
      const opts = buildCategoryOptions(data);
      setOptions([{ value: "none", label: "Không có (Danh mục gốc)" }, ...opts]);
    } catch (error) {
      toast({
        title: "Lỗi",
        description: "Đã xảy ra lỗi khi tải danh mục.",
        variant: "destructive",
      });
    }
  };

  useEffect(() => {
    fetchTree();
  }, []);

  useEffect(() => {
    if (currentCategory) {
      form.reset({
        name: currentCategory.name,
        parentId: currentCategory.parentId ? currentCategory.parentId.toString() : "none",
        sortOrder: currentCategory.sortOrder ?? 0,
      });
    } else {
      form.reset({
        name: "",
        parentId: "none",
        sortOrder: 0,
      });
    }
  }, [currentCategory, form]);

  const flattenCategories = (nodes: CategoryNode[], level: number = 0, parentCategory: string | null = null): FlatCategory[] => {
    let result: FlatCategory[] = [];
    for (const node of nodes) {
      const displayName = level > 0 ? '  '.repeat(level) + '└─ ' + node.name : node.name;
      result.push({
        id: node.id,
        name: node.name,
        displayName,
        parentCategory,
        parentId: node.parentId,
        sortOrder: node.sortOrder,
        status: node.status,
      });
      if (node.children.length > 0) {
        result = result.concat(flattenCategories(node.children, level + 1, node.name));
      }
    }
    return result;
  };

  const buildCategoryOptions = (nodes: CategoryNode[], indent: string = ""): SelectOption[] => {
    let result: SelectOption[] = [];
    for (const node of nodes) {
      result.push({ value: node.id.toString(), label: indent + node.name });
      if (node.children.length > 0) {
        result = result.concat(buildCategoryOptions(node.children, indent + "── "));
      }
    }
    return result;
  };

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    try {
      const parentId = values.parentId === "none" ? null : parseInt(values.parentId);
      const req: SaveCategoryRequest = {
        courseCategoryId: currentCategory?.id ?? null,
        name: values.name,
        parentId,
        sortOrder: values.sortOrder ?? null,
        status: true,
      };
      await saveCategory(req);
      toast({
        title: currentCategory ? "Đã cập nhật danh mục" : "Đã thêm danh mục",
        description: "Danh mục đã được lưu thành công.",
      });
      setIsOpen(false);
      setCurrentCategory(null);
      fetchTree();
    } catch (error) {
      toast({
        title: "Lỗi",
        description: "Lỗi khi lưu danh mục",
        variant: "destructive",
      });
    }
  };

  const handleDelete = async (cat: FlatCategory) => {
    try {
      const req: SaveCategoryRequest = {
        courseCategoryId: cat.id,
        name: cat.name,
        parentId: cat.parentId,
        sortOrder: cat.sortOrder,
        status: false,
      };
      await saveCategory(req);
      toast({
        title: "Đã hóa danh mục",
        description: "Danh mục đã được xóa.",
        variant: "destructive",
      });
      fetchTree();
    } catch (error) {
      toast({
        title: "Lỗi",
        description: "Lỗi khi xóa danh mục.",
        variant: "destructive",
      });
    }
  };

  const openAddDialog = () => {
    setCurrentCategory(null);
    setIsOpen(true);
  };

  const openEditDialog = (cat: FlatCategory) => {
    setCurrentCategory(cat);
    setIsOpen(true);
  };

  return (
    <DashboardLayout role="admin">
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-3xl font-bold tracking-tight">Danh mục khóa học</h2>
            <p className="text-muted-foreground">Quản lý danh mục khóa học</p>
          </div>
          <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogTrigger asChild>
              <Button onClick={openAddDialog}>
                <Plus className="mr-2 h-4 w-4" />
                Thêm mới danh mục
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>{currentCategory ? "Chỉnh sửa danh mục" : "Thêm mới danh mục"}</DialogTitle>
              </DialogHeader>
              <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                  <FormField
                    control={form.control}
                    name="name"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Tên danh mục</FormLabel>
                        <FormControl>
                          <Input placeholder="e.g., Web Development" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="parentId"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Lựa chọn danh mục</FormLabel>
                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                          <FormControl>
                            <SelectTrigger>
                              <SelectValue placeholder="Chọn danh mục cha" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            {options.map((opt) => (
                              <SelectItem key={opt.value} value={opt.value}>
                                {opt.label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="sortOrder"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Thứ tự sắp xếp (Không bắt buộc)</FormLabel>
                        <FormControl>
                          <Input
                            type="number"
                            placeholder="0"
                            {...field}
                            onChange={(e) => field.onChange(e.target.value ? parseInt(e.target.value) : undefined)}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <Button type="submit" className="w-full">
                    {currentCategory ? "Cập nhật danh mục" : "Thêm mới danh mục"}
                  </Button>
                </form>
              </Form>
            </DialogContent>
          </Dialog>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Tất cả khóa học</CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Tên</TableHead>
                  <TableHead>Danh mục cha</TableHead>
                  <TableHead className="text-right">Hành động</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {categories.map((category) => (
                  <TableRow key={category.id}>
                    <TableCell className="font-medium">{category.displayName}</TableCell>
                    <TableCell>{category.parentCategory || "Root"}</TableCell>
                    <TableCell className="text-right">
                      <Button variant="ghost" size="icon" onClick={() => openEditDialog(category)}>
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => handleDelete(category)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  );
}