// Base
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
// Protectors
import { LoginGuard } from "./protectors/LoginGuard";
import { AppGuard } from "./protectors/AppGuard";
// Pages
import AppPage from "../pages/App";
import LoginPage from "../pages/Login";

const Router = () => {
  return (
    <BrowserRouter>
      <Routes>

        /* Login-Page */
        <Route path="/login" element={<LoginGuard />}>
          <Route path="/login" element={<LoginPage />} />
        </Route>

        /* Dashboard */
        <Route path="/home" element={<AppGuard />}>
          <Route path="/home" element={<AppPage />} />
        </Route>

        /* Empty redirection */
        <Route path="/" element={
          <Navigate to="/home" replace />
        } />

        /* Error 404 */
        <Route path="/*" element={
          <div>404</div>
        } />

      </Routes>
    </BrowserRouter>
  );
};

export default Router;
