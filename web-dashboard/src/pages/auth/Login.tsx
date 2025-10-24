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
import { toast } from "@/components/ui/use-toast";
import { login as loginApi } from "@/api/authApi"; // Rename để tránh conflict
import { useContext } from "react";
import { AuthContext } from "@/context/AuthContext";

const formSchema = z.object({
  email: z.string().email({ message: "Invalid email address." }),
  password: z.string().min(8, { message: "Password must be at least 8 characters." }),
});

export default function Login() {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const navigate = useNavigate();
  const authContext = useContext(AuthContext); // Get context

  async function onSubmit(values: z.infer<typeof formSchema>) {
    try {
      const response = await loginApi({
        email: values.email,
        password: values.password,
      });

      if (response.success) {
        const { accessToken, scope } = response.data;

        // Normalize role (remove 'ROLE_' và toUpperCase)
        const role = scope.replace('ROLE_', '').toUpperCase();

        toast({ title: "Success", description: response.message || "Login successful!" });

        // Sử dụng context để set auth state
        authContext?.login(accessToken, role);

        let redirectPath = '';
        if (role === 'ADMIN') {
          redirectPath = "/admin";
        } else if (role === 'GIANGVIEN') {
          redirectPath = "/instructor";
        } else {
          console.error('Unknown role:', role);
          throw new Error("Unknown role");
        }
        // Workaround vẫn giữ nếu cần, nhưng với context re-render nên ok
        setTimeout(() => {
          navigate(redirectPath, { replace: true });
        }, 0);
      } else {
        throw new Error(response.message || "Login failed");
      }
    } catch (error: any) {
      console.error('Login error:', error);
      toast({
        title: "Error",
        description: error.response?.data?.message || "Login failed. Please check your credentials.",
        variant: "destructive",
      });
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary/10 via-background to-secondary/10">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>Login to Your Account</CardTitle>
          <CardDescription>Access the LMS platform as Admin or Instructor.</CardDescription>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
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
                    <FormLabel>Password</FormLabel>
                    <FormControl>
                      <Input type="password" placeholder="********" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
             <Button type="submit" className="w-full">
                Login
              </Button>
            </form>
          </Form>
          <p className="text-center text-sm text-muted-foreground mt-4">
            Don't have an account? <Link to="/register" className="text-primary hover:underline">Register as Instructor</Link>
          </p>
        </CardContent>
      </Card>
    </div>
  );
}