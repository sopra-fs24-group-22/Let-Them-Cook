import axios, { AxiosInstance } from "axios";
import { refreshAccessTokenAPI } from "./app.api";

let axiosAuth: AxiosInstance;
let axiosPublic: AxiosInstance;

export const setupAxios = () => {
  axiosPublic = axios.create({
    baseURL: './api/',
  });
  axiosAuth = axios.create({
    baseURL: './api/',
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
          window.location.href = '/login';
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
          axiosAuth.defaults.headers["Authorization"] = "Bearer " + accessToken;
          originalRequest.headers["Authorization"] = "Bearer " + accessToken;
          return axiosAuth(originalRequest);
        }
        return Promise.reject(error);
      } catch (e) {
        console.error("Your session has expired. Please log in again.");
        window.location.href = '/login';
        return Promise.reject(error);
      }
    }
  );
};

// To be called after login
const setAccessToken = (accessToken: string) => {
  if (!axiosAuth) {
    throw new Error("Axios hasn't been initialized, call setupAxios() first");
  }
  axiosAuth.defaults.headers["Authorization"] = "Bearer " + accessToken;
};

// To be called before route change, and redirect to login if not authenticated
const checkIfAuthenticated: () => Promise<boolean> = async () => {
  if (!axiosAuth) {
    throw new Error("Axios hasn't been initialized, call setupAxios() first");
  }
  if (axiosAuth.defaults.headers["Authorization"]) {
    return true;
  }
  try {
    const accessToken = await refreshAccessTokenAPI();
    axiosAuth.defaults.headers["Authorization"] = "Bearer " + accessToken;
    return true;
  } catch {
    console.error("You are not logged in.");
    return false;
  }
};

export { axiosAuth, axiosPublic, setAccessToken, checkIfAuthenticated };
