import AppPage from "../pages/App";
import { Navigate, RouterProvider } from "react-router-dom";
import { createBrowserRouter } from "react-router-dom";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to="/app" />,
  },
  {
    path: "/app",
    element: <AppPage />,
  },
  {
    path: "*",
    element: <div>404</div>,
  },
]);

const Router = () => {
  return <RouterProvider router={router} />;
};
export default Router;
