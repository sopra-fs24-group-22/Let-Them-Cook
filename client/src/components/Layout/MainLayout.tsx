import { ReactNode, useState } from "react";
import styled from "styled-components";
import {Link, useNavigate} from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSignOut } from "@fortawesome/free-solid-svg-icons";
import { faUser } from "@fortawesome/free-regular-svg-icons";
import { Modal } from 'react-bootstrap';
import {PrimaryButton, SecondaryButton} from "../ui/Button";
// import {postLogoutAPI} from "../../api/app.api";
import {deleteAccessToken} from "../../api/axios";

interface MainLayoutProps {
  children?: ReactNode;
  sidebarContent?: ReactNode;
}

const MainLayout: React.FC<MainLayoutProps> = ({
  children,
  sidebarContent,
}) => {
  const [easterEggClickCounter, setEasterEggClickCounter] = useState(0);
  const [showLogoutBox, setShowLogoutBox] = useState(false);
  const handleShowLogoutBox = () => setShowLogoutBox(true);
  const handleCloseLogoutBox = () => setShowLogoutBox(false);
  const navigate = useNavigate();
  const logout = async () => {
    try {
      // what now?
      deleteAccessToken();
      navigate("/login");
    } catch (error) {
      alert(
          "Something went wrong during logout"
      )
    }
  }
  return (
    <Wrapper>
      <Navbar>
        <div className="logo-box">
          <LogoText
            onClick={() => {setEasterEggClickCounter(easterEggClickCounter + 1);}}
          >Let them Cook</LogoText>
        </div>
        <ul>
          <li>
            <Link to="/home">Home</Link>
          </li>

          <li>
            <Link to="/recipes">Recipes</Link>
          </li>

          <li>
            <Link to="/sessions">Sessions</Link>
          </li>

          <li>
            <Link to="/chefs">Chefs</Link>
          </li>
          <li>
            <Link to="/chefs">
              <FontAwesomeIcon icon={faUser} />
            </Link>
          </li>

          <li>
            <Link to="" onClick={(e) => { e.preventDefault(); handleShowLogoutBox(); }}>
              <FontAwesomeIcon icon={faSignOut} />
            </Link>
          </li>
        </ul>
      </Navbar>
      <Main>
        <Sidebar>{sidebarContent}</Sidebar>
        <Content>{children}</Content>
      </Main>
      {/* Modal just for fun */}
      <Modal show={easterEggClickCounter === 5}
             onHide={() => setEasterEggClickCounter(0)}
             backdrop="static"
             keyboard={false}>
        <Modal.Body>
          <img src="gordon-ramsay-what-are-you.gif" width="100%" alt="Idiot sandwich" />
        </Modal.Body>
        <Modal.Footer>
          <SecondaryButton onClick={() => setEasterEggClickCounter(0)}>I'm an idiot sandwich</SecondaryButton>
        </Modal.Footer>
      </Modal>
      {/*Modal for Logout confirmation*/}
      <Modal show={showLogoutBox} onHide={handleCloseLogoutBox}>
        <Modal.Header>
          <Modal.Title>Are you sure you want to log out?</Modal.Title>
        </Modal.Header>
        <Modal.Footer>
          <SecondaryButton onClick={handleCloseLogoutBox}>
            No, stay logged in
          </SecondaryButton>
          <PrimaryButton onClick={logout}>
            Yes, log out
          </PrimaryButton>
        </Modal.Footer>
      </Modal>
    </Wrapper>
  );
};

export default MainLayout;

export const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  min-height: 100%;
  background: rgb(253, 153, 29);
  background: linear-gradient(
    90deg,
    rgba(253, 153, 29, 1) 0%,
    rgba(252, 176, 69, 1) 100%
  );
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
  img {
    height: 40px;
  }
  border-radius: 20px;
  transition: top 0.2s;
  &:hover {
    top: 25px;
  }

  .logo-box {
    font-family: "Pacifico", cursive;
    font-weight: 400;
    font-style: normal;
    display: flex;
    align-items: center;
    font-size: 2.6rem;
    font-weight: 500;
    color: #b46733;
  }

  ul {
    display: flex;
    margin-bottom: 0;
    align-items: center;
    list-style: none;
    height: 100%;
    margin-left: auto;
    li {
      margin-left: 30px;
      font-size: 1.8rem;
    }
    a {
      text-decoration: none;
      color: #8b684b;
      font-weight: 500;
      &:hover {
        border-bottom: 2px solid #8b684b;
      }
    }
  }
`;

const Main = styled.main`
  padding: 130px 30px 30px 30px;
  display: flex;
  flex-direction: row;
  height: 100%; 
`;

const Sidebar = styled.div`
  padding: 30px;
  background-color: #ffffffe2;
  height: 200px;
  width: 300px;
  margin-right: 30px;
  border-radius: 20px;
  box-shadow: 0px 18px 0px -8px #0000004e;
  transition: transform 0.2s;
  &:hover {
    transform: translateY(-5px);
  }
`;
const Content = styled.div`
  padding: 30px;
  background-color: #ffffffe2;
  flex: 1;
  border-radius: 20px;
  box-shadow: 0px 18px 0px -8px #0000004e;
  transition: transform 0.2s;
  max-height: 600px;
  overflow-y: auto;
  &:hover {
    transform: translateY(-5px);
  }
`;
const LogoText = styled.i`
  -webkit-touch-callout: none; /* iOS Safari */
  -webkit-user-select: none; /* Safari */
  -khtml-user-select: none; /* Konqueror HTML */
  -moz-user-select: none; /* Old versions of Firefox */
  -ms-user-select: none; /* Internet Explorer/Edge */
  user-select: none; /* Non-prefixed version, currently supported by Chrome, Edge, Opera and Firefox */
`;