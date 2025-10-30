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

  // 🧱 1️⃣ Thêm JSON dữ liệu khóa học
  formData.append(
    'courseData',
    new Blob([JSON.stringify(req)], { type: 'application/json' })
  );

  // 🖼️ 2️⃣ Thêm file ảnh (nếu có)
  if (imageFile) {
    formData.append('imageFile', imageFile);
  }

  // 🎬 3️⃣ Thêm danh sách video file (nếu có trong lessons)
  // Duyệt toàn bộ chapters và lessons trong req
  if (req.chapters && Array.isArray(req.chapters)) {
    req.chapters.forEach((chapter) => {
      if (chapter.lessons && Array.isArray(chapter.lessons)) {
        chapter.lessons.forEach((lesson, idx) => {
          if (lesson.videoFile) {
            // Gắn tên file có index để backend mapping theo thứ tự
            formData.append('videoFiles', lesson.videoFile, `video_${idx + 1}.mp4`);
          }
        });
      }
    });
  }

  // 🚀 4️⃣ Gửi request multipart
  const response = await axios.post(`${API_BASE_URL}/full-save`, formData, {
    headers: {
      ...getAuthHeaders(),
      'Content-Type': 'multipart/form-data',
    },
  });

  return response.data.data; // ApiResponse<Integer>
};

export const uploadLessonVideo = async (videoFile) => {
  const formData = new FormData();
  formData.append('file', videoFile);

  const response = await axios.post(
    `${API_BASE_URL}/lessons/upload-video`,
    formData,
    {
      headers: {
        ...getAuthHeaders(),
        'Content-Type': 'multipart/form-data',
      },
    }
  );

  return response.data.data; // Trả về URL video Cloudinary
};



export const getCategories = async () => {
  const response = await axios.get('http://localhost:8080/api/categories/tree', {
    headers: getAuthHeaders(),
  });
  return response.data.data;
};