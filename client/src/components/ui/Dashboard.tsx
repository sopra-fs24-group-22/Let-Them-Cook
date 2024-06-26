import { ReactNode, useEffect, useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { Header2 } from "./Header";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";

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
    <BaseTile
      textLeft={name}
      textRight={dateString}
      id={id}
      baseUrl="sessions/detail/"
    />
  );
};

interface RecipeTileProps {
  name?: string;
  id?: number;
  creatorName?: string;
}

export const RecipeTile: React.FC<RecipeTileProps> = ({
  name,
  id,
  creatorName,
}) => (
  <BaseTile
    textLeft={name}
    textRight={"by " + creatorName}
    id={id}
    baseUrl="recipes/id="
  />
);

interface BaseTileProps {
  textLeft?: string;
  textRight?: string;
  id?: number;
  baseUrl?: string;
}

const BaseTile: React.FC<BaseTileProps> = ({
  textLeft,
  textRight,
  id,
  baseUrl,
}) => {
  const navigate = useNavigate();
  return (
    <BaseTileStyled onClick={() => navigate("/" + baseUrl + id)}>
      {textLeft}
      <span style={{ float: "right", fontSize: "80%", marginTop: "3px" }}>
        {textRight}
      </span>
    </BaseTileStyled>
  );
};

const BaseTileStyled = styled.div`
  margin: 5px;
  border-radius: 16px;
  flex: 0 0 auto;
  width: 100%;
  cursor: pointer;
  background: linear-gradient(90deg, #bebebe 0%, #a9a9a9 100%);
  color: white;
  padding: 12px 16px;
  border-radius: 16px;
  font-weight: 500;
  outline: 0;
  border: 0;
  cursor: pointer;
  transition: transform 0.2s;
  &:hover {
    background-color: #737373;
    transform: scale(1.05);
  }
  &:disabled {
    cursor: default;
    opacity: 0.7;
  }
`;
