import { ReactNode } from "react";
import styled from "styled-components";
import { Wrapper } from "./MainLayout";
import Logo from "../ui/Logo";

interface LoginLayoutProps {
  children?: ReactNode;
}

const LoginLayout: React.FC<LoginLayoutProps> = ({ children }) => {
  return (
    <Wrapper>
      <Navbar>
        <Logo />
      </Navbar>
      <LoginContainer>{children}</LoginContainer>
    </Wrapper>
  );
};

export default LoginLayout;

const LoginContainer = styled.div`
  position: absolute;
  left: 50%;
  top: 140px;
  transform: translateX(-50%);
  width: 450px;
  align-items: center;
  padding: 50px;
  background-color: rgba(256, 256, 256, 0.4);
  border-radius: 20px;
  text-align: center;
  box-shadow: 0px 18px 0px -8px #0000004e;
`;

const Navbar = styled.nav`
  position: absolute;
  top: 30px;
  right: 30px;
  left: 30px;
  display: flex;
  align-items: center;
  padding: 0 40px;
  background-color: #ffffffe2;
  color: black;
  font-size: 2.4rem;
  height: 70px;
  box-shadow: 0px 14px 0px -8px #0000004e;
  border-radius: 20px;
`;
