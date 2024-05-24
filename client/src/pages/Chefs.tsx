import { Col, Container, Row } from "react-bootstrap";
import Layout from "../components/Layout/MainLayout";
import { getUsersAPI, postRateUserAPI } from "../api/app.api";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { StarRating } from "../components/ui/StarRating";
import styled from "styled-components";
import {
  Modal,
  ModalTitle,
  ModalBody,
  ModalFooter,
  ModalHeader,
} from "../components/ui/Modal";
import { SecondaryText } from "../components/ui/Header";
import { SecondaryButton } from "../components/ui/Button";

const ChefsPage = () => {
  const navigate = useNavigate();

  const [chefs, setChefs] = useState<any[]>([]);
  const fetchChefs = async () => {
    try {
      const res = await getUsersAPI();
      setChefs(res);
    } catch (e) {
      showErrorModal("Error while loading the chefs. Please try again.");
    }
  };

  useEffect(() => {
    fetchChefs();
  }, []);

  const rateChef = async (chefId: number, rating: number) => {
    await postRateUserAPI(chefId, rating).catch((error) => {
      if (error.code === "ERR_BAD_REQUEST")
        showErrorModal("You cannot rate yourself.");
      else showErrorModal("Error while rating the chef. Please try again.");
    });
  };

  // Error messages
  const [errorMessageModalShown, setErrorMessageModalShown] = useState(false);
  const [errorMessageModalText, setErrorMessageModalText] = useState("");

  const showErrorModal = (message: string) => {
    setErrorMessageModalText(message);
    setErrorMessageModalShown(true);
  };

  return (
    <Layout>
      <>
        <Container>
          <Row>
            {chefs.map((chef, index) => (
              <Col xs={4}>
                <Item>
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
                      callbackFunction={async (
                        chefId: number,
                        rating: number,
                      ) => {
                        await rateChef(chefId, rating).then(async () => {
                          await fetchChefs();
                        });
                      }}
                    />
                  </p>
                </Item>
              </Col>
            ))}
          </Row>
        </Container>

        {/* Modal for error messages */}
        <Modal
          show={errorMessageModalShown}
          onHide={() => setErrorMessageModalShown(false)}
        >
          <ModalHeader>
            <ModalTitle>Error</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <SecondaryText>{errorMessageModalText}</SecondaryText>
          </ModalBody>
          <ModalFooter>
            <SecondaryButton onClick={() => setErrorMessageModalShown(false)}>
              Ok
            </SecondaryButton>
          </ModalFooter>
        </Modal>
      </>
    </Layout>
  );
};
export default ChefsPage;

const Item = styled.div`
  background-color: white;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 15px;
`;
