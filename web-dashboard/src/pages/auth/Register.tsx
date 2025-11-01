import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "@/components/ui/use-toast"; // Để show error/success
import { register } from "@/api/authApi"; // Import API

const formSchema = z.object({
  fullname: z.string().min(2, { message: "Họ và tên phải có ít nhất 2 ký tự." }),
  email: z.string().email({ message: "Địa chỉ email không hợp lệ." }),
  password: z.string().min(8, { message: "Mật khẩu phải có ít nhất 8 ký tự." }),
});

export default function Register() {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      fullname: "",
      email: "",
      password: "",
    },
  });

  const navigate = useNavigate();

async function onSubmit(values: z.infer<typeof formSchema>) {
  try {
    const response = await register({
      fullName: values.fullname,
      email: values.email,
      password: values.password,
      role: "GiangVien",
    });
    if (response.success) {
      toast({ title: "Thành công", description: response.message || "Đăng ký thành công!" });
      setTimeout(() => {
        navigate("/login");
      }, 500); // Delay 0.5s để toast show rõ
    } else {
      throw new Error(response.message || "Đăng ký thất bại!");
    }
  } catch (error: any) {
    toast({
      title: "Lỗi",
      description: error.response?.data?.message || "Đăng ký thất bại. Vui lòng thử lại sau!",
      variant: "destructive",
    });
  }
}

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary/10 via-background to-secondary/10">
      <Card className="w-full max-w-md">
        <CardHeader> 
          <CardTitle className="text-center">Tạo tài khoản (Giảng viên)</CardTitle>
          <CardDescription>Đăng ký với tư cách giảng viên để truy cập vào nền tảng LMS.</CardDescription>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              <FormField
                control={form.control}
                name="fullname"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Họ và tên</FormLabel>
                    <FormControl>
                      <Input placeholder="John Doe" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input type="email" placeholder="johndoe@example.com" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Mật khẩu</FormLabel>
                    <FormControl>
                      <Input type="password" placeholder="********" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button type="submit" className="w-full">
                Đăng ký với tư cách giảng viên
              </Button>
            </form>
          </Form>
          <p className="text-center text-sm text-muted-foreground mt-4">
            Đã có tài khoản? <Link to="/login" className="text-primary hover:underline">Đăng nhập</Link>
          </p>
        </CardContent>
      </Card>
    </div>
  );
}