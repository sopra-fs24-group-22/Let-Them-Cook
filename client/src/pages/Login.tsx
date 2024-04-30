import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout/LoginLayout";
import { Input, Button, BorderlessButton } from "../components/ui/Login";
import { postLoginAPI } from "../api/app.api";
import { setAccessToken } from "../api/axios";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner } from "@fortawesome/free-solid-svg-icons";

const LoginPage = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState<string>();
  const [password, setPassword] = useState<string>();

  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string>();

  const login = async () => {
    setIsLoading(true);

    const body = {
      username: username,
      password: password,
    };

    try {
      const accessToken = await postLoginAPI(body);
      setAccessToken(accessToken);
      navigate("/home");
    } catch (error) {
      setErrorMessage("Login failed. Please try again.");
    }
    setIsLoading(false);
  };

  const handleEnter = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && username && password) login();
  };

  return (
    <Layout>
      <Input
        placeholder="Username"
        type="username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        onKeyDown={(e) => handleEnter(e)}
      />
      <Input
        placeholder="Password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        onKeyDown={(e) => handleEnter(e)}
      />
      <Button onClick={login} disabled={!(username && password) || isLoading}>
        {!isLoading ? (
          "Login"
        ) : (
          <FontAwesomeIcon icon={faSpinner} spin={true} />
        )}
      </Button>
      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
      <BorderlessButton
        onClick={() => navigate("/register")}
        style={{ cursor: "pointer" }}
      >
        Register instead
      </BorderlessButton>
    </Layout>
  );
};
export default LoginPage;
