import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import { AlertTriangle, UserCheck, Ban } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { getPendingLockRequests, updateLockRequestStatus } from "@/api/adminApi";
import { Skeleton } from "@/components/ui/skeleton";

interface LockRequest {
  RequestId: number;
  TeacherName: string;
  StudentName: string;
  StudentEmail: string;
  Reason: string;
  CreatedAt: string;
  Status: string;
}

export default function StudentsAdmin() {
  const { toast } = useToast();
  const queryClient = useQueryClient();
  const [tab, setTab] = useState("pending");

  // 🔹 Load danh sách yêu cầu khóa
  const { data, isLoading } = useQuery({
    queryKey: ["lockRequests"],
    queryFn: getPendingLockRequests,
    select: (res) => res.data as LockRequest[],
  });

  // 🔹 Mutation duyệt / từ chối
  const mutation = useMutation({
    mutationFn: ({ id, status }: { id: number; status: "APPROVED" | "REJECTED" }) =>
      updateLockRequestStatus(id, status),
    onSuccess: () => {
      toast({ title: "Success", description: "Lock request updated successfully." });
      queryClient.invalidateQueries({ queryKey: ["lockRequests"] });
    },
    onError: (error: any) => {
      toast({ title: "Error", description: error.message || "Update failed.", variant: "destructive" });
    },
  });

  return (
    <DashboardLayout role="admin">
      <div className="space-y-6 animate-fade-in">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Quản lý học viên</h2>
          <p className="text-muted-foreground">
            Xem và quản lý các yêu cầu khóa sinh viên do giảng viên gửi lên
          </p>
        </div>

        <Tabs value={tab} onValueChange={setTab}>
          <TabsList>
            <TabsTrigger value="pending">⏳ Yêu cầu đang chờ xử lý</TabsTrigger>
          </TabsList>

          <TabsContent value="pending">
            <Card>
              <CardHeader>
                <CardTitle>Yêu cầu khóa sinh viên chờ duyệt</CardTitle>
              </CardHeader>
              <CardContent>
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Giảng viên</TableHead>
                      <TableHead>Học viên</TableHead>
                      <TableHead>Email</TableHead>
                      <TableHead>Lý do</TableHead>
                      <TableHead>Ngày</TableHead>
                      <TableHead>Trạng thái</TableHead>
                      <TableHead className="text-right">Hành động</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {isLoading ? (
                      [...Array(3)].map((_, i) => (
                        <TableRow key={i}>
                          {[...Array(6)].map((__, j) => (
                            <TableCell key={j}>
                              <Skeleton className="h-4 w-[120px]" />
                            </TableCell>
                          ))}
                        </TableRow>
                      ))
                    ) : (data?.length || 0) === 0 ? (
                      <TableRow>
                        <TableCell colSpan={7} className="text-center text-muted-foreground">
                          No pending requests
                        </TableCell>
                      </TableRow>
                    ) : (
                      data!.map((req) => (
                        <TableRow key={req.RequestId}>
                          <TableCell>{req.TeacherName}</TableCell>
                          <TableCell>{req.StudentName}</TableCell>
                          <TableCell>{req.StudentEmail}</TableCell>
                          <TableCell className="max-w-[250px] truncate">{req.Reason}</TableCell>
                          <TableCell>{new Date(req.CreatedAt).toLocaleString()}</TableCell>
                          <TableCell>
                            <Badge variant="outline">{req.Status}</Badge>
                          </TableCell>
                          <TableCell className="text-right space-x-2">
                            <Button
                              variant="destructive"
                              size="sm"
                              onClick={() =>
                                mutation.mutate({ id: req.RequestId, status: "APPROVED" })
                              }
                              disabled={mutation.isPending}
                            >
                              <Ban className="mr-2 h-4 w-4" /> Phê duyệt khóa
                            </Button>
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() =>
                                mutation.mutate({ id: req.RequestId, status: "REJECTED" })
                              }
                              disabled={mutation.isPending}
                            >
                              <UserCheck className="mr-2 h-4 w-4" /> Hủy yêu cầu
                            </Button>
                          </TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </DashboardLayout>
  );
}
