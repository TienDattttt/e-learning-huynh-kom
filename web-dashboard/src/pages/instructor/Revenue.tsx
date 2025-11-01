import { useState } from "react";
import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Download, DollarSign, TrendingUp, ShoppingCart } from "lucide-react";
import { Line, LineChart, Bar, BarChart, ResponsiveContainer, XAxis, YAxis, Tooltip, Legend, CartesianGrid } from "recharts";
import { StatsCard } from "@/components/dashboard/StatsCard";

const monthlyData = [
  { month: "Jan", revenue: 2400, enrollments: 45 },
  { month: "Feb", revenue: 3200, enrollments: 58 },
  { month: "Mar", revenue: 4100, enrollments: 72 },
  { month: "Apr", revenue: 3800, enrollments: 65 },
  { month: "May", revenue: 5200, enrollments: 89 },
  { month: "Jun", revenue: 6100, enrollments: 103 },
];

const courseRevenue = [
  { course: "React Advanced", revenue: 8420, students: 342 },
  { course: "JS Fundamentals", revenue: 6890, students: 289 },
  { course: "TypeScript", revenue: 5210, students: 216 },
];

export default function InstructorRevenue() {
  const [period, setPeriod] = useState("monthly");

  return (
    <DashboardLayout role="instructor">
      <div className="space-y-6 animate-fade-in">
        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-3xl font-bold tracking-tight">Revenue Reports</h2>
            <p className="text-muted-foreground">Track your earnings and course performance</p>
          </div>
          <Button>
            <Download className="mr-2 h-4 w-4" />
            Export Report
          </Button>
        </div>

        <div className="flex gap-4 items-center">
          <Label>Period:</Label>
          <Select value={period} onValueChange={setPeriod}>
            <SelectTrigger className="w-[180px]">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="weekly">Weekly</SelectItem>
              <SelectItem value="monthly">Monthly</SelectItem>
              <SelectItem value="quarterly">Quarterly</SelectItem>
              <SelectItem value="yearly">Yearly</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div className="grid gap-4 md:grid-cols-3">
          <StatsCard
            title="Tổng doanh thu"
            value="$25,720"
            icon={DollarSign}
            trend={{ value: 28.4, isPositive: true }}
            variant="success"
          />
          <StatsCard
            title="Total Enrollments"
            value="532"
            icon={ShoppingCart}
            trend={{ value: 22.1, isPositive: true }}
            variant="primary"
          />
          <StatsCard
            title="Avg. Revenue/Course"
            value="$48.35"
            icon={TrendingUp}
            trend={{ value: 6.7, isPositive: true }}
            variant="success"
          />
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>Revenue Trend</CardTitle>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={monthlyData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                  <XAxis dataKey="month" stroke="hsl(var(--muted-foreground))" fontSize={12} />
                  <YAxis stroke="hsl(var(--muted-foreground))" fontSize={12} />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: "hsl(var(--card))",
                      border: "1px solid hsl(var(--border))",
                      borderRadius: "var(--radius)",
                    }}
                  />
                  <Legend />
                  <Line
                    type="monotone"
                    dataKey="revenue"
                    stroke="hsl(var(--primary))"
                    strokeWidth={2}
                    name="Revenue ($)"
                  />
                  <Line
                    type="monotone"
                    dataKey="enrollments"
                    stroke="hsl(var(--secondary))"
                    strokeWidth={2}
                    name="Enrollments"
                  />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Revenue by Course</CardTitle>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={courseRevenue}>
                  <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                  <XAxis dataKey="course" stroke="hsl(var(--muted-foreground))" fontSize={12} />
                  <YAxis stroke="hsl(var(--muted-foreground))" fontSize={12} />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: "hsl(var(--card))",
                      border: "1px solid hsl(var(--border))",
                      borderRadius: "var(--radius)",
                    }}
                  />
                  <Legend />
                  <Bar dataKey="revenue" fill="hsl(var(--primary))" radius={[8, 8, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Course Performance Details</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {courseRevenue.map((course, index) => (
                <div
                  key={index}
                  className="flex items-center justify-between p-4 bg-muted rounded-lg hover:bg-muted/70 transition-colors"
                >
                  <div>
                    <p className="font-medium">{course.course}</p>
                    <p className="text-sm text-muted-foreground">{course.students} students enrolled</p>
                  </div>
                  <div className="text-right">
                    <p className="text-xl font-bold text-success">${course.revenue.toLocaleString()}</p>
                    <p className="text-xs text-muted-foreground">
                      ${(course.revenue / course.students).toFixed(2)} per student
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  );
}
