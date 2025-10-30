// src/api/categoryApi.ts
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export interface CategoryNode {
  id: number;
  name: string;
  sortOrder: number | null;
  status: boolean;
  parentId: number | null;
  children: CategoryNode[];
}

export interface SaveCategoryRequest {
  courseCategoryId: number | null;
  name: string;
  parentId: number | null;
  sortOrder: number | null;
  status: boolean;
}

const getAuthHeader = () => {
  const token = localStorage.getItem('accessToken');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const getCategoryTree = async (): Promise<CategoryNode[]> => {
  const response = await axios.get(`${API_BASE_URL}/categories/tree`, { headers: getAuthHeader() });
  return response.data.data; // Assuming ApiResponse structure with 'data' field
};

export const saveCategory = async (req: SaveCategoryRequest): Promise<number> => {
  const response = await axios.post(`${API_BASE_URL}/categories/save`, req, { headers: getAuthHeader() });
  return response.data.data; // Returns the ID
};