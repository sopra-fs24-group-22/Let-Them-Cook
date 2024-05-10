import { axiosAuth } from "./axios";
import { objectToUrlParams } from "../helpers/objectToUrlParams";

export const getMyUser = async () => {
  const { data } = await axiosAuth.get("user/me");
  return data;
};
export const getUsers = async (params: any = {}) => {
  const { data } = await axiosAuth.get("users?" + objectToUrlParams(params));
  return data;
};
