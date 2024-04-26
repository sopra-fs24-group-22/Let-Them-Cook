import styled from "styled-components";

export const PrimaryButton = styled.button`
  background-color: #8B5858;
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

export const JoinButton = styled.button`
    background-color: #1fdb33;
    color: #000000;
    padding: 7px 15px;
    border-radius: 5px;
    outline: 0;
    border: 0;
    text-transform: uppercase;
    cursor: pointer;
    font-size: 1.3rem;
    font-weight: 500;
    &:hover {
        background-color: #18af28;
        color: #fff;
    }

    &:disabled {
        cursor: default;
        opacity: 0.7;
    }
`;

export const ButtonGroup = styled.div`
  width: 100%;
`;