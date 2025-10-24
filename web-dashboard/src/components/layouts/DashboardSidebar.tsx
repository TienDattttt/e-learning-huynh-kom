import { NavLink, useLocation } from "react-router-dom";
import {
  LayoutDashboard,
  FolderTree,
  Tag,
  DollarSign,
  Users,
  BookOpen,
  MessageSquare,
  GraduationCap,
} from "lucide-react";
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar";

interface DashboardSidebarProps {
  role: "admin" | "instructor";
}

const adminItems = [
  { title: "Dashboard", url: "/admin", icon: LayoutDashboard },
  { title: "Categories", url: "/admin/categories", icon: FolderTree },
  { title: "Promotions", url: "/admin/promotions", icon: Tag },
  { title: "Revenue Reports", url: "/admin/revenue", icon: DollarSign },
  { title: "Students", url: "/admin/students", icon: Users },
];

const instructorItems = [
  { title: "Dashboard", url: "/instructor", icon: LayoutDashboard },
  { title: "My Courses", url: "/instructor/courses", icon: BookOpen },
  { title: "Students", url: "/instructor/students", icon: Users },
  { title: "Q&A", url: "/instructor/qa", icon: MessageSquare },
  { title: "Revenue", url: "/instructor/revenue", icon: DollarSign },
];

export function DashboardSidebar({ role }: DashboardSidebarProps) {
  const { state } = useSidebar();
  const location = useLocation();
  const items = role === "admin" ? adminItems : instructorItems;

  const getNavClass = (url: string) => {
    const isActive = location.pathname === url;
    return isActive
      ? "bg-sidebar-accent text-sidebar-accent-foreground"
      : "hover:bg-sidebar-accent/50";
  };

  return (
    <Sidebar collapsible="icon">
      <SidebarContent>
        <div className="p-6 border-b border-sidebar-border">
          <div className="flex items-center gap-2">
            <GraduationCap className="h-8 w-8 text-sidebar-primary" />
            {state !== "collapsed" && (
              <span className="text-xl font-bold text-sidebar-foreground">
                LMS {role === "admin" ? "Admin" : "Instructor"}
              </span>
            )}
          </div>
        </div>

        <SidebarGroup>
          <SidebarGroupLabel>Navigation</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {items.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <SidebarMenuButton asChild>
                    <NavLink to={item.url} className={getNavClass(item.url)}>
                      <item.icon className="h-5 w-5" />
                      <span>{item.title}</span>
                    </NavLink>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  );
}
