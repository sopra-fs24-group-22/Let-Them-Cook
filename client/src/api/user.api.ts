import { axiosAuth } from "./axios";

export const getMyUser = async () => {
  const { data } = await axiosAuth.get("user/me");
  return data;
};
