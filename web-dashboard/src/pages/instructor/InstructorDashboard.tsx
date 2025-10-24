import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { StatsCard } from "@/components/dashboard/StatsCard";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { BookOpen, Users, DollarSign, Star } from "lucide-react";
import { Line, LineChart, ResponsiveContainer, XAxis, YAxis, Tooltip, Legend } from "recharts";

const enrollmentData = [
  { week: "Week 1", enrollments: 20 },
  { week: "Week 2", enrollments: 35 },
  { week: "Week 3", enrollments: 28 },
  { week: "Week 4", enrollments: 42 },
];

export default function InstructorDashboard() {
  return (
    <DashboardLayout role="instructor">
      <div className="space-y-6">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Instructor Dashboard</h2>
          <p className="text-muted-foreground">Track your courses and student engagement</p>
        </div>

        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <StatsCard
            title="My Courses"
            value="12"
            icon={BookOpen}
            trend={{ value: 16.7, isPositive: true }}
            variant="primary"
          />
          <StatsCard
            title="Total Students"
            value="847"
            icon={Users}
            trend={{ value: 22.4, isPositive: true }}
            variant="success"
          />
          <StatsCard
            title="Monthly Revenue"
            value="$8,420"
            icon={DollarSign}
            trend={{ value: 18.9, isPositive: true }}
            variant="success"
          />
          <StatsCard
            title="Avg. Rating"
            value="4.8"
            icon={Star}
            trend={{ value: 3.2, isPositive: true }}
            variant="warning"
          />
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>Student Enrollments</CardTitle>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={enrollmentData}>
                  <XAxis dataKey="week" stroke="hsl(var(--muted-foreground))" fontSize={12} />
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
                    dataKey="enrollments"
                    stroke="hsl(var(--primary))"
                    strokeWidth={2}
                  />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Top Performing Courses</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {[
                  { title: "React Advanced Patterns", students: 342, revenue: "$3,420" },
                  { title: "JavaScript Fundamentals", students: 289, revenue: "$2,890" },
                  { title: "TypeScript Masterclass", students: 216, revenue: "$2,110" },
                ].map((course, index) => (
                  <div key={index} className="flex items-center justify-between p-3 bg-muted rounded-lg">
                    <div>
                      <p className="font-medium">{course.title}</p>
                      <p className="text-sm text-muted-foreground">{course.students} students</p>
                    </div>
                    <div className="text-right">
                      <p className="font-bold text-success">{course.revenue}</p>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </DashboardLayout>
  );
}
