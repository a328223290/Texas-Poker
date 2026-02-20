import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

// 请求拦截器：自动携带 Token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器：统一处理错误
api.interceptors.response.use(
  (res) => res.data,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  },
);

// ---- 认证 ----
export const authApi = {
  register: (data: { username: string; password: string; email?: string; nickname?: string }) =>
    api.post('/auth/register', data),

  login: (data: { username: string; password: string }) =>
    api.post('/auth/login', data),
};

// ---- 用户 ----
export const userApi = {
  getProfile: (userId: number) => api.get(`/user/${userId}`),
};

// ---- 大厅 ----
export const lobbyApi = {
  getRooms: (params?: { page?: number; size?: number }) =>
    api.get('/lobby/rooms', { params }),

  createRoom: (data: { name: string; blindSmall: number; blindBig: number; maxPlayers: number }) =>
    api.post('/room/create', data),
};

// ---- 钱包 ----
export const walletApi = {
  getBalance: () => api.get('/wallet/balance'),
};

export default api;
