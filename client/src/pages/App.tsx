import { useEffect, useState } from "react";
import Layout from "../components/Layout/MainLayout";
import { Header1 } from "../components/ui/Header";
import { getMyUser } from "../api/user.api";
import { Container, Row } from "react-bootstrap";
import { SessionTile, Tile } from "../components/ui/Dashboard";
import { getAllSessionsAPI } from "../api/app.api";

const AppPage = () => {
  const ERROR_LOADING_DASHBOARD =
    "Error while loading the dashboard. Please reload the page.";
  useEffect(() => {
    fetchUserAndSessions();
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
    </Layout>
  );
};
export default AppPage;
