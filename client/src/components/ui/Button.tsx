import styled from "styled-components";
import React, { ReactNode } from "react";

export const PrimaryButton = styled.button`
  background-color: #8b5858;
  color: white;
  padding: 5px 15px;
  border-radius: 5px;
  outline: 0;
  border: 0;
  text-transform: uppercase;
  cursor: pointer;
  &:hover {
    background-color: #7c5050;
  }
  &:disabled {
    cursor: default;
    opacity: 0.7;
  }
`;

export const SecondaryButton = styled.button`
  background-color: #878787;
  color: white;
  padding: 5px 15px;
  border-radius: 5px;
  outline: 0;
  border: 0;
  text-transform: uppercase;
  cursor: pointer;
  &:hover {
    background-color: #737373;
  }
  &:disabled {
    cursor: default;
    opacity: 0.7;
  }
`;

interface MJoinButtonProps {
  children?: ReactNode;
  onClick?: Function | undefined;
  style?: any;
}
export const JoinButton: React.FC<MJoinButtonProps> = ({
  children,
  onClick,
  style,
}) => (
  <PrimaryButton
    style={Object.assign(
      {
        fontSize: "1.3rem",
        fontWeight: 500,
        margin: "5px",
      },
      style,
    )}
    onClick={
      onClick
        ? (event: any) => {
            onClick();
            event.stopPropagation();
          }
        : void 0
    }
  >
    {children}
  </PrimaryButton>
);

export const ButtonGroup = styled.div`
  width: 100%;
`;

export const HLine = styled.hr`
  margin: 20px 0;
  border-top: 2px solid #b1a99b;
  opacity: 1;
`;
