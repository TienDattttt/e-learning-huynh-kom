import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/teacher";

const getAuthHeaders = () => {
  const token = localStorage.getItem("accessToken");
  return { Authorization: `Bearer ${token}` };
};

// 🔹 Lấy danh sách yêu cầu khóa tài khoản (pending)
export const getPendingLockRequests = async () => {
  const response = await axios.get(`${API_BASE_URL}/lock-requests/pending`, {
    headers: getAuthHeaders(),
  });
  return response.data;
};

// 🔹 Duyệt / Từ chối yêu cầu
export const updateLockRequestStatus = async (id: number, status: "APPROVED" | "REJECTED") => {
  const response = await axios.put(
    `${API_BASE_URL}/lock-requests/${id}`,
    {},
    { params: { status }, headers: getAuthHeaders() }
  );
  return response.data;
};
