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

export const DoubleButton = styled.button`
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