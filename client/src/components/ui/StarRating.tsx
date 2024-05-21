import { faStar } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useState } from "react";

interface StarRatingProps {
  id: number;
  avgRating: number;
  nrRating: number;
  callbackFunction: Function;
}

export const StarRating: React.FC<StarRatingProps> = ({
  id,
  avgRating,
  nrRating,
  callbackFunction,
}) => {
  const [hoverOverRatingStar, setHoverOverRatingStar] = useState<number>(0);
  return (
    <>
      {[1, 2, 3, 4, 5].map((key) => (
        <FontAwesomeIcon
          icon={faStar}
          style={{
            marginLeft: key === 1 ? "5px" : "",
            marginRight: key === 5 ? "5px" : "",
            color:
              hoverOverRatingStar > 0
                ? hoverOverRatingStar >= key
                  ? "#f00"
                  : "#878787"
                : avgRating >= key
                  ? "#ffa500"
                  : "#878787",
            cursor: "pointer",
          }}
          onMouseEnter={() => setHoverOverRatingStar(key)}
          onMouseLeave={() => setHoverOverRatingStar(0)}
          onClick={() => callbackFunction(id, key)}
        />
      ))}
      ({nrRating ?? 0})
    </>
  );
};
