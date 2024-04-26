// Base
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
// Protectors
import { LoginGuard } from "./protectors/LoginGuard";
import { AppGuard } from "./protectors/AppGuard";
// Pages
import AppPage from "../pages/App";
import LoginPage from "../pages/Login";
import ProfilePage from "../pages/Profile";
import SessionsPage from "../pages/Sessions";
import RegisterPage from "../pages/Register";
import RecipesPage from "../pages/Recipes";
import CookbookPage from "../pages/Cookbook";
import SessionViewerPage from "../pages/SessionViewer";
import ChefsPage from "../pages/Chefs";
import ChefsFollowedPage from "../pages/ChefsFollowed";
import ErrorPage from "../pages/Error";

const Router = () => {
  return (
    <BrowserRouter basename="/">
      <Routes>
        {/* Login-Page */}
        <Route path="/login" element={<LoginGuard />}>
          <Route path="/login" element={<LoginPage />} />
        </Route>

        {/* Register-Page */}
        <Route path="/register" element={<LoginGuard />}>
          <Route path="/register" element={<RegisterPage />} />
        </Route>

        {/* Dashboard */}
        <Route path="/home" element={<AppGuard />}>
          <Route path="/home" element={<AppPage />} />
        </Route>

        {/* Sessions */}
        <Route path="/sessions" element={<AppGuard />}>
          <Route path="/sessions" element={<SessionsPage />} />
        </Route>

        {/* In session */}
        <Route path="/sessions/:sessionID" element={<AppGuard />}>
          <Route path="/sessions/:sessionID" element={<SessionViewerPage />} />
        </Route>

        {/* Profile */}
        <Route path="/profile/:user" element={<AppGuard />}>
          <Route path="/profile/:user" element={<ProfilePage />} />
        </Route>

        {/* Recipes */}
        <Route path="/recipes" element={<AppGuard />}>
          <Route path="/recipes" element={<RecipesPage />} />
        </Route>

        {/* Cookbook */}
        <Route path="/recipes/cookbook/:user" element={<AppGuard />}>
          <Route path="/recipes/cookbook/:user" element={<CookbookPage />} />
        </Route>

        {/* Chefs */}
        <Route path="/chefs" element={<AppGuard />}>
          <Route path="/chefs" element={<ChefsPage />} />
        </Route>

        {/* Followed Chefs */}
        <Route path="/chefs/mychefs/:user" element={<AppGuard />}>
          <Route path="/chefs/mychefs/:user" element={<ChefsFollowedPage />} />
        </Route>

        {/* Empty redirection */}
        <Route path="/" element={<Navigate to="/home" replace />} />

        {/* Error 404 */}
        <Route path="/*" element={<ErrorPage />} />
      </Routes>
    </BrowserRouter>
  );
};

export default Router;
