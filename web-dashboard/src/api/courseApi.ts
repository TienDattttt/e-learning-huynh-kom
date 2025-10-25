import axios from 'axios';
import { getTeacherId } from './authApi';

const API_BASE_URL = 'http://localhost:8080/api/courses';

const getAuthHeaders = () => {
  const token = localStorage.getItem('accessToken');
  return { Authorization: `Bearer ${token}` };
};

export const getMyCourses = async (page = 0, size = 20) => {
  const response = await axios.get(`${API_BASE_URL}/teacher/list`, {
    params: { page, size },
    headers: getAuthHeaders(),
  });
  return response.data.data;  
};

export const getCourseDetail = async (courseId) => {
  const response = await axios.get(`${API_BASE_URL}/teacher/detail`, {
    params: { courseId },
    headers: getAuthHeaders(),
  });
  return response.data.data;
};

export const saveCourse = async (req) => {
  const response = await axios.post(`${API_BASE_URL}/save`, req, {
    headers: getAuthHeaders(),
  });
  return response.data;
};

export const publishCourse = async (courseId, publish) => {
  const req = { courseId, publish };
  const response = await axios.post(`${API_BASE_URL}/publish`, req, {
    headers: getAuthHeaders(),
  });
  return response.data;
};

export const saveChapter = async (req) => {
  const response = await axios.post(`${API_BASE_URL}/chapters/save`, req, {
    headers: getAuthHeaders(),
  });
  return response.data;
};

export const saveLesson = async (req) => {
  const response = await axios.post(`${API_BASE_URL}/lessons/save`, req, {
    headers: getAuthHeaders(),
  });
  return response.data;
};

export const deleteCourse = async (courseId) => {
  const response = await axios.delete(`${API_BASE_URL}/${courseId}`, {
    headers: getAuthHeaders(),
  });
  return response.data;
};

export const deleteChapter = async (chapterId) => {
  const response = await axios.delete(`${API_BASE_URL}/chapters/${chapterId}`, {
    headers: getAuthHeaders(),
  });
  return response.data;
};

export const deleteLesson = async (lessonId) => {
  const response = await axios.delete(`${API_BASE_URL}/lessons/${lessonId}`, {
    headers: getAuthHeaders(),
  });
  return response.data;
};

export const saveFullCourse = async (req, imageFile = null) => {
  const formData = new FormData();
  formData.append('courseData', new Blob([JSON.stringify(req)], { type: 'application/json' }));
  if (imageFile) {
    formData.append('imageFile', imageFile);
  }
  const response = await axios.post(`${API_BASE_URL}/full-save`, formData, {
    headers: getAuthHeaders(),
  });
  return response.data.data;  // Assuming ApiResponse<Integer>
};

export const getCategories = async () => {
  const response = await axios.get('http://localhost:8080/api/categories/tree', {
    headers: getAuthHeaders(),
  });
  return response.data.data;
};