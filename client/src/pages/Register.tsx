import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout/LoginLayout";
import {
  Title,
  Input,
  Button,
  HLine,
  BorderlessButton,
} from "../components/ui/Login";
import { eMailIsValid } from "../helpers/eMailIsValid";
import { postRegisterAPI } from "../api/app.api";
import { setAccessToken } from "../api/axios";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner } from "@fortawesome/free-solid-svg-icons";

const RegisterPage = () => {
  const navigate = useNavigate();
  const [firstname, setFirstname] = useState<string>();
  const [lastname, setLastname] = useState<string>();
  const [username, setUsername] = useState<string>();
  const [email, setEmail] = useState<string>();
  const [emailIsValid, setEmailIsValid] = useState<boolean>(true);
  const [password, setPassword] = useState<string>();

  const changeMail = (email: string) => {
    setEmail(email);
    setEmailIsValid(eMailIsValid(email));
  };

  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string>();

  const register = async () => {
    setIsLoading(true);
    const body = {
      firstname,
      lastname,
      username,
      email,
      password,
    };
    try {
      const res = await postRegisterAPI(body);
      const { accessToken } = res;
      setAccessToken(accessToken);
      navigate("/home");
    } catch (error) {
      setErrorMessage("Registration failed. Please try again.");
    }
    setIsLoading(false);
  };

  const isValid = () =>
    firstname &&
    lastname &&
    username &&
    email &&
    eMailIsValid(email) &&
    password;

  return (
    <Layout>
      <Title>Register</Title>
      <Input
        placeholder="First name"
        value={firstname}
        onChange={(e) => setFirstname(e.target.value)}
      />
      <Input
        placeholder="Last name"
        value={lastname}
        onChange={(e) => setLastname(e.target.value)}
      />
      <Input
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <Input
        placeholder="E-Mail"
        type="email"
        value={email}
        onChange={(e) => changeMail(e.target.value)}
        style={emailIsValid ? {} : { border: "1px solid #f00" }}
      />
      <Input
        placeholder="Password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <Button onClick={register} disabled={!isValid() || isLoading}>
        {!isLoading ? (
          "Register"
        ) : (
          <FontAwesomeIcon icon={faSpinner} spin={true} />
        )}
      </Button>
      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
      <HLine />
      <BorderlessButton
        onClick={() => navigate("/login")}
        style={{ cursor: "pointer" }}
      >
        Login instead
      </BorderlessButton>
    </Layout>
  );
};
export default RegisterPage;
