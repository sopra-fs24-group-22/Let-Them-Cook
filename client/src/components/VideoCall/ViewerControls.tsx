import { PrimaryButton } from "../ui/Button";
import { useNavigate } from "react-router-dom";

const ViewerControls = () => {
  const navigate = useNavigate();

  return (
    <PrimaryButton
      onClick={() => {
        navigate("/sessions");
      }}
      style={{ marginBottom: "20px" }}
    >
      Leave
    </PrimaryButton>
  );
};

export { ViewerControls };
