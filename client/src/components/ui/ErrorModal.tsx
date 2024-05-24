import { SecondaryButton } from "./Button";
import { SecondaryText } from "./Header";
import {
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  ModalTitle,
} from "./Modal";

interface ErrorModalProps {
  error: string;
  title?: string;
  show: boolean;
  onClose: Function;
}

export const ErrorModal = ({
  error,
  title,
  show,
  onClose,
}: ErrorModalProps) => {
  return (
    <Modal show={show} onHide={() => onClose()}>
      <ModalHeader>
        <ModalTitle>{title ? title : "Error"}</ModalTitle>
      </ModalHeader>
      <ModalBody>
        <SecondaryText>{error}</SecondaryText>
      </ModalBody>
      <ModalFooter>
        <SecondaryButton onClick={() => onClose()}>Ok</SecondaryButton>
      </ModalFooter>
    </Modal>
  );
};
