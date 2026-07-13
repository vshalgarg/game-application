export const handleApiError = (error) => {
  if (error?.response) {
    // API responded with a status code != 2xx
    const { data, status } = error.response;
    if (status === 401) {
      return ' ';
    } 
    return data?.message || `Server error (${status})`;
  }
  if (error?.request) {
    // Request made but no response received
    return 'Network error. Please check your connection.';
  }
  
  // Something else (like manual throw, logical error)
  return error?.message || 'Something went wrong.';
};
  
// when the status is 200 OK and shows message based on response code 
export const checkLogicalError = (data, fallbackMessage = 'Action failed') => {
  if (data?.responseCode || data?.error?.responseCode) {
    const errorMessage = data.message || data?.error?.message|| fallbackMessage;
    throw new Error(errorMessage);
  }
  
  return data;
};