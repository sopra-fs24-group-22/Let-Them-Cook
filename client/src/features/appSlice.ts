import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { User } from "../types/user";
import { getMyUser } from "../api/user.api";
import { refreshAccessTokenAPI } from "../api/app.api";
import { setAccessToken } from "../api/axios";

// State
export interface AppState {
  appLoading: boolean;
  isLoggedIn: boolean;
  user: User;
}
const initialState: AppState = {
  appLoading: true,
  isLoggedIn: false,
  user: { id: 0, username: "", email: "", firstname: "", lastname: "" },
};

// Actions
/**
 * The loadAccessTokenAndUser action is dispatched on page load in the AppGuard component.
 * It fetches the user data from the server and updates the state with the user data.
 * If the request fails, the globalError state is updated with the error message
 * This means that the user is not logged in anymore or has lost permission to access the app.
 */
export const loadAccessTokenAndUser = createAsyncThunk(
  "app/loadAccessTokenAndUser",
  async (_, { dispatch }: any) => {
    const accessToken = await refreshAccessTokenAPI();
    setAccessToken(accessToken);
    const user = await getMyUser();
    return user as User;
  },
);

// Reducers
const appSlice = createSlice({
  name: "app",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder.addCase(loadAccessTokenAndUser.fulfilled, (state, action) => {
      /**
       * Case: User is logged in and fetched successfully
       */
      state.appLoading = false;
      state.isLoggedIn = true;
      state.user = action.payload;
    });
    builder.addCase(loadAccessTokenAndUser.rejected, (state) => {
      /**
       * Case: User is not logged in or lost permission
       */
      state.appLoading = false;
      state.isLoggedIn = false;
    });
  },
});

export default appSlice.reducer;
