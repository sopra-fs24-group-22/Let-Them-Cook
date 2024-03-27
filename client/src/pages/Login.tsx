import { useSelector } from "react-redux";
import { State } from "../features";
import Layout from "../components/Layout/MainLayout";

const LoginPage = () => {
  const appState = useSelector((state: State) => state.app);
  return (
    <Layout>
      Login
    </Layout>
  );
};
export default LoginPage;
