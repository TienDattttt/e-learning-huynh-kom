// src/pages/admin/Promotions.tsx
import { useEffect, useState } from "react";
import { DashboardLayout } from "@/components/layouts/DashboardLayout";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Plus, Edit, Trash2 } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { getDiscountList, saveDiscount, disableDiscount, DiscountDto, SaveDiscountRequest } from "@/api/discountApi";

interface Voucher {
  id: number;
  code: string;
  discountType: "percentage" | "fixed";
  value: number;
  startDate: string | null;
  endDate: string | null;
  status: "active" | "expired";
}

const formSchema = z.object({
  code: z.string().min(1, { message: "Mã voucher là bắt buộc" }),
  discountType: z.enum(["percentage", "fixed"]),
  value: z.number().min(1, { message: "Giá trị giảm giá phải tối thiểu là 1" }),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
});

export default function Promotions() {
  const [vouchers, setVouchers] = useState<Voucher[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [currentVoucher, setCurrentVoucher] = useState<Voucher | null>(null);
  const { toast } = useToast();

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      code: "",
      discountType: "percentage",
      value: 0,
      startDate: "",
      endDate: "",
    },
  });

  const formatDate = (dateStr: string | null) => {
    if (!dateStr) return "N/A";
    const [year, month, day] = dateStr.split('-');
    return `${day}/${month}/${year}`;
  };

  const fetchVouchers = async () => {
    try {
      const data = await getDiscountList();
      const mapped: Voucher[] = data.map((d: DiscountDto) => ({
        id: d.discountId,
        code: d.code,
        discountType: d.percent ? "percentage" : "fixed",
        value: d.percent || d.amount || 0,
        startDate: d.fromDate ? d.fromDate.split('T')[0] : null,
        endDate: d.toDate ? d.toDate.split('T')[0] : null,
        status: d.active ? "active" : "expired",
      }));
      setVouchers(mapped);
    } catch (error) {
      toast({
        title: "Lỗi",
        description: "Lỗi khi tải khuyến mãi",
        variant: "destructive",
      });
    }
  };

  useEffect(() => {
    fetchVouchers();
  }, []);

  useEffect(() => {
    if (currentVoucher) {
      form.reset({
        code: currentVoucher.code,
        discountType: currentVoucher.discountType,
        value: currentVoucher.value,
        startDate: currentVoucher.startDate ?? "",
        endDate: currentVoucher.endDate ?? "",
      });
    } else {
      form.reset({
        code: "",
        discountType: "percentage",
        value: 0,
        startDate: "",
        endDate: "",
      });
    }
  }, [currentVoucher, form]);

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    try {
      const percent = values.discountType === "percentage" ? values.value : null;
      const amount = values.discountType === "fixed" ? values.value : null;
      const req: SaveDiscountRequest = {
        discountId: currentVoucher?.id ?? null,
        code: values.code,
        percent,
        amount,
        fromDate: values.startDate ? `${values.startDate}T00:00:00` : null,
        toDate: values.endDate ? `${values.endDate}T23:59:59` : null,
      };
      await saveDiscount(req);
      toast({
        title: currentVoucher ? "Đã cập nhật khuyến mãi" : "Đã tạo khuyến mãi",
        description: "Khuyến mãi đã được lưu",
      });
      setIsOpen(false);
      setCurrentVoucher(null);
      fetchVouchers();
    } catch (error) {
      toast({
        title: "Lỗi",
        description: "Lỗi khi lưu khuyến mãi.",
        variant: "destructive",
      });
    }
  };

  const handleDisable = async (id: number) => {
    try {
      await disableDiscount(id);
      toast({
        title: "Khuyến mãi bị vô hiệu hóa",
        description: "Khuyến mãi đã bị vô hiệu hóa.",
        variant: "destructive",
      });
      fetchVouchers();
    } catch (error) {
      toast({
        title: "Lỗi",
        description: "Lỗi khi vô hiệu hóa khuyến mãi.",
        variant: "destructive",
      });
    }
  };

  const openAddDialog = () => {
    setCurrentVoucher(null);
    setIsOpen(true);
  };

  const openEditDialog = (voucher: Voucher) => {
    setCurrentVoucher(voucher);
    setIsOpen(true);
  };

  return (
    <DashboardLayout role="admin">
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-3xl font-bold tracking-tight">Khuyến mãi và phiếu giảm giá</h2>
          </div>
          <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogTrigger asChild>
              <Button onClick={openAddDialog}>
                <Plus className="mr-2 h-4 w-4" />
                Tạo mã khuyến mãi
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>{currentVoucher ? "Cập nhật mã giảm giá": "Tạo mã giảm giá"}</DialogTitle>
              </DialogHeader>
              <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                  <FormField
                    control={form.control}
                    name="code"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Mã phiếu giảm giá</FormLabel>
                        <FormControl>
                          <Input placeholder="e.g., WELCOME2024" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="discountType"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Loại giảm giá</FormLabel>
                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                          <FormControl>
                            <SelectTrigger>
                              <SelectValue placeholder="Select type" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            <SelectItem value="percentage">Percentage (%)</SelectItem>
                            <SelectItem value="fixed">Fixed Amount ($)</SelectItem>
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="value"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Giá trị giảm giá</FormLabel>
                        <FormControl>
                          <Input
                            type="number"
                            placeholder="e.g., 20"
                            {...field}
                            onChange={(e) => field.onChange(parseFloat(e.target.value) || 0)}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <div className="grid grid-cols-2 gap-4">
                    <FormField
                      control={form.control}
                      name="startDate"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Ngày bắt đầu</FormLabel>
                          <FormControl>
                            <Input type="date" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name="endDate"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Ngày kết thúc</FormLabel>
                          <FormControl>
                            <Input type="date" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>
                  <Button type="submit" className="w-full">
                    {currentVoucher ? "Cập nhật mã giảm giá" : "Tạo mã giảm giá mới"}
                  </Button>
                </form>
              </Form>
            </DialogContent>
          </Dialog>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Tất cả mã giảm giá</CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Mã</TableHead>
                  <TableHead>Loại</TableHead>
                  <TableHead>Giá trị</TableHead>
                  <TableHead>Thời gian hiệu lực</TableHead>
                  <TableHead>Trạng thái</TableHead>
                  <TableHead className="text-right">Hoạt động</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {vouchers.map((voucher) => (
                  <TableRow key={voucher.id}>
                    <TableCell className="font-medium">{voucher.code}</TableCell>
                    <TableCell className="capitalize">{voucher.discountType}</TableCell>
                    <TableCell>
                      {voucher.discountType === "percentage" ? `${voucher.value}%` : `$${voucher.value}`}
                    </TableCell>
                    <TableCell>
  <div className="flex flex-wrap gap-2">
    <span className="px-2 py-1 text-xs rounded-full bg-green-100 text-green-700">
      {formatDate(voucher.startDate)}
    </span>
    <span className="text-gray-500">→</span>
    <span className="px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-700">
      {formatDate(voucher.endDate)}
    </span>
  </div>
</TableCell>

                    <TableCell>
                      <Badge variant={voucher.status === "active" ? "default" : "secondary"}>
  {voucher.status === "active" ? "Đang hoạt động" : "Hết hạn"}
</Badge>

                    </TableCell>
                    <TableCell className="text-right">
                      <Button variant="ghost" size="icon" onClick={() => openEditDialog(voucher)}>
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => handleDisable(voucher.id)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  );
}