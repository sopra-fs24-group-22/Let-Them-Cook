import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

export function SecondaryIconButton({
  icon,
  style,
  onClick,
}: {
  icon: any;
  style: {};
  onClick: () => void;
}) {
  return (
    <FontAwesomeIcon
      icon={icon}
      style={Object.assign({}, { color: "#878787" }, style)}
      onClick={onClick}
    />
  );
}
