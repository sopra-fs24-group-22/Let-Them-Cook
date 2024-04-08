import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout/LoginLayout";
import { Title, Input, Button, HLine, BorderlessButton } from "../components/ui/Login";
import { eMailIsValid } from "../helpers/eMailIsValid";
import { postRegisterAPI } from "../api/app.api";
import { setAccessToken } from "../api/axios";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';

const RegisterPage = () => {
  const navigate = useNavigate();
  const [firstname, setFirstname] = useState<string>();
  const [lastname, setLastname] = useState<string>();
  const [username, setUsername] = useState<string>();
  const [email, setEmail] = useState<string>();
  const [emailIsValid, setEmailIsValid] = useState<boolean>(true);
  const [password, setPassword] = useState<string>();
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const changeMail = (email: string) => {
    setEmail(email);
    setEmailIsValid(eMailIsValid(email));
  };

  const doRegistration = () => {
    setIsLoading(true);
    
    const data = {
      firstname: firstname,
      lastname: lastname,
      username: username,
      email: email,
      password: password
    };

    // TODO: API call
    // try {
    //   const accessToken = postRegisterAPI(data);
    //   if (typeof accessToken === "string") {
    //     setAccessToken(accessToken);
    //     navigate("/home");
    //   } else {
    //     alert("Something went wrong. Please try again.");
    //   }
    // } catch (error) {
    //   if(error !== undefined && error !== null && (error as Error).message !== undefined) {
    //     alert("Something went wrong: " + (error as Error).message);
    //   } else {
    //     alert("Something went wrong. Please try again.");
    //   }
    // }

    console.log(data); //! DEV ONLY
    
    // setIsLoading(false);
    setTimeout(() => { setIsLoading(false) }, 3000); //! DEV ONLY
  };

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
        style={emailIsValid ? {} : {border: '1px solid #f00'}}
      />
      <Input
        placeholder="Password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <Button onClick={doRegistration}
        disabled={!(firstname && lastname && username && email && eMailIsValid(email) && password) || isLoading}>
        { !isLoading ? "Register" : (
          <FontAwesomeIcon
            icon={faSpinner}
            spin={true}
          />
        ) }
      </Button>
      <HLine />
      <BorderlessButton onClick={() => navigate("/login")} style={{cursor: 'pointer'}}>
        Login instead
      </BorderlessButton>
    </Layout>
  );
};
export default RegisterPage;
