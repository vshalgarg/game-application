let showSnackbarRef = null;

export const setSnackbar = (showSnackbar) => {
    showSnackbarRef = showSnackbar;
};

export const successSnackbar = (message) => {
    if (showSnackbarRef && message) {
        showSnackbarRef(message, "success");
    }
};

export const errorSnackbar = (message) => {
    if (showSnackbarRef && message) {
        showSnackbarRef(message, "error");
    }
};