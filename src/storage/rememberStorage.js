const REMEMBER_EMAIL_KEY = "rememberEmail";

export const loadRememberedEmail = () => {
  return localStorage.getItem(REMEMBER_EMAIL_KEY);
};

export const updateRememberedEmail = (email) => {
  localStorage.setItem(REMEMBER_EMAIL_KEY, email);
  return email;
};

export const clearRememberedEmail = () => {
  localStorage.removeItem(REMEMBER_EMAIL_KEY);
};
