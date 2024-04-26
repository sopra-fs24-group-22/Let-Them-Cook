import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout/ErrorLayout";
import { Header1 } from "../components/ui/Header";
import { Button } from "../components/ui/Login";

const ErrorPage = () => {
  const navigate = useNavigate();
  return (
    <Layout errorCode="404">
      <Header1>Something's wrong I can feel it.</Header1>
      <img
        src="gordon-ramsay-no.gif"
        width="100%"
				style={{margin: '20px 0'}}
        alt="Nope"
        />
      <Button
        onClick={() => navigate("/")}
        style={{margin: '0'}}>
        Back to the Dashboard</Button>
    </Layout>
  );
};
export default ErrorPage;
