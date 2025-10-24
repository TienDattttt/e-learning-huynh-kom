import { useState } from "react";
import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger, DialogFooter } from "@/components/ui/dialog";
import { Progress } from "@/components/ui/progress";
import { Search, Eye, AlertTriangle } from "lucide-react";
import { useToast } from "@/components/ui/use-toast";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getStudents, getStudentProgress, sendLockRequest } from "@/api/teacherApi";
import { Skeleton } from "@/components/ui/skeleton";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";

interface Student {
  studentId: number;
  fullName: string;
  email: string;
  courseId: number | null;
  courseName: string | null;
  progressPercent: number;
  lastUpdated: string | null;
}

interface ProgressDetail {
  studentId: number;
  courseId: number;
  courseName: string;
  overallPercent: number;
  lastUpdated: string;
  lessons: {
    lessonId: number;
    lessonName: string;
    progressPercent: number;
    completed: boolean;
    updatedAt: string;
  }[];
}

export default function InstructorStudents() {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedStudent, setSelectedStudent] = useState<Student | null>(null);
  const [reason, setReason] = useState("");
  const [showLockDialog, setShowLockDialog] = useState(false);
  const { toast } = useToast();
  const queryClient = useQueryClient();

  // Fetch list students
  const { data: studentsData, isLoading } = useQuery({
    queryKey: ['students'],
    queryFn: () => getStudents(0, 20),
    select: (data) => data.data.items as Student[], // Extract items
  });

  // Fetch progress when selected (lazy)
  const { data: progressData, isLoading: progressLoading } = useQuery({
    queryKey: ['studentProgress', selectedStudent?.studentId, selectedStudent?.courseId],
    queryFn: () => getStudentProgress(selectedStudent!.studentId, selectedStudent!.courseId!),
    select: (data) => data.data as ProgressDetail,
    enabled: !!selectedStudent && !!selectedStudent.courseId,
  });

  // Mutation for lock request
  const lockMutation = useMutation({
    mutationFn: ({ studentId, reason }: { studentId: number; reason: string }) => sendLockRequest(studentId, reason),
    onSuccess: () => {
      toast({ title: "Success", description: "Lock request sent to admin." });
      setShowLockDialog(false);
      setReason("");
      queryClient.invalidateQueries({ queryKey: ['students'] }); // Refresh list if needed
    },
    onError: (error: any) => {
      toast({ title: "Error", description: error.message || "Failed to send request.", variant: "destructive" });
    },
  });

  const filteredStudents = (studentsData || []).filter(
    (student) =>
      student.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      student.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (student.courseName || "").toLowerCase().includes(searchQuery.toLowerCase())
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
                  <TableHead>Last Updated</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {isLoading ? (
                  [...Array(3)].map((_, i) => (
                    <TableRow key={i}>
                      <TableCell><Skeleton className="h-4 w-[150px]" /></TableCell>
                      <TableCell><Skeleton className="h-4 w-[200px]" /></TableCell>
                      <TableCell><Skeleton className="h-4 w-[100px]" /></TableCell>
                      <TableCell><Skeleton className="h-4 w-[100px]" /></TableCell>
                      <TableCell><Skeleton className="h-4 w-[50px]" /></TableCell>
                    </TableRow>
                  ))
                ) : (
                  filteredStudents.map((student) => (
                    <TableRow key={student.studentId}>
                      <TableCell>
                        <div>
                          <p className="font-medium">{student.fullName}</p>
                          <p className="text-sm text-muted-foreground">{student.email}</p>
                        </div>
                      </TableCell>
                      <TableCell>{student.courseName || 'N/A'}</TableCell>
                      <TableCell>
                        <div className="space-y-1">
                          <div className="flex items-center gap-2">
                            <Progress value={student.progressPercent} className="flex-1" />
                            <span className="text-sm font-medium">{student.progressPercent}%</span>
                          </div>
                        </div>
                      </TableCell>
                      <TableCell>{student.lastUpdated || 'N/A'}</TableCell>
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
                          <DialogContent className="sm:max-w-[600px]">
                            <DialogHeader>
                              <DialogTitle>Student Details: {selectedStudent?.fullName}</DialogTitle>
                            </DialogHeader>
                            {progressLoading ? (
                              <Skeleton className="h-32 w-full" />
                            ) : (
                              <div className="space-y-4">
                                <div>
                                  <Label>Email</Label>
                                  <p>{selectedStudent?.email}</p>
                                </div>
                                <div>
                                  <Label>Course</Label>
                                  <p>{progressData?.courseName}</p>
                                </div>
                                <div>
                                  <Label>Overall Progress</Label>
                                  <Progress value={progressData?.overallPercent} className="mt-2" />
                                  <p className="text-sm text-muted-foreground mt-1">
                                    {progressData?.overallPercent}% - Last updated: {progressData?.lastUpdated}
                                  </p>
                                </div>
                                <div>
                                  <Label>Lessons</Label>
                                  <Table>
                                    <TableHeader>
                                      <TableRow>
                                        <TableHead>Lesson</TableHead>
                                        <TableHead>Progress</TableHead>
                                        <TableHead>Completed</TableHead>
                                        <TableHead>Updated</TableHead>
                                      </TableRow>
                                    </TableHeader>
                                    <TableBody>
                                      {progressData?.lessons.map((lesson) => (
                                        <TableRow key={lesson.lessonId}>
                                          <TableCell>{lesson.lessonName}</TableCell>
                                          <TableCell>{lesson.progressPercent}%</TableCell>
                                          <TableCell>{lesson.completed ? 'Yes' : 'No'}</TableCell>
                                          <TableCell>{lesson.updatedAt}</TableCell>
                                        </TableRow>
                                      ))}
                                    </TableBody>
                                  </Table>
                                </div>
                                <Button
                                  variant="destructive"
                                  onClick={() => setShowLockDialog(true)}
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
                  ))
                )}
              </TableBody>
            </Table>
          </CardContent>
        </Card>

        {/* Lock Request Dialog */}
        <Dialog open={showLockDialog} onOpenChange={setShowLockDialog}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Report Fraud for {selectedStudent?.fullName}</DialogTitle>
            </DialogHeader>
            <div className="space-y-4">
              <Label htmlFor="reason">Reason for Lock Request</Label>
              <Textarea
                id="reason"
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                placeholder="Describe the violation..."
              />
            </div>
            <DialogFooter>
              <Button variant="outline" onClick={() => setShowLockDialog(false)}>Cancel</Button>
              <Button
                variant="destructive"
                onClick={() => lockMutation.mutate({ studentId: selectedStudent!.studentId, reason })}
                disabled={!reason || lockMutation.isPending}
              >
                {lockMutation.isPending ? 'Sending...' : 'Send Request'}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </DashboardLayout>
  );
}