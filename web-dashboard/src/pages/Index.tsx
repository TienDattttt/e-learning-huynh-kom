import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { GraduationCap, UserCircle, Shield } from "lucide-react";
import { useNavigate } from "react-router-dom";

const Index = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary/10 via-background to-secondary/10">
      <div className="container px-4 py-16">
        <div className="text-center mb-12">
          <div className="flex justify-center mb-6">
            <GraduationCap className="h-16 w-16 text-primary" />
          </div>
          <h1 className="text-4xl md:text-5xl font-bold mb-4">
            Learning Management System
          </h1>
          <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
            Comprehensive platform for managing online courses, instructors, and students
          </p>
        </div>

        <div className="grid md:grid-cols-2 gap-6 max-w-4xl mx-auto">
          <Card className="hover:shadow-lg transition-shadow cursor-pointer" onClick={() => navigate("/admin")}>
            <CardHeader>
              <div className="flex items-center gap-3 mb-2">
                <div className="p-2 rounded-lg bg-primary/10">
                  <Shield className="h-6 w-6 text-primary" />
                </div>
                <CardTitle>Admin Dashboard</CardTitle>
              </div>
              <CardDescription>
                Manage categories, promotions, revenue, and platform users
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li>• Course category management</li>
                <li>• Voucher & promotion control</li>
                <li>• Revenue reports & analytics</li>
                <li>• Student management</li>
              </ul>
              <Button className="w-full mt-4">
                Access Admin Panel
              </Button>
            </CardContent>
          </Card>

          <Card className="hover:shadow-lg transition-shadow cursor-pointer" onClick={() => navigate("/instructor")}>
            <CardHeader>
              <div className="flex items-center gap-3 mb-2">
                <div className="p-2 rounded-lg bg-secondary/10">
                  <UserCircle className="h-6 w-6 text-secondary" />
                </div>
                <CardTitle>Instructor Dashboard</CardTitle>
              </div>
              <CardDescription>
                Create courses, track students, and manage your teaching content
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li>• Course creation & management</li>
                <li>• Student progress tracking</li>
                <li>• Q&A and feedback</li>
                <li>• Revenue analytics</li>
              </ul>
              <Button className="w-full mt-4" variant="secondary">
                Access Instructor Panel
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default Index;
