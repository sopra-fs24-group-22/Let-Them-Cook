import { ReactNode } from "react";
import styled from "styled-components";
import { Wrapper } from "./MainLayout";

interface LoginLayoutProps {
  children?: ReactNode;
}

const LoginLayout: React.FC<LoginLayoutProps> = ({ children }) => {
  return (
    <Wrapper>
      <LoginContainer>
        <LogoContainer>
          <img src="logo.svg" alt="React Logo" width="100px" />
        </LogoContainer>
        <br />
        {children}
      </LoginContainer>
    </Wrapper>
  );
};

export default LoginLayout;

const LoginContainer = styled.div`
  margin-left: calc((100% - 450px) / 2);
  margin-top: 5%;
  width: 450px;
  align-items: center;
  padding: 50px;
  background-color: rgba(256, 256, 256, 0.4);
  border-radius: 20px;
  text-align: center;
`;

const LogoContainer = styled.div`
  width: 100%;
`;