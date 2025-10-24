import axios from 'axios';

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
  try {
    const response = await axios.post(`${API_BASE_URL}/login`, data);
    return response.data;
  } catch (error) {
    throw error;
  }
};