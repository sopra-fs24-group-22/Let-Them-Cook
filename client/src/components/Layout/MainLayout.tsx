import { ReactNode, useState } from "react";
import styled from "styled-components";
import { Link, useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSignOut } from "@fortawesome/free-solid-svg-icons";
import { faUser } from "@fortawesome/free-regular-svg-icons";
import { Modal } from "react-bootstrap";
import { PrimaryButton, SecondaryButton } from "../ui/Button";
import { postLogoutAPI } from "../../api/app.api";
import { deleteAccessToken } from "../../api/axios";
import Logo from "../ui/Logo";

interface MainLayoutProps {
  children?: ReactNode;
  sidebarContent?: ReactNode;
}

const MainLayout: React.FC<MainLayoutProps> = ({
  children,
  sidebarContent,
}) => {
  const [showLogoutBox, setShowLogoutBox] = useState(false);
  const handleShowLogoutBox = () => setShowLogoutBox(true);
  const handleCloseLogoutBox = () => setShowLogoutBox(false);
  const navigate = useNavigate();
  const logout = async () => {
    try {
      deleteAccessToken();
      await postLogoutAPI({});
      navigate("/login");
    } catch (error) {
      alert("Something went wrong during logout");
    }
  };
  return (
    <Wrapper>
      <Navbar>
        <Logo />
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
            <Link
              to=""
              onClick={(e) => {
                e.preventDefault();
                handleShowLogoutBox();
              }}
            >
              <FontAwesomeIcon icon={faSignOut} />
            </Link>
          </li>
        </ul>
      </Navbar>
      <Main>
        <Sidebar
          style={{
            backgroundColor: sidebarContent ? "#ffffffe2" : "transparent",
            boxShadow: sidebarContent ? "0px 18px 0px -8px #0000004e" : "none",
            width: sidebarContent ? "300px" : "135px",
          }}
        >
          {sidebarContent}
        </Sidebar>
        <Content>{children}</Content>
        {!sidebarContent && <Sidebar style={{ width: "135px" }}></Sidebar>}
      </Main>
      {/*Modal for Logout confirmation*/}
      <Modal show={showLogoutBox} onHide={handleCloseLogoutBox}>
        <Modal.Header>
          <Modal.Title>Are you sure you want to log out?</Modal.Title>
        </Modal.Header>
        <Modal.Footer>
          <SecondaryButton onClick={handleCloseLogoutBox}>
            No, stay logged in
          </SecondaryButton>
          <PrimaryButton onClick={logout}>Yes, log out</PrimaryButton>
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
  margin-right: 30px;
  border-radius: 20px;
  height: fit-content;
`;
const Content = styled.div`
  padding: 30px;
  background-color: #ffffffe2;
  flex: 1;
  border-radius: 20px;
  box-shadow: 0px 18px 0px -8px #0000004e;
  max-height: 1000px;
  overflow-y: auto;
`;
