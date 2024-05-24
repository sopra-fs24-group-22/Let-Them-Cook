import { PrimaryButton, SecondaryButton } from "./Button";
import { SecondaryText } from "./Header";
import {
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  ModalTitle,
} from "./Modal";

interface ConfirmModalProps {
  error: string;
  title?: string;
  show: boolean;
  onAccept: Function;
  onDecline: Function;
}

export const ConfirmModal = ({
  error,
  title,
  show,
  onAccept,
  onDecline,
}: ConfirmModalProps) => {
  return (
    <Modal show={show} onHide={() => onDecline()}>
      <ModalHeader>
        <ModalTitle>{title}</ModalTitle>
      </ModalHeader>
      <ModalBody>
        <SecondaryText>{error}</SecondaryText>
      </ModalBody>
      <ModalFooter>
        <SecondaryButton onClick={() => onDecline()}>Cancel</SecondaryButton>
        <PrimaryButton onClick={() => onAccept()}>Ok</PrimaryButton>
      </ModalFooter>
    </Modal>
  );
};
