import { useState } from "react";
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
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Plus, Edit, Trash2 } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

interface Voucher {
  id: number;
  code: string;
  discountType: "percentage" | "fixed";
  value: number;
  startDate: string;
  endDate: string;
  status: "active" | "expired";
  usedCount: number;
}

const mockVouchers: Voucher[] = [
  {
    id: 1,
    code: "SUMMER2024",
    discountType: "percentage",
    value: 20,
    startDate: "2024-06-01",
    endDate: "2024-08-31",
    status: "active",
    usedCount: 145,
  },
  {
    id: 2,
    code: "NEWYEAR50",
    discountType: "fixed",
    value: 50,
    startDate: "2024-01-01",
    endDate: "2024-01-31",
    status: "expired",
    usedCount: 89,
  },
];

export default function Promotions() {
  const [vouchers, setVouchers] = useState<Voucher[]>(mockVouchers);
  const [isOpen, setIsOpen] = useState(false);
  const { toast } = useToast();

  const handleAddVoucher = () => {
    toast({
      title: "Voucher created",
      description: "The voucher has been successfully created.",
    });
    setIsOpen(false);
  };

  const handleDelete = (id: number) => {
    setVouchers(vouchers.filter((v) => v.id !== id));
    toast({
      title: "Voucher deleted",
      description: "The voucher has been removed.",
      variant: "destructive",
    });
  };

  return (
    <DashboardLayout role="admin">
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-3xl font-bold tracking-tight">Promotions & Vouchers</h2>
            <p className="text-muted-foreground">Create and manage discount vouchers</p>
          </div>
          <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogTrigger asChild>
              <Button>
                <Plus className="mr-2 h-4 w-4" />
                Create Voucher
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Create New Voucher</DialogTitle>
              </DialogHeader>
              <div className="space-y-4">
                <div>
                  <Label htmlFor="code">Voucher Code</Label>
                  <Input id="code" placeholder="e.g., WELCOME2024" />
                </div>
                <div>
                  <Label htmlFor="discountType">Discount Type</Label>
                  <Select>
                    <SelectTrigger>
                      <SelectValue placeholder="Select type" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="percentage">Percentage (%)</SelectItem>
                      <SelectItem value="fixed">Fixed Amount ($)</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <Label htmlFor="value">Discount Value</Label>
                  <Input id="value" type="number" placeholder="e.g., 20" />
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="startDate">Start Date</Label>
                    <Input id="startDate" type="date" />
                  </div>
                  <div>
                    <Label htmlFor="endDate">End Date</Label>
                    <Input id="endDate" type="date" />
                  </div>
                </div>
                <Button onClick={handleAddVoucher} className="w-full">
                  Create Voucher
                </Button>
              </div>
            </DialogContent>
          </Dialog>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>All Vouchers</CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Code</TableHead>
                  <TableHead>Type</TableHead>
                  <TableHead>Value</TableHead>
                  <TableHead>Valid Period</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Used</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
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
                      {voucher.startDate} - {voucher.endDate}
                    </TableCell>
                    <TableCell>
                      <Badge variant={voucher.status === "active" ? "default" : "secondary"}>
                        {voucher.status}
                      </Badge>
                    </TableCell>
                    <TableCell>{voucher.usedCount}</TableCell>
                    <TableCell className="text-right">
                      <Button variant="ghost" size="icon">
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => handleDelete(voucher.id)}
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
