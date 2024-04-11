export const getBaseUrl = () => {
  return process.env.NODE_ENV === "development"
    ? "http://localhost:8080"
    : window.location.origin;
};
