import { useEffect, useState } from "react";
import Layout from "../components/Layout/MainLayout";
import { Header1 } from "../components/ui/Header";
import { getMyUser } from "../api/user.api";

const AppPage = () => {
  const ERROR_LOADING_DASHBOARD = "Error while loading the dashboard. Please reload the page.";
  useEffect(() => {
    fetchUser();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // User
  const [user, setUser] = useState<any>(null);
  const fetchUser = async () => {
    try {
      const user = await getMyUser();
      console.log(user);
      setUser(user);
    } catch (e) {
      alert(ERROR_LOADING_DASHBOARD);
    }
  };

  // --- RETURN ---
  return (
    <Layout>
      <Header1>
        Let {(user?.firstname) ? user.firstname : 'them'} Cook!
      </Header1>
    </Layout>
  );
};
export default AppPage;
