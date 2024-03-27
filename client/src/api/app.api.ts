import axios from "axios";

// Things
export const getThingsAPI = async () => {
  const { data } = await axios.get("./api/things");
  return data;
};
export const postThingAPI = async (thing: any) => {
  const { data } = await axios.post("./api/thing", { thing });
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