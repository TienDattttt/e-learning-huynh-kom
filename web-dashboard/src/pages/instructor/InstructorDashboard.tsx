import { useEffect, useState } from "react";
import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { StatsCard } from "@/components/dashboard/StatsCard";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { BookOpen, Users, DollarSign, Star } from "lucide-react";
import { Line, LineChart, ResponsiveContainer, XAxis, YAxis, Tooltip, Legend } from "recharts";
import { dashboardApi } from "@/api/dashboardApi";

export default function InstructorDashboard() {
  const [summary, setSummary] = useState<{ totalRevenue: number; totalOrders: number } | null>(null);
  const [trend, setTrend] = useState<{ month: number; revenue: number }[]>([]);
  const [topCourses, setTopCourses] = useState<
    { courseName: string; revenue: number; orders: number }[]
  >([]);

  useEffect(() => {
    dashboardApi.getDashboard({ year: new Date().getFullYear() }).then((data) => {
      setSummary(data.summary);
      setTrend(data.trend);
      setTopCourses(data.topCourses);
    });
  }, []);

  return (
    <DashboardLayout role="instructor">
      <div className="space-y-6">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Bảng điều khiển giảng viên</h2>
          <p className="text-muted-foreground">Theo dõi doanh thu và hiệu quả khóa học của bạn.</p>
        </div>

        {/* Tổng quan */}
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <StatsCard
            title="Tất cả khóa học"
            value={topCourses.length.toString()}
            icon={BookOpen}
            trend={{ value: 0, isPositive: true }}
            variant="primary"
          />
          <StatsCard
            title="Tổng số học viên"
            value={summary ? summary.totalOrders.toString() : "..."}
            icon={Users}
            trend={{ value: 0, isPositive: true }}
            variant="success"
          />
          <StatsCard
            title="Tổng doanh thu"
            value={summary ? `${summary.totalRevenue.toLocaleString()}đ` : "..."}
            icon={DollarSign}
            trend={{ value: 0, isPositive: true }}
            variant="success"
          />
          <StatsCard title="Đánh giá trung bình" value="4.8" icon={Star} trend={{ value: 0, isPositive: true }} variant="warning" />
        </div>

        {/* Biểu đồ */}
        <Card>
          <CardHeader>
            <CardTitle>Doanh thu theo tháng</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart
                data={trend.map((d) => ({
                  month: `M${d.month}`,
                  revenue: d.revenue,
                }))}
              >
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="revenue" stroke="hsl(var(--primary))" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* Top Courses */}
        <Card>
          <CardHeader>
            <CardTitle>Các khóa học có hiệu suất cao nhất</CardTitle>
          </CardHeader>
          <CardContent>
            {topCourses.map((course, index) => (
              <div key={index} className="flex justify-between border-b py-2">
                <div>
                  <p className="font-medium">{course.courseName}</p>
                  <p className="text-sm text-muted-foreground">{course.orders} lượt mua</p>
                </div>
               <p className="font-semibold text-success">
  {course.revenue.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}
</p>
              </div>
            ))}
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  );
}
