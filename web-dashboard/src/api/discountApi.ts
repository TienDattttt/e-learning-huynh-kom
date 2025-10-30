// src/api/discountApi.ts
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export interface DiscountDto {
  discountId: number;
  code: string;
  percent: number | null;
  amount: number | null;
  fromDate: string | null;
  toDate: string | null;
  active: boolean;
}

export interface SaveDiscountRequest {
  discountId: number | null;
  code: string;
  percent: number | null;
  amount: number | null;
  fromDate: string | null;
  toDate: string | null;
}

export interface AttachCoursesRequest {
  discountId: number;
  courseIds: number[];
}

const getAuthHeader = () => {
  const token = localStorage.getItem('accessToken');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const getDiscountList = async (): Promise<DiscountDto[]> => {
  const response = await axios.get(`${API_BASE_URL}/discounts/list`, {
    params: { page: 0, size: 100, activeOnly: true },
    headers: getAuthHeader()
  });
  return response.data.data.items;
};

export const saveDiscount = async (req: SaveDiscountRequest): Promise<DiscountDto> => {
  const response = await axios.post(`${API_BASE_URL}/discounts/save`, req, { headers: getAuthHeader() });
  return response.data.data;
};

export const disableDiscount = async (discountId: number): Promise<void> => {
  await axios.post(`${API_BASE_URL}/discounts/disable`, null, {
    params: { discountId },
    headers: getAuthHeader()
  });
};

export const attachCourses = async (req: AttachCoursesRequest): Promise<void> => {
  await axios.post(`${API_BASE_URL}/discounts/attach-courses`, req, { headers: getAuthHeader() });
};