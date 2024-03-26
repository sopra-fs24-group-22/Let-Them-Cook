import { Navigate, Outlet } from "react-router-dom";
import PropTypes from "prop-types";

/**
 * Checks if user is not logged in
 * @Guard
 * @param props
 */
export const LoginGuard = () => {
  // TODO: login check
  if (true) {
    return <Outlet />;
  }

  return <Navigate to="/login" replace />;
};

LoginGuard.propTypes = {
  children: PropTypes.node
};