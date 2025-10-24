import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Index from "./pages/Index";
import AdminDashboard from "./pages/admin/AdminDashboard";
import Categories from "./pages/admin/Categories";
import Promotions from "./pages/admin/Promotions";
import AdminRevenue from "./pages/admin/Revenue";
import AdminStudents from "./pages/admin/Students";
import InstructorDashboard from "./pages/instructor/InstructorDashboard";
import Courses from "./pages/instructor/Courses";
import CreateCourse from "./pages/instructor/CreateCourse";
import InstructorStudents from "./pages/instructor/Students";
import QA from "./pages/instructor/QA";
import InstructorRevenue from "./pages/instructor/Revenue";
import NotFound from "./pages/NotFound";
import Login from "./pages/auth/Login";
import Register from "./pages/auth/Register";
import { AuthProvider, AuthContext } from "./context/AuthContext"; // Import
import { useContext } from "react";

const queryClient = new QueryClient();

// Wrapper cho protected routes
const RequireAuth = ({ children, allowedRole }: { children: JSX.Element; allowedRole: string }) => {
  const authContext = useContext(AuthContext);
  const isAuthenticated = authContext?.isAuthenticated;
  const userRole = authContext?.userRole?.toUpperCase(); // Normalize

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (userRole !== allowedRole.toUpperCase()) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

const App = () => (
  <QueryClientProvider client={queryClient}>
    <AuthProvider> {/* Wrap toàn bộ */}
      <TooltipProvider>
        <Toaster />
        <Sonner />
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Index />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register"element={<Register />} />
            
            {/* Admin Routes - Protected */}
            <Route path="/admin" element={<RequireAuth allowedRole="ADMIN"><AdminDashboard /></RequireAuth>} />
            <Route path="/admin/categories" element={<RequireAuth allowedRole="ADMIN"><Categories /></RequireAuth>} />
            <Route path="/admin/promotions" element={<RequireAuth allowedRole="ADMIN"><Promotions /></RequireAuth>} />
            <Route path="/admin/revenue" element={<RequireAuth allowedRole="ADMIN"><AdminRevenue /></RequireAuth>} />
            <Route path="/admin/students" element={<RequireAuth allowedRole="ADMIN"><AdminStudents /></RequireAuth>} />
            
            {/* Instructor Routes - Protected */}
            <Route path="/instructor" element={<RequireAuth allowedRole="GIANGVIEN"><InstructorDashboard /></RequireAuth>} />
            <Route path="/instructor/courses" element={<RequireAuth allowedRole="GIANGVIEN"><Courses /></RequireAuth>} />
            <Route path="/instructor/courses/create" element={<RequireAuth allowedRole="GIANGVIEN"><CreateCourse /></RequireAuth>} />
            <Route path="/instructor/students" element={<RequireAuth allowedRole="GIANGVIEN"><InstructorStudents /></RequireAuth>} />
            <Route path="/instructor/qa" element={<RequireAuth allowedRole="GIANGVIEN"><QA /></RequireAuth>} />
            <Route path="/instructor/revenue" element={<RequireAuth allowedRole="GIANGVIEN"><InstructorRevenue /></RequireAuth>} />
            
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </TooltipProvider>
    </AuthProvider>
  </QueryClientProvider>
);

export default App;