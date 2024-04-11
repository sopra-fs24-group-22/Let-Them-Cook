import { Navigate, Outlet } from "react-router-dom";
import PropTypes from "prop-types";

/**
 * Checks if refresh token cookie exits and is valid
 */
const isAuthenticated = () => {
  const refreshToken = document.cookie.split(';').find(c => c.trim().startsWith('refreshToken='));
  return refreshToken ? true : false;
};

/**
 * Checks if user is not logged in
 * @Guard
 * @param props
 */
export const LoginGuard = () => {
  // TODO: login check
  if (!isAuthenticated()) {
    return <Outlet />;
  }

  return <Navigate to="/login" replace />;
};

LoginGuard.propTypes = {
  children: PropTypes.node
};