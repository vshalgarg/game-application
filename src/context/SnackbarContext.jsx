import React, {createContext,useContext,useState,useEffect,} from "react";
import CustomSnackbar from "../components/CustomSnackbar";
import { setSnackbar as registerSnackbar } from "../services/snackbarService";

const SnackbarContext = createContext();

export const useSnackbar = () => useContext(SnackbarContext);

export const SnackbarProvider = ({ children }) => {

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success",
    anchorOrigin: {
      vertical: "bottom",
      horizontal: "right",
    },
    duration: 3000,
  });

  const showSnackbar = (
    message,
    severity = "success",
    duration = 3000,
    anchorOrigin = {
      vertical: "bottom",
      horizontal: "right",
    }
  ) => {

    if (!message || typeof message !== "string") {
      return;
    }

    if (!message.trim()) {
      return;
    }

    setSnackbar({
      open: true,
      message,
      severity,
      duration,
      anchorOrigin,
    });
  };

  useEffect(() => {
    registerSnackbar(showSnackbar);
  }, []);

  const closeSnackbar = () => {
    setSnackbar((prev) => ({
      ...prev,
      open: false,
    }));
  };

  return (
    <SnackbarContext.Provider value={{ showSnackbar }}>
      {children}

      <CustomSnackbar
        open={snackbar.open}
        message={snackbar.message}
        severity={snackbar.severity}
        duration={snackbar.duration}
        anchorOrigin={snackbar.anchorOrigin}
        onClose={closeSnackbar}
      />
    </SnackbarContext.Provider>
  );
};

export default SnackbarProvider;