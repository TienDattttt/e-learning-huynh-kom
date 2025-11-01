import axios from 'axios';
import { jwtDecode } from "d:/CNPM2/QLDA/GK/e-learning-huynh-kom/web-dashboard/node_modules/jwt-decode/build/esm/index";

interface JwtPayload {
  uid: number;
  // other fields
}

export const getTeacherId = () => {
  const token = localStorage.getItem('accessToken');
  if (!token) return null;
  const decoded = jwtDecode<JwtPayload>(token);
  return decoded.uid;
};

const API_BASE_URL = 'http://localhost:8080/api/auth'; // Có thể thay bằng env variable sau

export const register = async (data: { fullName: string; email: string; password: string; role: string }) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/register`, data);
    return response.data;
  } catch (error) {
    throw error; // Handle ở component
  }
};

export const login = async (data: { email: string; password: string }) => {
  const response = await axios.post(`${API_BASE_URL}/login`, data);
  const token = response.data?.data?.accessToken;

  if (token) {
    localStorage.setItem('accessToken', token); // ✅ lưu đúng key
  }

  return response.data;
};