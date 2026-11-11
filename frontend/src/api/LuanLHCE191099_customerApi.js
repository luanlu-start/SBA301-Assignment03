import axiosClient from './LuanLHCE191099_axiosClient.js';

export const customerApi = {
  getAll: ({ search, signal } = {}) =>
    axiosClient.get('/customers', { params: { q: search }, signal }),
  create: (data) => axiosClient.post('/customers', data),
  update: (id, data) => axiosClient.put(`/customers/${id}`, data),
  delete: (id) => axiosClient.delete(`/customers/${id}`),
};
