import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner } from "@fortawesome/free-solid-svg-icons";
import styled from "styled-components";

const LoadingPage = () => {
  return (
    <SpinnerContainer>
      <FontAwesomeIcon icon={faSpinner} spin={true} />
    </SpinnerContainer>
  );
};
export default LoadingPage;

const SpinnerContainer = styled.div`
  display: flex;
  height: 100%;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
`;
