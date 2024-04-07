import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout/LoginLayout";
import { Title, Input, Button, HLine, BorderlessButton } from "../components/ui/Login";
import { eMailIsValid } from "../helpers/eMailIsValid";

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

  const doRegistration = () => {
    // TODO: API call
    const data = {
      firstname: firstname,
      lastname: lastname,
      username: username,
      email: email,
      password: password
    };
    // postRegisterAPI(data);
    console.log(data); //! DEV ONLY
    // TODO: Save token
    navigate("/home");
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
        disabled={!(firstname && lastname && username && email && eMailIsValid(email) && password)}>
        Register
      </Button>
      <HLine />
      <BorderlessButton onClick={() => navigate("/login")} style={{cursor: 'pointer'}}>
        Login instead
      </BorderlessButton>
    </Layout>
  );
};
export default RegisterPage;
