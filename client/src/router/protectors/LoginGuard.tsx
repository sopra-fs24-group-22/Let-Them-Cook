import { Navigate, Outlet } from "react-router-dom";
import { State, loadAccessTokenAndUser } from "../../features";
import { Dispatch } from "@reduxjs/toolkit";
import LoadingPage from "../../pages/Loading";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";

export const LoginGuard = () => {
  const { appLoading, isLoggedIn } = useSelector((state: State) => state.app);
  const dispatch: Dispatch<any> = useDispatch();

  useEffect(() => {
    dispatch(loadAccessTokenAndUser());
  }, [dispatch]);

  if (appLoading) {
    return <LoadingPage />;
  }
  if (isLoggedIn) {
    return <Navigate to="/home" replace />;
  }
  return <Outlet />;
};
