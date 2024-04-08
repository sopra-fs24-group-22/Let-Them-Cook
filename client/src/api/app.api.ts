import { axiosAuth, axiosPublic } from "./axios";

// Login
export const postLoginAPI = async (login: any) => {
  const { data } = await axiosPublic.post("./api/auth/login", { login });
  return data.accessToken;
};
export const postRegisterAPI = async (register: any) => {
  const { data } = await axiosPublic.post("./api/auth/register", { register });
  return data.accessToken;
};

// Refresh
export const refreshAccessTokenAPI = async () => {
  const { data } = await axiosPublic.get("./api/refresh", { withCredentials: true });
  return data.accessToken;
};

// Recipes
export const getAllRecipesAPI = async () => {
  const { data } = await axiosAuth.get("./api/recipes");
  return data;
};

// Sessions
export const postSessionAPI = async (session: any) => {
  const { data } = await axiosAuth.post("./api/session", { session });
  return data;
};