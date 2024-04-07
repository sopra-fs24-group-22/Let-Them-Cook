import axios from "axios";

// Login
export const postLoginAPI = async (login: any) => {
  const { data } = await axios.post("./api/auth/login", { login });
  return data;
};
export const postRegisterAPI = async (register: any) => {
  const { data } = await axios.post("./api/auth/register", { register });
  return data;
};

// Recipes
export const getAllRecipesAPI = async () => {
  const { data } = await axios.get("./api/recipes");
  return data;
};

// Sessions
export const postSessionAPI = async (session: any) => {
  const { data } = await axios.post("./api/session", { session });
  return data;
};