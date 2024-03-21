import { ReactNode } from "react";
import styled from "styled-components";

interface MainLayoutProps {
  children?: ReactNode;
}

const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
  return (
    <Wrapper>
      <Navbar>Let them Cook</Navbar>
      <Main>{children}</Main>
    </Wrapper>
  );
};

export default MainLayout;

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  background: rgb(253, 153, 29);
  background: linear-gradient(
    90deg,
    rgba(253, 153, 29, 1) 0%,
    rgba(252, 176, 69, 1) 100%
  );
`;

const Navbar = styled.nav`
  display: flex;
  align-items: center;
  padding: 1.6rem 2.4rem;
  background-color: rgba(0, 0, 0, 0.3);
  color: white;
  font-size: 2.4rem;
`;

const Main = styled.main`
  padding: 2.4rem;
`;
