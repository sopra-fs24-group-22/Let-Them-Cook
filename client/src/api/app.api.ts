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
  const { data } = await axiosPublic.get("auth/refresh", {
    withCredentials: true,
  });
  return data.accessToken;
};

// Recipes
export const getRecipesAPI = async (params: any = {}) => {
  const { data } = await axiosAuth.get("recipes?" + objectToUrlParams(params));
  return data;
};
export const getRecipeAPI = async (id: number) => {
  const { data } = await axiosAuth.get("recipe/" + id);
  return data;
};
export const deleteRecipeAPI = async (id: string) => {
  await axiosAuth.delete("recipe/" + id);
};
export const postRecipeAPI = async (body: any) => {
  const { data } = await axiosAuth.post("recipe", body);
  return data;
};
export const putRecipeAPI = async (body: any) => {
  await axiosAuth.put("recipe/", body);
};
export const postRateRecipeAPI = async (recipeId: number, rating: number) => {
  await axiosAuth.post("recipe/" + recipeId + "/rate", { rating: rating });
};

// Cookbooks
export const getCookbookAPI = async (userId: number) => {
  const { data } = await axiosAuth.get("cookbook/" + userId);
  return data;
};
export const addRecipeToCookbookAPI = async (recipeId: number) => {
  const { data } = await axiosAuth.post("cookbook/recipe/" + recipeId);
  return data;
};
export const removeRecipeFromCookbookAPI = async (recipeId: number) => {
  await axiosAuth.delete("cookbook/recipe/" + recipeId);
};

// Sessions
export const postSessionAPI = async (session: any) => {
  const { data } = await axiosAuth.post("session", session);
  return data;
};
export const getSessionsAPI = async (params: any = {}) => {
  const { data } = await axiosAuth.get("sessions?" + objectToUrlParams(params));
  return data;
};
export const getSessionAPI = async (sessionId: number) => {
  const { data } = await axiosAuth.get("session/" + sessionId);
  return data;
};
export const getSessionCredentialsAPI = async (sessionId: number) => {
  const { data } = await axiosAuth.get("session/credentials/" + sessionId);
  return data;
};
export const putChecklistAPI = async (sessionId: number, body: any) => {
  await axiosAuth.put("session/" + sessionId + "/checklist", body);
};
export const getChecklistAPI = async (sessionId: number) => {
  const { data } = await axiosAuth.get("session/" + sessionId + "/checklist");
  return data;
};
export const postSessionRequestAPI = async (sessionId: number) => {
  await axiosAuth.post("session_request/" + sessionId);
};
export const postSessionRequestAcceptAPI = async (
  sessionId: number,
  body: any,
) => {
  await axiosAuth.post("session_request/" + sessionId + "/accept/", body);
};
export const postSessionRequestDenyAPI = async (
  sessionId: number,
  body: any,
) => {
  await axiosAuth.post("session_request/" + sessionId + "/deny/", body);
};
export const getSessionRequestsAPI = async (sessionId: number) => {
  const { data } = await axiosAuth.get("session_request/" + sessionId);
  return data;
};
export const getSessionRequestsUserAPI = async () => {
  const { data } = await axiosAuth.get("session_request/");
  return data;
};
export const getOpenSessionsAPI = async () => {
  const { data } = await axiosAuth.get("sessions/open");
  return data;
};

// Users
export const getUsersAPI = async (params: any = {}) => {
  const { data } = await axiosAuth.get("users?" + objectToUrlParams(params));
  return data;
};
export const putUserMeAPI = async (params: any = {}) => {
  const { data } = await axiosAuth.put("user/me", params);
  return data;
};
