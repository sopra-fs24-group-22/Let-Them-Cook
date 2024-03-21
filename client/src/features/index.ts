import { configureStore } from "@reduxjs/toolkit";
import appReducer, { AppState } from "./appSlice";

export interface State {
  app: AppState;
}

const store = configureStore({
  reducer: {
    app: appReducer,
  },
});

export default store;

export * from "./appSlice";
