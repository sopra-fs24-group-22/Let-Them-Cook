import React from "react";
import styled from "styled-components";

export const Modal: React.FC<{
  children: React.ReactNode;
  show: boolean;
  onHide: () => void;
}> = ({ children, show, onHide }) => {
  return (
    <div
      style={{
        transition: "all 0.2s",
        visibility: show ? "visible" : "hidden",
        opacity: show ? "1" : "0",
      }}
    >
      <Overlay onClick={onHide}></Overlay>
      <CustomModal
        style={{
          transform: show
            ? "translate(-50%, -50%) scale(1)"
            : "translate(-50%, -50%) scale(0.3)",
        }}
      >
        {children}
      </CustomModal>
    </div>
  );
};

export const ModalHeader = styled.h3`
  display: block;
  color: #85705d;
  font-size: 2.2rem;
  padding-bottom: 25px;
  margin-bottom: 25px;
  border-bottom: 2px solid #ccc;
`;
export const ModalFooter = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  padding-top: 25px;
  margin-top: 25px;
  border-top: 2px solid #ccc;

  & > * {
    margin-left: 5px;
  }
`;
export const ModalTitle = styled.div``;
export const ModalBody = styled.div`
  max-height: 600px;
  overflow-y: auto;
`;

const Overlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #0000004e;
  z-index: 10000;
`;

const CustomModal = styled.div`
  transition: all 0.2s;
  padding: 30px;
  background-color: #f1f1f1;
  flex: 1;
  border-radius: 20px;
  box-shadow: 0px 18px 0px -8px #0000004e;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 600px;
  z-index: 100000;
`;
