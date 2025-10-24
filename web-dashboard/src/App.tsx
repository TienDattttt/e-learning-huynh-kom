import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
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

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Index />} />
          
          {/* Admin Routes */}
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/admin/categories" element={<Categories />} />
          <Route path="/admin/promotions" element={<Promotions />} />
          <Route path="/admin/revenue" element={<AdminRevenue />} />
          <Route path="/admin/students" element={<AdminStudents />} />
          
          {/* Instructor Routes */}
          <Route path="/instructor" element={<InstructorDashboard />} />
          <Route path="/instructor/courses" element={<Courses />} />
          <Route path="/instructor/courses/create" element={<CreateCourse />} />
          <Route path="/instructor/students" element={<InstructorStudents />} />
          <Route path="/instructor/qa" element={<QA />} />
          <Route path="/instructor/revenue" element={<InstructorRevenue />} />
          
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
