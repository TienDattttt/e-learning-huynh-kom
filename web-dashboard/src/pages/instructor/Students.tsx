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
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Progress } from "@/components/ui/progress";
import { Search, Eye, AlertTriangle } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

interface StudentProgress {
  id: number;
  name: string;
  email: string;
  course: string;
  progress: number;
  completedLessons: number;
  totalLessons: number;
  lastActive: string;
  enrollDate: string;
}

const mockStudents: StudentProgress[] = [
  {
    id: 1,
    name: "Alice Johnson",
    email: "alice@example.com",
    course: "React Advanced Patterns",
    progress: 75,
    completedLessons: 18,
    totalLessons: 24,
    lastActive: "2024-10-20",
    enrollDate: "2024-09-15",
  },
  {
    id: 2,
    name: "Bob Smith",
    email: "bob@example.com",
    course: "React Advanced Patterns",
    progress: 45,
    completedLessons: 11,
    totalLessons: 24,
    lastActive: "2024-10-18",
    enrollDate: "2024-09-20",
  },
  {
    id: 3,
    name: "Charlie Brown",
    email: "charlie@example.com",
    course: "JavaScript Fundamentals",
    progress: 90,
    completedLessons: 27,
    totalLessons: 30,
    lastActive: "2024-10-22",
    enrollDate: "2024-08-10",
  },
];

export default function InstructorStudents() {
  const [students, setStudents] = useState<StudentProgress[]>(mockStudents);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedStudent, setSelectedStudent] = useState<StudentProgress | null>(null);
  const { toast } = useToast();

  const handleReportFraud = (student: StudentProgress) => {
    toast({
      title: "Fraud report submitted",
      description: `Report for ${student.name} has been sent to admin for review.`,
      variant: "destructive",
    });
  };

  const filteredStudents = students.filter(
    (student) =>
      student.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      student.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      student.course.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <DashboardLayout role="instructor">
      <div className="space-y-6 animate-fade-in">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">My Students</h2>
          <p className="text-muted-foreground">Monitor student progress and engagement</p>
        </div>

        <div className="flex gap-4">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Search students by name, email, or course..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
          </div>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Student Progress ({filteredStudents.length})</CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Student</TableHead>
                  <TableHead>Course</TableHead>
                  <TableHead>Progress</TableHead>
                  <TableHead>Lessons</TableHead>
                  <TableHead>Last Active</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredStudents.map((student) => (
                  <TableRow key={student.id}>
                    <TableCell>
                      <div>
                        <p className="font-medium">{student.name}</p>
                        <p className="text-sm text-muted-foreground">{student.email}</p>
                      </div>
                    </TableCell>
                    <TableCell>{student.course}</TableCell>
                    <TableCell>
                      <div className="space-y-1">
                        <div className="flex items-center gap-2">
                          <Progress value={student.progress} className="flex-1" />
                          <span className="text-sm font-medium">{student.progress}%</span>
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      {student.completedLessons}/{student.totalLessons}
                    </TableCell>
                    <TableCell>{student.lastActive}</TableCell>
                    <TableCell className="text-right">
                      <Dialog>
                        <DialogTrigger asChild>
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => setSelectedStudent(student)}
                          >
                            <Eye className="h-4 w-4" />
                          </Button>
                        </DialogTrigger>
                        <DialogContent>
                          <DialogHeader>
                            <DialogTitle>Student Details</DialogTitle>
                          </DialogHeader>
                          {selectedStudent && (
                            <div className="space-y-4">
                              <div>
                                <Label className="text-sm font-medium">Name</Label>
                                <p>{selectedStudent.name}</p>
                              </div>
                              <div>
                                <Label className="text-sm font-medium">Email</Label>
                                <p>{selectedStudent.email}</p>
                              </div>
                              <div>
                                <Label className="text-sm font-medium">Course</Label>
                                <p>{selectedStudent.course}</p>
                              </div>
                              <div>
                                <Label className="text-sm font-medium">Enroll Date</Label>
                                <p>{selectedStudent.enrollDate}</p>
                              </div>
                              <div>
                                <Label className="text-sm font-medium">Progress</Label>
                                <Progress value={selectedStudent.progress} className="mt-2" />
                                <p className="text-sm text-muted-foreground mt-1">
                                  {selectedStudent.completedLessons} of {selectedStudent.totalLessons}{" "}
                                  lessons completed
                                </p>
                              </div>
                              <Button
                                variant="destructive"
                                className="w-full"
                                onClick={() => handleReportFraud(selectedStudent)}
                              >
                                <AlertTriangle className="mr-2 h-4 w-4" />
                                Report Fraud to Admin
                              </Button>
                            </div>
                          )}
                        </DialogContent>
                      </Dialog>
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

function Label({ children, className }: { children: React.ReactNode; className?: string }) {
  return <label className={`text-sm font-medium ${className}`}>{children}</label>;
}
