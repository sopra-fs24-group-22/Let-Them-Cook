import React, { useState } from "react";
import { Modal } from "react-bootstrap";
import styled from "styled-components";
import { SecondaryButton } from "./Button";
import { useNavigate } from "react-router-dom";

export default function Logo() {
  const [easterEggClickCounter, setEasterEggClickCounter] = useState(0);
  const navigate = useNavigate();
  const handleNavigateHome = () => {
    if(window.location.pathname !== '/home') {
        navigate("/");
    }
  };
  return (
    <>
      <LogoBox className="logo-box" onClick={handleNavigateHome}>
        <LogoText
          onClick={() => {
            setEasterEggClickCounter(easterEggClickCounter + 1);
          }}
        >
          Let them Cook
        </LogoText>
      </LogoBox>
      {/* Modal just for fun */}
      <Modal
        show={easterEggClickCounter === 5}
        onHide={() => setEasterEggClickCounter(0)}
        backdrop="static"
        keyboard={false}
      >
        <Modal.Body>
          <img
            src={window.location.origin + "/gordon-ramsay-what-are-you.gif"}
            width="100%"
            alt="Idiot sandwich"
          />
        </Modal.Body>
        <Modal.Footer>
          <SecondaryButton onClick={() => setEasterEggClickCounter(0)}>
            I'm an idiot sandwich
          </SecondaryButton>
        </Modal.Footer>
      </Modal>
    </>
  );
}

const LogoBox = styled.div`
  font-family: "Pacifico", cursive;
  font-weight: 400;
  font-style: normal;
  display: flex;
  align-items: center;
  font-size: 2.6rem;
  font-weight: 500;
  color: #b46733;
  cursor: pointer;
`;

const LogoText = styled.i`
  -webkit-touch-callout: none; /* iOS Safari */
  -webkit-user-select: none; /* Safari */
  -khtml-user-select: none; /* Konqueror HTML */
  -moz-user-select: none; /* Old versions of Firefox */
  -ms-user-select: none; /* Internet Explorer/Edge */
  user-select: none; /* Non-prefixed version, currently supported by Chrome, Edge, Opera and Firefox */
`;
