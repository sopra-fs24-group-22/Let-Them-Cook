import Layout from "../components/Layout/MainLayout";
import { Header1 } from "../components/ui/Header";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faKitchenSet } from "@fortawesome/free-solid-svg-icons";

const AppPage = () => {
  return (
    <Layout>
      <Header1>
        Let them Cook <FontAwesomeIcon icon={faKitchenSet} />
      </Header1>
    </Layout>
  );
};
export default AppPage;
