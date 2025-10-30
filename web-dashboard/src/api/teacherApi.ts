import axios from 'axios';
import { getTeacherId } from './authApi'; // Import to get teacherId

const API_BASE_URL = 'http://localhost:8080/api/teacher';

const getAuthHeaders = () => {
  const token = localStorage.getItem('accessToken');
  return { Authorization: `Bearer ${token}` };
};

export const getStudents = async (page = 0, size = 20) => {
  const teacherId = getTeacherId();
  if (!teacherId) throw new Error('No teacher ID found');
  const response = await axios.get(`${API_BASE_URL}/students`, {
    params: { teacherId, page, size },
    headers: getAuthHeaders(),
  });
  return response.data;
};

export const getStudentProgress = async (studentId: number, courseId: number) => {
  const teacherId = getTeacherId();
  if (!teacherId) throw new Error('No teacher ID found');
  const response = await axios.get(`${API_BASE_URL}/students/${studentId}/progress`, {
    params: { teacherId, courseId },
    headers: getAuthHeaders(),
  });
  return response.data;
};

export const sendLockRequest = async (studentId: number, reason: string) => {
  const response = await axios.post(`${API_BASE_URL}/students/lock-request`, {
    studentId,
    reason,
  }, {
    headers: getAuthHeaders(),
  });
  return response.data;
};