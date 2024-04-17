import { useSelector } from "react-redux";
import { State } from "../features";
import Layout from "../components/Layout/MainLayout";
import { getRefreshToken } from "../api/app.api";

const AppPage = () => {
  const appState = useSelector((state: State) => state.app);
  return (
    <Layout>
      App Page
      <br />
      App State: {JSON.stringify(appState)}
      <br />
      <br />
      Group: sopra-fs24-group-22
      <br />
      <br />
      Refresh token: {getRefreshToken()}
    </Layout>
  );
};
export default AppPage;
