import { axiosAuth, axiosPublic } from "./axios";

// Login
export const postLoginAPI = async (body: any) => {
  const { data } = await axiosPublic.post("auth/login", body);
  setRefreshToken(data.refreshToken);
  return data.accessToken;
};
export const postRegisterAPI = async (body: any) => {
  const { data } = await axiosPublic.post("auth/register", body);
  setRefreshToken(data.refreshToken);
  return data.accessToken;
};

// Refresh
export const refreshAccessTokenAPI = async () => {
  const { data } = await axiosPublic.post("auth/refresh",
    { refreshToken: getRefreshToken() });
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

export const getAllSessionsAPI = async () => {
  const { data } = await axiosAuth.get("sessions");
  return data;
}

// ### Helpers ###
const setRefreshToken = (refreshToken: string) => {
  localStorage.setItem("refreshToken", refreshToken);
};
export const getRefreshToken = () => localStorage.refreshToken;
export const deleteRefreshToken = () => localStorage.removeItem("refreshToken");