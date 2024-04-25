import { axiosAuth, axiosPublic } from "./axios";
import { objectToUrlParams } from "../helpers/objectToUrlParams";

// Login
export const postLoginAPI = async (body: any) => {
  const { data } = await axiosPublic.post("auth/login", body);
  return data.accessToken;
};
export const postLogoutAPI = async (body: any) => {
  await axiosPublic.post("auth/logout", body);
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
export const getAllRecipesAPI = async (limit = null, offset = null, queryParams = {}) => {
  // Add limit and offset
  if(limit !== null) { queryParams[limit] = limit; }
  if(offset !== null) { queryParams[offset] = offset; }

  const { data } = await axiosAuth.get("recipes?" + objectToUrlParams(queryParams));
  return data;
};
export const getRecipeAPI = async (id: number) => {
  const { data } = await axiosAuth.get("recipe/" + id);
  return data;
};
export const deleteRecipeAPI = async (id: string) => {
  await axiosAuth.delete("recipe/" + id);
};

// Cookbooks
export const getCookbookAPI = async (userId: number) => {
   const { data } = await axiosAuth.get("cookbook/" + userId);
  return data;
};

export const postRecipeAPI = async (body: any) => {
  const { data } = await axiosAuth.post("recipe", body);
  return data;
};

// Sessions
export const postSessionAPI = async (session: any) => {
  const { data } = await axiosAuth.post("session", session);
  return data;
};
export const getAllSessionsAPI = async (params?: any) => {
  const { data } = await axiosAuth.get("sessions", { params });
  return data;
}
export const getSessionAPI = async (sessionId: number) => {
  const { data } = await axiosAuth.get("session/" + sessionId);
  return data;
}
export const getSessionCredentialsAPI = async (sessionId: number) => {
  const { data } = await axiosAuth.get("session/credentials/" + sessionId);
  return data;
}
export const putChecklistAPI = async (sessionId: number, body: any) => {
  await axiosAuth.put("session/" + sessionId + "/checklist", body);
}
export const getChecklistAPI = async (sessionId: number, body: any) => {
  const { data } = await axiosAuth.get("session/" + sessionId + "/checklist", body);
  return data;
}