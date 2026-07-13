// let showSnackbarRef = null;

// export const setSnackbar = (showSnackbar) => {
//     showSnackbarRef = showSnackbar;
// };

// export const successSnackbar = (message) => {
//     if (showSnackbarRef && message) {
//         showSnackbarRef(message, "success");
//     }
// };

// export const errorSnackbar = (message) => {
//     if (showSnackbarRef && message) {
//         showSnackbarRef(message, "error");
//     }
// };

let snackbarFunction = null;

export const setSnackbar = (fn) => {
  snackbarFunction = fn;
};

export const showGlobalSnackbar = (
  message,
  severity = "success",
  duration = 3000,
  anchorOrigin = {
    vertical: "bottom",
    horizontal: "right",
  }
) => {
  if (snackbarFunction) {
    snackbarFunction(message, severity, duration, anchorOrigin);
  }
};