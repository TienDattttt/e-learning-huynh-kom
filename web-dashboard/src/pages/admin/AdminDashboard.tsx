import { useEffect, useState } from "react";
import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { StatsCard } from "@/components/dashboard/StatsCard";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Users, BookOpen, DollarSign, TrendingUp } from "lucide-react";
import { Bar, BarChart, ResponsiveContainer, XAxis, YAxis, Tooltip, Legend } from "recharts";
import { dashboardApi } from "@/api/dashboardApi";

export default function AdminDashboard() {
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
    <DashboardLayout role="admin">
      <div className="space-y-6">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Tổng quan bảng điều khiển</h2>
          <p className="text-muted-foreground">Giám sát hiệu suất và các chỉ số của nền tảng</p>
        </div>

        {/* ✅ Thống kê tổng quan */}
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <StatsCard

            title="Tổng số đơn hàng"
            value={summary ? summary.totalOrders.toLocaleString() : "..."}
            icon={Users}
            trend={{ value: 0, isPositive: true }}
            variant="primary"
          />
          <StatsCard
            title="Tổng doanh thu"
            value={summary ? `$${summary.totalRevenue.toLocaleString()}` : "..."}
            icon={DollarSign}
            trend={{ value: 0, isPositive: true }}
            variant="success"
          />
          <StatsCard
            title="Khóa học đang diễn ra"
            value={topCourses.length.toString()}
            icon={BookOpen}
            trend={{ value: 0, isPositive: true }}
            variant="success"
          />
          <StatsCard
            title="Giảng viên"
            value="N/A"
            icon={TrendingUp}
            trend={{ value: 0, isPositive: true }}
            variant="primary"
          />
        </div>
        <div className="grid gap-4 md:grid-cols-2">
        {/* ✅ Biểu đồ doanh thu */}
        <Card>
          <CardHeader>
            <CardTitle>Tổng quan doanh thu</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart
                data={trend.map((d) => ({
                  month: `M${d.month}`,
                  revenue: d.revenue,
                }))}
              >
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="revenue" fill="hsl(var(--primary))" radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* ✅ Top khóa học */}
        <Card>
          <CardHeader>
            <CardTitle>Các khóa học có doanh thu cao nhất</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {topCourses.map((c, i) => (
                <div key={i} className="flex justify-between border-b pb-2">
                  <span className="font-medium">{c.courseName}</span>
                  <span className="text-success font-semibold">${c.revenue.toLocaleString()}</span>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  );
}
