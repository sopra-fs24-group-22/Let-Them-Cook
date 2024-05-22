import { postLogoutAPI } from "../api/app.api";
import { deleteAccessToken } from "../api/axios";

export const logout = async (navigate: Function) => {
  try {
    deleteAccessToken();
    await postLogoutAPI({});
    navigate("/login");
  } catch (error) {
    alert("Something went wrong during logout");
  }
};
