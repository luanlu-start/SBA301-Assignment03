import axiosClient from './LuanLHCE191099_axiosClient.js';

export const authApi = {
  login: (data) => axiosClient.post('/auth/login', data),
  register: (data) => axiosClient.post('/auth/register', data),
  getCurrentUser: () => axiosClient.get('/auth/me'),
};
