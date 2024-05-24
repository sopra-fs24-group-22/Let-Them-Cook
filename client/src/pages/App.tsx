import { useEffect, useState } from "react";
import Layout from "../components/Layout/MainLayout";
import { Header1 } from "../components/ui/Header";
import { RecipeTile, SessionTile } from "../components/ui/Dashboard";
import {
  getOpenSessionsAPI,
  getRecipesAPI,
  getSessionMeAPI,
} from "../api/app.api";
import { useSelector } from "react-redux";
import { State } from "../features";
import styled from "styled-components";
import CookImage from "../assets/img/cook.png";

const AppPage = () => {
  // const ERROR_LOADING_DASHBOARD =
  //   "Error while loading the dashboard. Please reload the page.";

  useEffect(() => {
    fetchUserAndSessions();
    fetchNewestRecipes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Fetching  Sessions
  const { user } = useSelector((state: State) => state.app);
  const [sessions, setSessions] = useState<any>([]);
  const [openSessions, setOpenSessions] = useState<any>([]);
  const fetchUserAndSessions = async () => {
    try {
      // Sessions
      const sessions = await getSessionMeAPI();
      setSessions(sessions);

      // Open Sessions (only the first 6 that arent shown in upcoming sessions already)
      const openSessions = await getOpenSessionsAPI();
      setOpenSessions(
        openSessions
          .filter((s: any) => !sessions.map((se: any) => se.id).includes(s.id))
          .slice(0, 6),
      );
    } catch (e) {
      // alert(ERROR_LOADING_DASHBOARD);
    }
  };

  const [newestRecipes, setNewestRecipes] = useState<any>([]);
  const fetchNewestRecipes = async () => {
    try {
      const recipes = await getRecipesAPI({ limit: 6 });
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
      <CardList>
        {/* My Sessions */}
        <CardWrapper>
          <Card style={{ margin: "20px 0 0 0", width: "calc(100% - 7.5px)" }}>
            <Subtitle>My upcoming sessions</Subtitle>
            <List>
              {sessions.map((s: any) => (
                <SessionTile name={s.sessionName} id={s.id} date={s.date} />
              ))}
              {sessions.length === 0 && (
                <NotFoundText>No sessions found.</NotFoundText>
              )}
            </List>
          </Card>
        </CardWrapper>

        {/* Open sessions */}
        <CardWrapper>
          {" "}
          <Card style={{ margin: "20px 0 0 0", width: "calc(100% - 7.5px)" }}>
            <Subtitle>Sessions open for registration</Subtitle>
            <List>
              {openSessions.map((s: any) => (
                <SessionTile name={s.sessionName} id={s.id} date={s.date} />
              ))}
              {openSessions.length === 0 && (
                <NotFoundText>No open sessions found.</NotFoundText>
              )}
            </List>
          </Card>
        </CardWrapper>

        {/* Newest recipes */}
        <CardWrapper>
          <Card style={{ margin: "20px 0 0 0", width: "calc(100% - 7.5px)" }}>
            <Subtitle>Newest recipes</Subtitle>
            <List>
              {newestRecipes.map((r: any) => (
                <RecipeTile
                  name={r.title}
                  id={r.id}
                  creatorName={r.creatorName}
                />
              ))}
              {newestRecipes.length === 0 && (
                <NotFoundText>No recipes found.</NotFoundText>
              )}
            </List>
          </Card>
        </CardWrapper>
      </CardList>
      <Cook src={CookImage} />
    </Layout>
  );
};
export default AppPage;

const Subtitle = styled.h2`
  margin-bottom: 20px;
  font-size: 1.8rem;
  color: #6d5f5f;
`;

const Card = styled.div`
  padding: 25px;
  background-color: white;
  border-radius: 16px;
  width: 100%;
  height: 100%;
`;

const List = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
`;

export const NotFoundText = styled.div`
  color: #878787;
`;

const CardList = styled.div`
  display: flex;
  flex-direction: row;
  margin: -10px;
`;

const CardWrapper = styled.div`
  padding: 10px;
  flex: 1;
  height: 100%;
`;

const Cook = styled.img`
  width: 600px;
  display: block;
  margin: 0 auto;
`;
