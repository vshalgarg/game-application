import { Snackbar, Alert } from "@mui/material";

const alertSx = {
  success: {
    backgroundColor: "var(--gz-snackbar-success)",
    color: "var(--gz-popup-dark)",
  },
  warning: {
    backgroundColor: "var(--gz-snackbar-warning)",
    color: "var(--gz-text)",
  },
  error: {
    backgroundColor: "var(--gz-snackbar-error)",
    color: "var(--gz-text)",
  },
  info: {
    backgroundColor: "var(--gz-snackbar-info)",
    color: "var(--gz-text)",
  },
};

const CustomSnackbar = ({
  open,
  message,
  severity = "success",
  onClose,
  duration = 3000,
  anchorOrigin = { vertical: "bottom", horizontal: "right" },
}) => {
  const colors = alertSx[severity] || alertSx.info;

  return (
    <Snackbar
      open={open}
      autoHideDuration={duration}
      onClose={onClose}
      anchorOrigin={anchorOrigin}
      sx={{ zIndex: 9999 }}
    >
      <Alert
        onClose={onClose}
        severity={severity}
        variant="filled"
        sx={{
          backgroundColor: colors.backgroundColor,
          color: colors.color,
          fontFamily: "var(--gz-font-sans)",
          fontWeight: 500,
          "& .MuiAlert-icon": { color: colors.color },
          "& .MuiIconButton-root": { color: colors.color },
        }}
      >
        {message}
      </Alert>
    </Snackbar>
  );
};

export default CustomSnackbar;
