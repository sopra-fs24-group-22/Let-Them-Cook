import styled from "styled-components";

export const Title = styled.h1`
  color: #000;
  margin: 10px 0 30px 0;
`;

export const Input = styled.input`
  color: #000;
  background-color: rgba(0,0,0,0);
  margin: 5px 0;
  padding: 5px;
  border-radius: 5px;
  border: 1px solid #000; 
  width: 350px;
  &:hover {
    background-color: rgba(0, 0, 0, 0.1);
  }
  &:focus {
    background-color: rgba(0, 0, 0, 0.1);
  }
  &::placeholder {
    color: #000;
  }
`;

export const Button = styled.button`
  color: #000;
  background-color: rgba(0, 0, 0, 0);
  margin: 5px 0;
  padding: 5px;
  border-radius: 5px;
  border: 1px solid #000; 
  cursor: pointer;
  width: 350px;
  &:hover {
    background-color: rgba(0, 0, 0, 0.1);
  }
  &:disabled {
    cursor: default;
    color: rgba(0, 0, 0, 0.5);
    border: 1px solid rgba(0, 0, 0, 0.5);
  }
  &:focus {
    background-color: rgba(0, 0, 0, 0.1);
  }
  &:disabled:hover {
    background-color: rgba(0, 0, 0, 0);
  }
`;

export const HLine = styled.hr`
  margin: 20px 0;
  border-top: 1px solid #000;
  opacity: 1;
`;

export const BorderlessButton = styled.p`
  margin: 0;
`;