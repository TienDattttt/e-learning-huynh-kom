import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { StatsCard } from "@/components/dashboard/StatsCard";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Users, BookOpen, DollarSign, TrendingUp } from "lucide-react";
import { Bar, BarChart, ResponsiveContainer, XAxis, YAxis, Tooltip, Legend } from "recharts";

const revenueData = [
  { month: "Jan", revenue: 4000, courses: 24 },
  { month: "Feb", revenue: 3000, courses: 18 },
  { month: "Mar", revenue: 5000, courses: 32 },
  { month: "Apr", revenue: 4500, courses: 28 },
  { month: "May", revenue: 6000, courses: 40 },
  { month: "Jun", revenue: 5500, courses: 35 },
];

export default function AdminDashboard() {
  return (
    <DashboardLayout role="admin">
      <div className="space-y-6">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Dashboard Overview</h2>
          <p className="text-muted-foreground">Monitor your platform's performance and metrics</p>
        </div>

        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <StatsCard
            title="Total Students"
            value="2,543"
            icon={Users}
            trend={{ value: 12.5, isPositive: true }}
            variant="primary"
          />
          <StatsCard
            title="Total Courses"
            value="156"
            icon={BookOpen}
            trend={{ value: 8.2, isPositive: true }}
            variant="success"
          />
          <StatsCard
            title="Total Revenue"
            value="$45,231"
            icon={DollarSign}
            trend={{ value: 15.3, isPositive: true }}
            variant="success"
          />
          <StatsCard
            title="Active Instructors"
            value="42"
            icon={TrendingUp}
            trend={{ value: 5.1, isPositive: true }}
            variant="primary"
          />
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>Revenue Overview</CardTitle>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={revenueData}>
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
                  <Bar dataKey="revenue" fill="hsl(var(--primary))" radius={[8, 8, 0, 0]} />
                  <Bar dataKey="courses" fill="hsl(var(--secondary))" radius={[8, 8, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Recent Activities</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {[
                  { action: "New course published", instructor: "John Doe", time: "2 hours ago" },
                  { action: "Student enrolled", course: "React Mastery", time: "4 hours ago" },
                  { action: "Revenue milestone", amount: "$45K", time: "1 day ago" },
                  { action: "New instructor joined", name: "Jane Smith", time: "2 days ago" },
                ].map((activity, index) => (
                  <div key={index} className="flex items-center">
                    <div className="flex-1">
                      <p className="text-sm font-medium">{activity.action}</p>
                      <p className="text-xs text-muted-foreground">
                        {Object.values(activity)[1]} • {activity.time}
                      </p>
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
