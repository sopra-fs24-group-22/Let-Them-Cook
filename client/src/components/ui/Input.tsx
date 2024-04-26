import styled from "styled-components";

export const Label = styled.label`
  color: #000;
  margin: 0 5px;
  display: block;
  &:after {
    content: "";
    display: block;
  }
`;

export const Input = styled.input`
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
