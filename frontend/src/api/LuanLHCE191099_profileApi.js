import axiosClient from './LuanLHCE191099_axiosClient.js';

export const profileApi = {
  get: () => axiosClient.get('/my/profile'),
  update: (data) => axiosClient.put('/my/profile', data),
};
