import styled from "styled-components";
import React, { ReactNode } from "react";

export const PrimaryButton = styled.button`
  background: linear-gradient(
    90deg,
    rgba(253, 153, 29, 1) 0%,
    rgba(252, 176, 69, 1) 100%
  );
  color: white;
  padding: 8px 16px;
  font-weight: 500;
  border-radius: 16px;
  outline: 0;
  border: 0;
  text-transform: uppercase;
  cursor: pointer;
  transition: transform 0.2s;
  &:hover {
    background-color: #7c5050;
    transform: scale(1.05);
  }
  &:disabled {
    cursor: default;
    opacity: 0.7;
  }
`;

export const SimplePrimaryButton = styled(PrimaryButton)`
  &:hover {
    filter: brightness(90%);
    transform: scale(1);
  }
`;

export const SecondaryButton = styled.button`
  background: linear-gradient(90deg, #bebebe 0%, #a9a9a9 100%);
  color: white;
  padding: 8px 16px;
  border-radius: 16px;
  font-weight: 500;
  outline: 0;
  border: 0;
  text-transform: uppercase;
  cursor: pointer;
  transition: transform 0.2s;
  &:hover {
    background-color: #737373;
    transform: scale(1.05);
  }
  &:disabled {
    cursor: default;
    opacity: 0.7;
  }
`;

export const SimpleSecondaryButton = styled(SecondaryButton)`
  &:hover {
    filter: brightness(90%);
    transform: scale(1);
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
