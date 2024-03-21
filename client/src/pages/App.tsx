import { useSelector } from "react-redux";
import { State } from "../features";
import Layout from "../components/Layout/MainLayout";

const AppPage = () => {
  const appState = useSelector((state: State) => state.app);
  return (
    <Layout>
      App Page
      <br />
      App State: {JSON.stringify(appState)}
    </Layout>
  );
};
export default AppPage;
