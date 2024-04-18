import axios from "axios";
import { refreshAccessTokenAPI } from "./app.api";
import { getBaseUrl } from "../helpers/baseUrl";

const axiosPublic = axios.create({
  baseURL: getBaseUrl() + "/api",
});
const axiosAuth = axios.create({
  baseURL: getBaseUrl() + "/api",
});

// Check if request contains Access Token, if not get it and put it into headers
axiosAuth.interceptors.request.use(
  async (config: any) => {
    if (!axiosAuth.defaults.headers["Authorization"]) {
      try {
        const accessToken = await refreshAccessTokenAPI();
        axiosAuth.defaults.headers["Authorization"] = "Bearer " + accessToken;
        config.headers["Authorization"] = "Bearer " + accessToken;
      } catch {
        console.error("Your session has expired. Please log in again.");
        window.location.href = "/login";
      }
    }
    return config;
  },
  (error: any) => Promise.reject(error)
);

// Check if response is error 403, if yes refresh Access token
axiosAuth.interceptors.response.use(
  (res: any) => res,
  async (error: any) => {
    try {
      const originalRequest = error.config;
      if (error.response.status === 403 && !originalRequest._retry) {
        originalRequest._retry = true;
        const accessToken = await refreshAccessTokenAPI();
        console.log("Access Token: " + accessToken);
        axiosAuth.defaults.headers["Authorization"] = "Bearer " + accessToken;
        originalRequest.headers["Authorization"] = "Bearer " + accessToken;
        return axiosAuth(originalRequest);
      }
      return Promise.reject(error);
    } catch (e) {
      console.error("Your session has expired. Please log in again.");
      // TODO: something's wrong I can feel it
      // window.location.href = "/login";
      return Promise.reject(error);
    }
  }
);

// To be called after login
const setAccessToken = (accessToken: string) => {
  axiosAuth.defaults.headers["Authorization"] = "Bearer " + accessToken;
};
const deleteAccessToken = () => {
  delete axiosAuth.defaults.headers["Authorization"];
};

export { axiosAuth, axiosPublic, setAccessToken, deleteAccessToken };
