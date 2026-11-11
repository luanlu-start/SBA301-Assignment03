import axiosClient from './LuanLHCE191099_axiosClient.js';

export const roomApi = {
  getAll: ({ search, signal } = {}) =>
    axiosClient.get('/rooms', { params: { q: search }, signal }),
  getPublic: ({ search, startDate, endDate, signal } = {}) =>
    axiosClient.get('/public/rooms', { params: { q: search, startDate, endDate }, signal }),
  getTypes: ({ signal } = {}) => axiosClient.get('/public/room-types', { signal }),
  create: (data) => axiosClient.post('/rooms', data),
  update: (id, data) => axiosClient.put(`/rooms/${id}`, data),
  delete: (id) => axiosClient.delete(`/rooms/${id}`),
};
