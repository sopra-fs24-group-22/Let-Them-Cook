import { axiosAuth, axiosPublic } from "./axios";

// Login
export const postLoginAPI = async (login: any) => {
  const { data } = await axiosPublic.post("auth/login", login);
  return data.accessToken;
};
export const postRegisterAPI = async (body: any) => {
  const { data } = await axiosPublic.post("auth/register", body);
  return data.accessToken;
};

// Refresh
export const refreshAccessTokenAPI = async () => {
  const { data } = await axiosPublic.get("auth/refresh", { withCredentials: true });
  return data.accessToken;
};

// Recipes
export const getAllRecipesAPI = async () => {
  const { data } = await axiosAuth.get("recipes");
  return data;
};

export const postRecipeAPI = async (session: any) => {
  const { data } = await axiosAuth.post("session", { session });
  return data;
};

// Sessions
export const postSessionAPI = async (session: any) => {
  const { data } = await axiosAuth.post("session", { session });
  return data;
};
