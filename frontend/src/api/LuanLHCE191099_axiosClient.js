import axios from 'axios';
export const sessionKey = 'a3-login';
export const expiredEvent = 'a3-login-expired';
const axiosClient = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/api', timeout: 15000 });
axiosClient.interceptors.request.use((config) => {
  const token = sessionStorage.getItem(sessionKey);
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
axiosClient.interceptors.response.use(
  (response) => {
    const body = response.data;
    if (body?.success === true) response.data = body.data;
    return response;
  },
  (error) => {
    if (error.response?.status === 401 && sessionStorage.getItem(sessionKey)) {
      sessionStorage.removeItem(sessionKey);
      window.dispatchEvent(new Event(expiredEvent));
    }
    return Promise.reject(error);
  },
);
export function errorText(error) {
  const body = error.response?.data;
  const errors = body?.success === false ? body.data : body?.errors;
  if (errors)
    return Object.entries(errors)
      .map(([field, message]) => `${field}: ${message}`)
      .join(' / ');
  return (
    body?.message ||
    (error.response
      ? `Request failed (${error.response.status}).`
      : 'Cannot connect to the API. Check that the backend is running.')
  );
}
export default axiosClient;
