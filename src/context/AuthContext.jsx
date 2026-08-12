import React, { createContext, useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSnackbar } from "./SnackbarContext";
import { clearAuth, loadAuth, updateAuth } from "../storage/authStorage";

const AuthContext = createContext();
export const AuthProvider = ({ children }) => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const [auth, setAuth] = useState(loadAuth);

  const logout = () => {
    clearAuth();
    setAuth(null);
  };

  useEffect(() => {
    const onAppLogout = (event) => {
      logout();
      showSnackbar(event.detail?.message || "Session expired! Login again", "error");
      navigate("/login", { replace: true });
    };

    // Register listener
    window.addEventListener("app:logout", onAppLogout);

    // Clean up listener when provider unmounts
    return () => {
      window.removeEventListener("app:logout", onAppLogout);
    };
  }, [navigate]);

  const login = async ({ token, userId, username, roles, permissions = [], userProfile }) => {
    const updatedAuth = updateAuth({
      token,
      userId,
      username,
      roles,
      permissions,
      userProfile,
    });

    setAuth(updatedAuth);
  };

  const updateCurrentUser = (partialData) => {
    const updatedAuth = updateAuth(partialData);
    setAuth(updatedAuth);
    return updatedAuth;
  };

  return (
    <AuthContext.Provider value={{ auth, login, logout, updateCurrentUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
