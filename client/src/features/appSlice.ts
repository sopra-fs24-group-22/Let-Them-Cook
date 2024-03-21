import { createSlice } from "@reduxjs/toolkit";

// State
export interface AppState {
  property: string;
}
const initialState: AppState = {
  property: "Hello World!",
};

// Slice Config
const appSlice = createSlice({
  name: "app",
  initialState,
  reducers: {},
  extraReducers: (builder) => {},
});

export default appSlice.reducer;
