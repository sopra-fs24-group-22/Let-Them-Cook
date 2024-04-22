import { axiosAuth, axiosPublic } from "./axios";
import { objectToUrlParams } from "../helpers/objectToUrlParams";
import {format} from "node:url";

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
export const deleteRecipeAPI = async (id: string) => {
  await axiosAuth.delete("recipe/" + id);
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