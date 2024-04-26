import Layout from "../components/Layout/LoginLayout";
import { Header1 } from "../components/ui/Header";

const ErrorPage = () => {
  return (
    <Layout>
      <Header1>Something's wrong I can feel it.</Header1>
      <img
        src="gordon-ramsay-no.gif"
        width="100%"
				style={{margin: '20px 0 10px 0'}}
        alt="Nope"
        />
    </Layout>
  );
};
export default ErrorPage;
