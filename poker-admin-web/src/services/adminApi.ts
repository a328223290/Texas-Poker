import axios from 'axios';

const adminApi = axios.create({
  baseURL: '/api/admin',
  timeout: 10000,
});

adminApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('admin_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

adminApi.interceptors.response.use(
  (res) => res.data,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('admin_token');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  },
);

// ---- 仪表盘 ----
export const dashboardApi = {
  getOverview: () => adminApi.get('/dashboard/overview'),
  getUserStats: (days = 7) => adminApi.get(`/dashboard/user-stats?days=${days}`),
  getGameStats: (days = 7) => adminApi.get(`/dashboard/game-stats?days=${days}`),
};

// ---- 用户管理 ----
export const userManageApi = {
  list: (params: { page: number; size: number; username?: string; status?: string }) =>
    adminApi.get('/users', { params }),
  detail: (id: number) => adminApi.get(`/users/${id}`),
  updateStatus: (id: number, status: string) =>
    adminApi.put(`/users/${id}/status`, { status }),
  resetPassword: (id: number) => adminApi.put(`/users/${id}/password`),
  setRole: (id: number, roleId: number) =>
    adminApi.put(`/users/${id}/role`, { roleId }),
};

// ---- 筹码管理 ----
export const chipManageApi = {
  getBalance: (userId: number) => adminApi.get(`/chips/${userId}`),
  adjust: (data: { targetUserId: number; amount: number; reason: string }) =>
    adminApi.post('/chips/adjust', data),
  transactions: (params: { userId?: number; page: number; size: number }) =>
    adminApi.get('/chips/transactions', { params }),
};

// ---- 房间管理 ----
export const roomManageApi = {
  list: (params?: { page?: number; size?: number }) =>
    adminApi.get('/rooms', { params }),
  close: (id: number) => adminApi.post(`/rooms/${id}/close`),
};

// ---- 牌局记录 ----
export const gameRecordApi = {
  list: (params?: { page?: number; size?: number }) =>
    adminApi.get('/games', { params }),
  detail: (id: number) => adminApi.get(`/games/${id}`),
};

// ---- 公告管理 ----
export const announcementApi = {
  list: () => adminApi.get('/announcements'),
  create: (data: { title: string; content: string }) =>
    adminApi.post('/announcements', data),
  update: (id: number, data: { title: string; content: string }) =>
    adminApi.put(`/announcements/${id}`, data),
  remove: (id: number) => adminApi.delete(`/announcements/${id}`),
};

export default adminApi;
