import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const dashboardApi = {
  async getDashboard(params?: {
    year?: number;
    month?: number;
    quarter?: number;
    courseId?: number;
  }) {
    const token = localStorage.getItem('accessToken'); // ✅ cùng key
    const headers = token ? { Authorization: `Bearer ${token}` } : {};
    const res = await axios.get(`${BASE_URL}/dashboard`, { params, headers });
    return res.data.data;
  },
};

export { dashboardApi };
