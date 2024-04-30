import { ReactNode, useEffect, useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { Header2 } from "./Header";
import { useNavigate } from "react-router-dom";

interface TileProps {
  children?: ReactNode;
  title?: string;
  xs?: any;
}

export const Tile: React.FC<TileProps> = ({ children, title, xs }) => {
  return (
    <Col
      style={{
        padding: "20px",
        borderRadius: "10px",
        border: "1px solid #878787",
        margin: "5px",
      }}
      xs={xs}
    >
      <Container>
        {title && (
          <Row>
            <Header2 style={{ margin: "0 0 10px 0", padding: "0" }}>
              {title}
            </Header2>
          </Row>
        )}
        <Row>{children}</Row>
      </Container>
    </Col>
  );
};

interface SessionTileProps {
  name?: string;
  id?: number;
  date?: string;
}

export const SessionTile: React.FC<SessionTileProps> = ({ name, id, date }) => {
  const navigate = useNavigate();
  const [isHovered, setIsHovered] = useState(false);
  const [dateString, setDateString] = useState("" as string);

  const generateDateString = () => {
    if (date) {
      const dateObj = new Date(date);

      // If date is in past: don't show (we get all sessions from Backend)
      if (dateObj.getTime() < new Date().getTime() + 24 * 60 * 60 * 1000) {
        return;
      }

      // If today: show time
      if (dateObj.toDateString() === new Date().toDateString()) {
        setDateString(dateObj.toLocaleTimeString().slice(0, 5));
        // Else: show date
      } else {
        setDateString(dateObj.toLocaleDateString());
      }
    } else {
      setDateString("");
    }
  };

  useEffect(() => {
    generateDateString();
  });

  return (
    <Col
      style={{
        margin: "5px",
        padding: "10px",
        borderRadius: "10px",
        border: "1px solid #878787",
        flex: "0 0 auto",
        width: "32%",
        cursor: "pointer",
        backgroundColor: isHovered ? "#e5dcd0" : "",
      }}
      onClick={() => navigate("/sessions/" + id)}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {name}
      <span style={{ float: "right" }}>{dateString}</span>
    </Col>
  );
};
