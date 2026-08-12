export const loadAuth = () => {
  const auth = localStorage.getItem('auth');

  if (!auth) {
    return null;
  }
  try {
    return JSON.parse(auth);
  } catch {
    localStorage.removeItem('auth');
    return null;
  }
};

export const updateAuth = (partialData = {}) => {
  const currentAuth = loadAuth() || {};

  const updatedAuth = {
    ...currentAuth,
    ...partialData,
  };

  localStorage.setItem('auth', JSON.stringify(updatedAuth));

  return updatedAuth;
};

export const clearAuth = () => {
  localStorage.removeItem('auth');
};
