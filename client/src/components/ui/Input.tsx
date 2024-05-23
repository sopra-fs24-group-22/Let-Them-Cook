import styled from "styled-components";

export const Label = styled.label`
  color: #867b77;
  font-weight: 500;
  margin: 0 5px 4px 0;
  display: block;
  &:after {
    content: "";
    display: block;
  }
`;

export const Input = styled.input`
  color: #4e4e4e;
  padding: 8px 16px;
  margin: 10px 0 20px 0;
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 5px #d9d9d9;
  &:disabled {
    cursor: default;
    opacity: 0.7;
  }
`;

export const Select = styled.select`
  color: #000;
  padding: 5px 5px;
  margin: 10px 5px 20px 5px;
  border-radius: 5px;
  border: 1px solid #d9d9d9;
  box-shadow: 0 2px 5px #d9d9d9;
  &:disabled {
    cursor: default;
    opacity: 0.7;
  }
`;

export const Option = styled.option``;
