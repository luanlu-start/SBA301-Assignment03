import { createContext, useContext, useEffect, useState } from 'react';
import { authApi } from '../api/LuanLHCE191099_authApi.js';
import { expiredEvent, sessionKey } from '../api/LuanLHCE191099_axiosClient.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(() => Boolean(sessionStorage.getItem(sessionKey)));
  useEffect(() => {
    let active = true;
    const expire = () => setUser(null);
    window.addEventListener(expiredEvent, expire);
    if (sessionStorage.getItem(sessionKey)) {
      authApi
        .getCurrentUser()
        .then(({ data }) => {
          if (active) setUser(data);
        })
        .catch(() => {
          sessionStorage.removeItem(sessionKey);
        })
        .finally(() => {
          if (active) setLoading(false);
        });
    }
    return () => {
      active = false;
      window.removeEventListener(expiredEvent, expire);
    };
  }, []);
  async function login(email, password) {
    const { data } = await authApi.login({ email, password });
    sessionStorage.setItem(sessionKey, data.token);
    setUser(data.user);
    return data.user;
  }
  function logout() {
    sessionStorage.removeItem(sessionKey);
    setUser(null);
  }
  async function refresh() {
    const { data } = await authApi.getCurrentUser();
    setUser(data);
  }
  return (
    <AuthContext.Provider value={{ user, loading, login, logout, refresh }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
