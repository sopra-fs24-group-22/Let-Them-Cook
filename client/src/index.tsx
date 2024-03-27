import React from "react";
import { createRoot } from "react-dom/client";
import Router from "./router/Router";
import { Provider } from "react-redux";
import store from "./features";
import "./assets/css/globals.css";

const rootElement = document.getElementById("root");
if (!rootElement) throw new Error("Failed to find the root element");
const root = createRoot(rootElement);

root.render(
  <React.StrictMode>
    <Provider store={store}>
      <Router />
    </Provider>
  </React.StrictMode>
);
