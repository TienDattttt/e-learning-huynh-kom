import { useState } from "react";
import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { Search, Eye, Ban, UserCheck } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

interface Student {
  id: number;
  name: string;
  email: string;
  enrolledCourses: number;
  completedCourses: number;
  totalSpent: number;
  joinDate: string;
  status: "active" | "blocked";
}

const mockStudents: Student[] = [
  {
    id: 1,
    name: "Alice Johnson",
    email: "alice@example.com",
    enrolledCourses: 5,
    completedCourses: 3,
    totalSpent: 450,
    joinDate: "2024-01-15",
    status: "active",
  },
  {
    id: 2,
    name: "Bob Smith",
    email: "bob@example.com",
    enrolledCourses: 8,
    completedCourses: 6,
    totalSpent: 720,
    joinDate: "2023-11-20",
    status: "active",
  },
  {
    id: 3,
    name: "Charlie Brown",
    email: "charlie@example.com",
    enrolledCourses: 3,
    completedCourses: 1,
    totalSpent: 280,
    joinDate: "2024-03-10",
    status: "blocked",
  },
];

export default function Students() {
  const [students, setStudents] = useState<Student[]>(mockStudents);
  const [searchQuery, setSearchQuery] = useState("");
  const { toast } = useToast();

  const handleBlockStudent = (id: number) => {
    setStudents(
      students.map((s) =>
        s.id === id ? { ...s, status: s.status === "active" ? "blocked" : "active" } : s
      )
    );
    toast({
      title: "Student status updated",
      description: "The student account status has been changed.",
    });
  };

  const filteredStudents = students.filter(
    (student) =>
      student.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      student.email.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <DashboardLayout role="admin">
      <div className="space-y-6 animate-fade-in">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Quản lý học viên</h2>
        </div>

        <div className="flex gap-4">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Tìm kiếm..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
          </div>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Tất cả học sinh({filteredStudents.length})</CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Họ tên </TableHead>
                  <TableHead>Email</TableHead>
                  <TableHead>Đã đăng ký</TableHead>
                  <TableHead>Đã hoàn thành</TableHead>
                  <TableHead>Tổng chi tiêu</TableHead>
                  <TableHead>Ngày tham gia</TableHead>
                  <TableHead>Trạng thái</TableHead>
                  <TableHead className="text-right">Hoạt động</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredStudents.map((student) => (
                  <TableRow key={student.id}>
                    <TableCell className="font-medium">{student.name}</TableCell>
                    <TableCell>{student.email}</TableCell>
                    <TableCell>{student.enrolledCourses}</TableCell>
                    <TableCell>{student.completedCourses}</TableCell>
                    <TableCell>${student.totalSpent}</TableCell>
                    <TableCell>{student.joinDate}</TableCell>
                    <TableCell>
                      <Badge variant={student.status === "active" ? "default" : "destructive"}>
                        {student.status}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-right">
                      <Button variant="ghost" size="icon">
                        <Eye className="h-4 w-4" />
                      </Button>
                      <AlertDialog>
                        <AlertDialogTrigger asChild>
                          <Button variant="ghost" size="icon">
                            {student.status === "active" ? (
                              <Ban className="h-4 w-4 text-destructive" />
                            ) : (
                              <UserCheck className="h-4 w-4 text-success" />
                            )}
                          </Button>
                        </AlertDialogTrigger>
                        <AlertDialogContent>
                          <AlertDialogHeader>
                            <AlertDialogTitle>
                              {student.status === "active" ? "Block Student" : "Unblock Student"}
                            </AlertDialogTitle>
                            <AlertDialogDescription>
                              Are you sure you want to{" "}
                              {student.status === "active" ? "block" : "unblock"} {student.name}?
                              {student.status === "active" &&
                                " They will no longer be able to access their courses."}
                            </AlertDialogDescription>
                          </AlertDialogHeader>
                          <AlertDialogFooter>
                            <AlertDialogCancel>Cancel</AlertDialogCancel>
                            <AlertDialogAction onClick={() => handleBlockStudent(student.id)}>
                              Confirm
                            </AlertDialogAction>
                          </AlertDialogFooter>
                        </AlertDialogContent>
                      </AlertDialog>
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
