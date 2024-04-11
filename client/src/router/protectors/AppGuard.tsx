import { Navigate, Outlet } from "react-router-dom";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { State, loadUser } from "../../features";
import { Dispatch } from "@reduxjs/toolkit";
import LoadingPage from "../../pages/Loading";

export const AppGuard = () => {
  const { appLoading, isLoggedIn } = useSelector((state: State) => state.app);
  const dispatch: Dispatch<any> = useDispatch();

  /**
   * Fetch user data on page load
   */
  useEffect(() => {
    dispatch(loadUser());
  }, [dispatch]);

  /**
   * If app is loading, show loading page
   * If user is logged in, show the app
   * If user is not logged in, redirect to login page
   */
  if (appLoading) {
    return <LoadingPage />;
  }
  if (isLoggedIn) {
    return <Outlet />;
  }
  return <Navigate to="/login" replace />;
};
