import { useEffect, useState } from "react";
import Layout from "../components/Layout/MainLayout";
import { Header1 } from "../components/ui/Header";
import { getMyUser } from "../api/user.api";
import { Container, Row } from "react-bootstrap";
import { RecipeTile, SessionTile, Tile } from "../components/ui/Dashboard";
import { getAllRecipesAPI, getAllSessionsAPI } from "../api/app.api";

const AppPage = () => {
  // const ERROR_LOADING_DASHBOARD =
  //   "Error while loading the dashboard. Please reload the page.";
  useEffect(() => {
    fetchUserAndSessions();
    fetchNewestRecipes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Fetching User, Sessions
  const [user, setUser] = useState<any>(null);
  const [sessions, setSessions] = useState<any>([]);
  const fetchUserAndSessions = async () => {
    try {
      const user = await getMyUser();
      setUser(user);

      const session = await getAllSessionsAPI({
        hostId: user.id,
        limit: 6,
      });
      setSessions(session);
    } catch (e) {
      // alert(ERROR_LOADING_DASHBOARD);
    }
  };

  const [newestRecipes, setNewestRecipes] = useState<any>([]);
  const fetchNewestRecipes = async () => {
    try {
      const recipes = await getAllRecipesAPI({ limit: 6 });
      setNewestRecipes(recipes);
    } catch (e) {
      // alert(ERROR_LOADING_DASHBOARD);
    }
  };

  // --- RETURN ---
  return (
    <Layout>
      {/* Title */}
      <Header1>Let {user?.firstname ? user.firstname : "them"} Cook!</Header1>

      {/* My Sessions */}
      <Container style={{ margin: "20px 0 0 0", width: "calc(100% - 7.5px)" }}>
        <Row>
          <Tile title="My upcoming sessions" xs={12}>
            <Container>
              <Row>
                {sessions.map((s: any) => (
                  <SessionTile name={s.sessionName} id={s.id} date={s.date} />
                ))}
                {sessions.length === 0 && (
                  <p style={{ marginTop: "10px" }}>No sessions found.</p>
                )}
              </Row>
            </Container>
          </Tile>
        </Row>
      </Container>

      {/* Newest recipes */}
      <Container style={{ margin: "20px 0 0 0", width: "calc(100% - 7.5px)" }}>
        <Row>
          <Tile title="Newest recipes" xs={12}>
            <Container>
              <Row>
                {newestRecipes.map((r: any) => (
                  <RecipeTile
                    name={r.title}
                    id={r.id}
                    creatorName={r.creatorName}
                  />
                ))}
                {newestRecipes.length === 0 && (
                  <p style={{ marginTop: "10px" }}>No recipes found.</p>
                )}
              </Row>
            </Container>
          </Tile>
        </Row>
      </Container>
    </Layout>
  );
};
export default AppPage;
