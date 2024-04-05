import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout/LoginLayout";
import { Title, Input, Button, HLine, BorderlessButton } from "../components/ui/Login";

const LoginPage = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState<string>();
  const [password, setPassword] = useState<string>();

  const doLogin = () => {
    // TODO: API call
    // postLoginAPI({username: username, password: password});
    console.log({username: username, password: password}); //! DEV ONLY
    // TODO: Save token
    navigate("/home");
  };

  return (
    <Layout>
      <Title>Login</Title>
      <Input
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <Input
        placeholder="Password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <Button onClick={doLogin} disabled={!(username && password)}>Login</Button>
      <HLine />
      <BorderlessButton onClick={() => navigate("/register")} style={{cursor: 'pointer'}}>
        Register instead
      </BorderlessButton>
    </Layout>
  );
};
export default LoginPage;
