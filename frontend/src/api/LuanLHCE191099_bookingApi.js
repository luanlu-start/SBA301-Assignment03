import axiosClient from './LuanLHCE191099_axiosClient.js';

export const bookingApi = {
  getAll: ({ search, signal } = {}) =>
    axiosClient.get('/bookings', { params: { q: search }, signal }),
  getMine: ({ search, signal } = {}) =>
    axiosClient.get('/my/bookings', { params: { q: search }, signal }),
  create: (data) => axiosClient.post('/bookings', data),
  update: (id, data) => axiosClient.put(`/bookings/${id}`, data),
  delete: (id) => axiosClient.delete(`/bookings/${id}`),
};
