import styled from "styled-components";

export const Title = styled.h1`
  color: #804e15;
  font-size: 2.4rem;
  font-weight: 600;
  margin-bottom: 30px;
`;

export const Input = styled.input`
  color: #251f18;
  font-weight: 500;
  font-size: 1.7rem;
  background-color: rgba(0, 0, 0, 0);
  margin-bottom: 20px;
  padding: 10px 0;
  border-radius: 0;
  background-color: transparent;
  border: none;
  border-bottom: 3px solid #8b5a2c;
  width: 350px;
  &:focus {
    border-bottom: 3px solid #da7e16;
  }
  outline: none;
  &::placeholder {
    color: #744e23;
  }
`;

export const Button = styled.button`
  color: #744e23;
  background: linear-gradient(
    90deg,
    rgba(252, 176, 69, 1) 0%,
    rgba(253, 153, 29, 1) 100%
  );
  border-radius: 5px;
  box-shadow: 0px 14px 0px -8px #0000004e;
  margin-top: 20px;
  margin-bottom: 20px;
  font-weight: 600;
  font-size: 1;
  padding: 10px;
  border: none;
  cursor: pointer;
  width: 350px;
  transition: all 0.2s;
  &:hover {
    box-shadow: 0px 14px 0px -7px #0000004e;
    transform: translateY(-2px);
  }
  &:disabled {
    cursor: default;
    box-shadow: none;
    border: none;
    color: #94652f;
  }
  &:disabled:hover {
  }
`;

export const HLine = styled.hr`
  margin: 20px 0;
  border-top: 2px solid #b1a99b;
  opacity: 1;
`;

export const BorderlessButton = styled.p`
  font-weight: 600;
  color: #8b5a2c;
`;
