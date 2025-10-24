import { createContext, useState, useEffect, ReactNode } from 'react';

interface AuthContextType {
  isAuthenticated: boolean;
  userRole: string | null;
  login: (token: string, role: string) => void;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [authState, setAuthState] = useState<{
    token: string | null;
    role: string | null;
  }>({
    token: localStorage.getItem('accessToken'),
    role: localStorage.getItem('userRole'),
  });

  // Sync localStorage với state khi load (persist qua refresh)
  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    const role = localStorage.getItem('userRole');
    if (token && role) {
      setAuthState({ token, role });
    }
  }, []);

  const login = (token: string, role: string) => {
    localStorage.setItem('accessToken', token);
    localStorage.setItem('userRole', role);
    setAuthState({ token, role });
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('userRole');
    setAuthState({ token: null, role: null });
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated: !!authState.token,
        userRole: authState.role,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};