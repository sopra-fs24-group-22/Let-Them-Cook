import { Navigate, Outlet } from "react-router-dom";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { State, loadAccessTokenAndUser } from "../../features";
import { Dispatch } from "@reduxjs/toolkit";
import LoadingPage from "../../pages/Loading";

export const AppGuard = () => {
  const { appLoading, isLoggedIn } = useSelector((state: State) => state.app);
  const dispatch: Dispatch<any> = useDispatch();

  useEffect(() => {
    dispatch(loadAccessTokenAndUser());
  }, [dispatch]);

  if (appLoading) {
    return <LoadingPage />;
  }
  if (isLoggedIn) {
    return <Outlet />;
  }
  return <Navigate to="/login" replace />;
};
