import { create } from 'zustand';

interface AuthState {
  token: string | null;
  userId: number | null;
  nickname: string | null;
  chips: number;
  login: (token: string, userId: number, nickname: string, chips: number) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem('token'),
  userId: null,
  nickname: null,
  chips: 0,

  login: (token, userId, nickname, chips) => {
    localStorage.setItem('token', token);
    set({ token, userId, nickname, chips });
  },

  logout: () => {
    localStorage.removeItem('token');
    set({ token: null, userId: null, nickname: null, chips: 0 });
  },
}));
