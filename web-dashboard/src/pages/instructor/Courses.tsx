import { useState } from "react";
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

interface Course {
  id: number;
  title: string;
  category: string;
  price: number;
  students: number;
  status: "published" | "draft";
  rating: number;
}

const mockCourses: Course[] = [
  {
    id: 1,
    title: "React Advanced Patterns",
    category: "Programming",
    price: 99.99,
    students: 342,
    status: "published",
    rating: 4.8,
  },
  {
    id: 2,
    title: "JavaScript Fundamentals",
    category: "Programming",
    price: 49.99,
    students: 289,
    status: "published",
    rating: 4.6,
  },
  {
    id: 3,
    title: "TypeScript Masterclass",
    category: "Programming",
    price: 79.99,
    students: 216,
    status: "draft",
    rating: 0,
  },
];

export default function Courses() {
  const [courses, setCourses] = useState<Course[]>(mockCourses);
  const { toast } = useToast();
  const navigate = useNavigate();

  const handleDelete = (id: number) => {
    setCourses(courses.filter((c) => c.id !== id));
    toast({
      title: "Course deleted",
      description: "The course has been removed.",
      variant: "destructive",
    });
  };

  return (
    <DashboardLayout role="instructor">
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-3xl font-bold tracking-tight">My Courses</h2>
            <p className="text-muted-foreground">Create and manage your course content</p>
          </div>
          <Button onClick={() => navigate("/instructor/courses/create")}>
            <Plus className="mr-2 h-4 w-4" />
            Create Course
          </Button>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>All Courses</CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Title</TableHead>
                  <TableHead>Category</TableHead>
                  <TableHead>Price</TableHead>
                  <TableHead>Students</TableHead>
                  <TableHead>Rating</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {courses.map((course) => (
                  <TableRow key={course.id}>
                    <TableCell className="font-medium">{course.title}</TableCell>
                    <TableCell>{course.category}</TableCell>
                    <TableCell>${course.price}</TableCell>
                    <TableCell>{course.students}</TableCell>
                    <TableCell>
                      {course.status === "published" ? (
                        <span className="flex items-center gap-1">
                          <span className="text-warning">★</span>
                          {course.rating}
                        </span>
                      ) : (
                        "-"
                      )}
                    </TableCell>
                    <TableCell>
                      <Badge variant={course.status === "published" ? "default" : "secondary"}>
                        {course.status}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-right">
                      <Button variant="ghost" size="icon">
                        <Eye className="h-4 w-4" />
                      </Button>
                      <Button variant="ghost" size="icon">
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button variant="ghost" size="icon" onClick={() => handleDelete(course.id)}>
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
