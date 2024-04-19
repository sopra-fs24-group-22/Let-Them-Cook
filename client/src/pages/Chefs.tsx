import { useSelector } from "react-redux";
import { State } from "../features";
import Layout from "../components/Layout/MainLayout";

const ChefsPage = () => {
    const appState = useSelector((state: State) => state.app);
    return (
        <Layout>
            Chefs
        </Layout>
    );
};
export default ChefsPage;
