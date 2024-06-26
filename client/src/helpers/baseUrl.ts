export const getBaseUrl = () => {
  return process.env.NODE_ENV === "development"
    ? "http://localhost:5000/api"
    : window.location.origin;
};
