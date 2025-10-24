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
import { Download, TrendingUp, DollarSign, ShoppingCart, Users } from "lucide-react";
import { Bar, BarChart, Line, LineChart, ResponsiveContainer, XAxis, YAxis, Tooltip, Legend, CartesianGrid } from "recharts";
import { StatsCard } from "@/components/dashboard/StatsCard";

const monthlyData = [
  { month: "Jan", revenue: 12400, transactions: 145, students: 289 },
  { month: "Feb", revenue: 15600, transactions: 178, students: 324 },
  { month: "Mar", revenue: 18900, transactions: 210, students: 378 },
  { month: "Apr", revenue: 16700, transactions: 195, students: 356 },
  { month: "May", revenue: 21300, transactions: 245, students: 421 },
  { month: "Jun", revenue: 23800, transactions: 267, students: 467 },
];

const topCourses = [
  { title: "React Advanced Patterns", revenue: 8420, enrollments: 342, instructor: "John Doe" },
  { title: "Python for Data Science", revenue: 7650, enrollments: 298, instructor: "Jane Smith" },
  { title: "JavaScript Fundamentals", revenue: 6890, enrollments: 289, instructor: "John Doe" },
  { title: "AWS Cloud Practitioner", revenue: 5430, enrollments: 216, instructor: "Mike Johnson" },
  { title: "UI/UX Design Basics", revenue: 4820, enrollments: 198, instructor: "Sarah Wilson" },
];

export default function Revenue() {
  const [period, setPeriod] = useState("monthly");

  return (
    <DashboardLayout role="admin">
      <div className="space-y-6 animate-fade-in">
        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-3xl font-bold tracking-tight">Revenue Reports</h2>
            <p className="text-muted-foreground">Track platform revenue and performance metrics</p>
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
              <SelectItem value="daily">Daily</SelectItem>
              <SelectItem value="weekly">Weekly</SelectItem>
              <SelectItem value="monthly">Monthly</SelectItem>
              <SelectItem value="quarterly">Quarterly</SelectItem>
              <SelectItem value="yearly">Yearly</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <StatsCard
            title="Total Revenue"
            value="$108,770"
            icon={DollarSign}
            trend={{ value: 23.5, isPositive: true }}
            variant="success"
          />
          <StatsCard
            title="Total Transactions"
            value="1,240"
            icon={ShoppingCart}
            trend={{ value: 18.2, isPositive: true }}
            variant="primary"
          />
          <StatsCard
            title="New Students"
            value="2,235"
            icon={Users}
            trend={{ value: 15.8, isPositive: true }}
            variant="primary"
          />
          <StatsCard
            title="Avg. Transaction"
            value="$87.72"
            icon={TrendingUp}
            trend={{ value: 4.3, isPositive: true }}
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
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Transactions & Students</CardTitle>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={monthlyData}>
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
                  <Bar dataKey="transactions" fill="hsl(var(--primary))" radius={[8, 8, 0, 0]} />
                  <Bar dataKey="students" fill="hsl(var(--secondary))" radius={[8, 8, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Top Performing Courses</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {topCourses.map((course, index) => (
                <div
                  key={index}
                  className="flex items-center justify-between p-4 bg-muted rounded-lg hover:bg-muted/70 transition-colors"
                >
                  <div className="flex items-center gap-4">
                    <div className="flex items-center justify-center w-10 h-10 rounded-full bg-primary text-primary-foreground font-bold">
                      {index + 1}
                    </div>
                    <div>
                      <p className="font-medium">{course.title}</p>
                      <p className="text-sm text-muted-foreground">
                        by {course.instructor} • {course.enrollments} enrollments
                      </p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-xl font-bold text-success">${course.revenue.toLocaleString()}</p>
                    <p className="text-xs text-muted-foreground">Total revenue</p>
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
