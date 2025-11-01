import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { GraduationCap, UserCircle, Shield } from "lucide-react";
import { useNavigate, Link } from "react-router-dom";

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
            Nền tảng toàn diện để quản lý các khóa học trực tuyến, giảng viên và sinh viên.
          </p>
        </div>

        <div className="grid md:grid-cols-2 gap-6 max-w-4xl mx-auto">
          <Card className="hover:shadow-lg transition-shadow cursor-pointer" onClick={() => navigate("/admin/categories")}>
            <CardHeader>
              <div className="flex items-center gap-3 mb-2">
                <div className="p-2 rounded-lg bg-primary/10">
                  <Shield className="h-6 w-6 text-primary" />
                </div>
                <CardTitle>Trang quản lý quản trị viên</CardTitle>
              </div>
              <CardDescription>
                Quản lý danh mục, khuyến mãi, doanh thu và người dùng trên nền tảng.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li>• Quản lý danh mục khóa học</li>
                <li>• Quản lý voucher và chương trình khuyến mãi</li>
                <li>• Thống kê và phân tích doanh thu</li>
                <li>• Quản lý học viên</li>
              </ul>
              <Button className="w-full mt-4">
                Truy cập bảng điều khiển quản trị viên
              </Button>
            </CardContent>
          </Card>

          <Card className="hover:shadow-lg transition-shadow cursor-pointer" onClick={() => navigate("/instructor")}>
            <CardHeader>
              <div className="flex items-center gap-3 mb-2">
                <div className="p-2 rounded-lg bg-secondary/10">
                  <UserCircle className="h-6 w-6 text-secondary" />
                </div>
                <CardTitle>Trang quản lý giảng viên</CardTitle>
              </div>
              <CardDescription>
                Tạo khóa học, theo dõi sinh viên và quản lý nội dung giảng dạy của bạn.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li>• Quản lý và tạo mới khóa học</li>
                <li>• Quản lý tiến độ sinh viên</li>
                <li>• Thống kê doanh thu</li>
              </ul>
              <Button className="w-full mt-4" variant="secondary">
                Truy cập bảng điều khiển giảng viên
              </Button>
            </CardContent>
          </Card>
        </div>
        <div className="text-center mt-8">
          <p className="text-muted-foreground">
            Bạn đã sẵn sàng bắt đầu chưa? <Link to="/register" className="text-primary hover:underline">Đăng ký</Link> or <Link to="/login" className="text-primary hover:underline">Đăng nhập</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Index;
