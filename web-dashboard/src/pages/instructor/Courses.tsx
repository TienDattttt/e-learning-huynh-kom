// Updated Courses.jsx
import { useState, useEffect } from "react";
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
import { Badge } from "@/components/ui/badge";
import { Plus, Edit, Trash2, Eye } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { useNavigate } from "react-router-dom";
import { getMyCourses, deleteCourse } from "@/api/courseApi"; // Adjust path

interface Course {
  courseId: number;
  name: string;
  categoryName: string; // Changed to match updated DTO
  price: number;
  students: number; // Mocked as 0 since not in DTO
  status: boolean;
}

export default function Courses() {
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        setLoading(true);
        const data = await getMyCourses();
        setCourses(data.content || []); // Assuming Page response
      } catch (error) {
        toast({
          title: "Error loading courses",
          description: "Could not fetch your courses.",
          variant: "destructive",
        });
      } finally {
        setLoading(false);
      }
    };
    fetchCourses();
  }, []);

  const handleDelete = async (id: number) => {
    try {
      await deleteCourse(id);
      setCourses(courses.filter((c) => c.courseId !== id));
      toast({
        title: "Course deleted",
        description: "The course has been removed.",
        variant: "destructive",
      });
    } catch (error) {
      toast({
        title: "Error deleting course",
        description: "Could not delete the course.",
        variant: "destructive",
      });
    }
  };

  return (
    <DashboardLayout role="instructor">
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-3xl font-bold tracking-tight">Khóa học của tôi</h2>
            <p className="text-muted-foreground">Tạo và quản lý nội dung khóa học của bạn.</p>
          </div>
          <Button onClick={() => navigate("/instructor/courses/create")}>
            <Plus className="mr-2 h-4 w-4" />
            Tạo mới khóa học
          </Button>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Danh sách khóa học</CardTitle>
          </CardHeader>
          <CardContent>
            {loading ? (
              <div className="text-center py-4">Loading...</div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Tiêu đề</TableHead>
                    <TableHead>Danh mục</TableHead>
                    <TableHead>Giá</TableHead>
                    {/* <TableHead>Students</TableHead> */}
                    <TableHead>Trạng thái</TableHead>
                    <TableHead className="text-center">Hành động</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {courses.map((course) => (
                    <TableRow key={course.courseId}>
                      <TableCell className="font-medium">{course.name}</TableCell>
                      <TableCell>{course.categoryName}</TableCell>
                      <TableCell>{course.price}đ</TableCell>
                      {/* <TableCell>{course.students}</TableCell> */}
                      <TableCell>
                        <Badge variant={course.status ? "default" : "secondary"}>
                          {course.status ? "Đã xuất bản" : "Bản nháp"}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        <Button variant="ghost" size="icon" onClick={() => navigate(`/instructor/courses/${course.courseId}/edit`)}>
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button variant="ghost" size="icon" onClick={() => handleDelete(course.courseId)}>
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  );
}