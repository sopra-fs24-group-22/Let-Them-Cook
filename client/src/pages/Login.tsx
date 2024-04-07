import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout/LoginLayout";
import { Title, Input, Button, HLine, BorderlessButton } from "../components/ui/Login";
import { eMailIsValid } from "../helpers/eMailIsValid";
import { postLoginAPI } from "../api/app.api";
import { setAccessToken } from "../api/axios";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';

const LoginPage = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState<string>();
  const [emailIsValid, setEmailIsValid] = useState<boolean>(true);
  const [password, setPassword] = useState<string>();
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const changeMail = (email: string) => {
    setEmail(email);
    setEmailIsValid(eMailIsValid(email));
  };

  const doLogin = () => {
    setIsLoading(true);

    // TODO: API call
    // try {
    //   const accessToken = postLoginAPI({email: email, password: password});

    //   if (typeof accessToken === "string") {
    //     setAccessToken(accessToken);
    //     navigate("/home");
    //   } else {
    //     alert("Something went wrong. Please try again.");
    //   }
    // } catch (error) {
    //   alert("Your credentials are incorrect.");
    // }

    console.log({email: email, password: password}); //! DEV ONLY

    // setIsLoading(false);
    setTimeout(() => { setIsLoading(false) }, 3000); //! DEV ONLY
  };

  return (
    <Layout>
      <Title>Login</Title>
      <Input
        placeholder="E-Mail"
        type="email"
        value={email}
        onChange={(e) => changeMail(e.target.value)}
        style={emailIsValid ? {} : {border: '1px solid #f00'}}
      />
      <Input
        placeholder="Password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <Button onClick={doLogin} disabled={!(email && eMailIsValid(email) && password) || isLoading}>
      { !isLoading ? "Log in" : (
          <FontAwesomeIcon
            icon={faSpinner}
            spin={true}
          />
        ) }
      </Button>
      <HLine />
      <BorderlessButton onClick={() => navigate("/register")} style={{cursor: 'pointer'}}>
        Register instead
      </BorderlessButton>
    </Layout>
  );
};
export default LoginPage;
