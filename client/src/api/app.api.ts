import axios from "axios";

export const getThingsAPI = async () => {
  const { data } = await axios.get("/things");
  return data;
};
export const postThingAPI = async (thing: any) => {
  const { data } = await axios.post("/thing", { thing });
  return data;
};
