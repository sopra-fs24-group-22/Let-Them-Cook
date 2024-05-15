import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout/LoginLayout";
import { Input, Button, BorderlessButton } from "../components/ui/Login";
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
      firstname: firstname,
      lastname: lastname,
      username: username,
      email: email,
      password: password,
    };
    try {
      const res = await postRegisterAPI(body);
      const accessToken = res;
      setAccessToken(accessToken);
      navigate("/home");
    } catch (error: any) {
      if (error.code === "ERR_BAD_REQUEST")
        setErrorMessage(error.response.data.split('"')[1]);
      else setErrorMessage("Registration failed. Please try again.");
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

  const handleEnter = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && isValid()) register();
  };

  return (
    <Layout>
      <Input
        placeholder="First name"
        value={firstname}
        onChange={(e) => setFirstname(e.target.value)}
        onKeyDown={(e) => handleEnter(e)}
      />
      <Input
        placeholder="Last name"
        value={lastname}
        onChange={(e) => setLastname(e.target.value)}
        onKeyDown={(e) => handleEnter(e)}
      />
      <Input
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        onKeyDown={(e) => handleEnter(e)}
      />
      <Input
        placeholder="E-Mail"
        type="email"
        value={email}
        onChange={(e) => changeMail(e.target.value)}
        onKeyDown={(e) => handleEnter(e)}
        style={
          emailIsValid ? {} : { borderBottom: "3px solid #f00", color: "#f00" }
        }
      />
      <Input
        placeholder="Password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        onKeyDown={(e) => handleEnter(e)}
      />
      <Button onClick={register} disabled={!isValid() || isLoading}>
        {!isLoading ? (
          "Register"
        ) : (
          <FontAwesomeIcon icon={faSpinner} spin={true} />
        )}
      </Button>
      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
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
