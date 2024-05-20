import { Col, Container, Row } from "react-bootstrap";
import Layout from "../components/Layout/MainLayout";
import { getUsersAPI, postRateUserAPI } from "../api/app.api";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { StarRating } from "../components/ui/StarRating";

const ChefsPage = () => {
  const navigate = useNavigate();

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

  const rateChef = async (chefId: number, rating: number) => {
    await postRateUserAPI(chefId, rating).catch((error) => {
      if (error.code === "ERR_BAD_REQUEST") alert("You cannot rate yourself.");
      else alert("Error while rating the chef. Please try again.");
    });
  };

  return (
    <Layout>
      <Container>
        <Row>
          {chefs.map((chef, index) => (
            <Col
              xs={4}
              style={{
                borderLeft: index % 3 !== 0 ? "1px solid #ccc" : "",
                padding: "10px 20px",
              }}
            >
              <h3
                style={{ cursor: "pointer" }}
                onClick={() => navigate("/recipes/chef=" + chef.username)}
              >
                {chef.firstname + " " + chef.lastname}
              </h3>
              <p style={{ fontSize: "80%" }}>
                @{chef.username} |
                <StarRating
                  id={chef.id}
                  avgRating={chef.avgTotalRating}
                  nrRating={chef.nrRatings}
                  callbackFunction={async (chefId: number, rating: number) => {
                    await rateChef(chefId, rating).then(async () => {
                      await fetchChefs();
                    });
                  }}
                />
              </p>
            </Col>
          ))}
        </Row>
      </Container>
    </Layout>
  );
};
export default ChefsPage;
