import { Col, Container, Row } from "react-bootstrap";
import Layout from "../components/Layout/MainLayout";
import { getUsersAPI } from "../api/app.api";
import { useEffect, useState } from "react";

const ChefsPage = () => {
  const [chefs, setChefs] = useState<any[]>([]);
  const fetchChefs = async () => {
    try {
      const res = await getUsersAPI();
      setChefs(res);
    } catch (e) {
      alert("Error while loading the chefs. Please try again.");
    }
  };

  useEffect(() => {
    fetchChefs();
  }, []);

  return (
    <Layout>
      <Container>
        <Row>
          {chefs.map((chef, index) => (
            <Col
              style={{
                borderLeft: index % 3 !== 0 ? "1px solid #ccc" : "",
                padding: "10px 20px",
              }}
            >
              <h3>{chef.firstname + " " + chef.lastname}</h3>
              <p style={{ fontSize: "80%" }}>@{chef.username}</p>
            </Col>
          ))}
        </Row>
      </Container>
    </Layout>
  );
};
export default ChefsPage;
