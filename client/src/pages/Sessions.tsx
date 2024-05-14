import { useState, useEffect } from "react";
import {
  PrimaryButton,
  SecondaryButton,
  ButtonGroup,
  JoinButton,
  HLine,
} from "../components/ui/Button";
import { Label, Input, Select, Option } from "../components/ui/Input";
import { Accordion, Col, Container, Modal, Row } from "react-bootstrap";
import MainLayout from "../components/Layout/MainLayout";
import {
  getRecipesAPI,
  getSessionsAPI,
  getCookbookAPI,
  postSessionAPI,
  postSessionRequestAPI,
} from "../api/app.api";
import { getMyUser, getUsers } from "../api/user.api";
import { useNavigate } from "react-router-dom";
import { Header2, Header3 } from "../components/ui/Header";
import { formatDateTime } from "../helpers/formatDateTime";
import { ENV } from "../env";

const SessionsPage = () => {
  const navigate = useNavigate();
  const fetchUser = async () => {
    try {
      const user = await getMyUser();
      await fetchAllRecipes(user.id);
    } catch (e) {
      alert("Error while fetching the user. Please reload the page.");
    }
  };

  //Session Overview
  const fetchSessions = async (view: "ALL" | "MY") => {
    try {
      // build object for filtering
      var filter = {};
      filter = { ...filter, limit: 10000 };
      if (dateFilter)
        filter = {
          ...filter,
          date: dateFilter.toISOString().substring(0, 10) + "T00:00:00",
        };
      if (recipeFilter !== "") filter = { ...filter, recipeName: recipeFilter };
      if (hostFilter !== "") filter = { ...filter, hostName: hostFilter };
      if (sessionNameFilter !== "")
        filter = { ...filter, sessionName: sessionNameFilter };

      const res =
        view === "ALL"
          ? await getSessionsAPI(filter)
          : await getSessionsAPI(filter); //! DEV ONLY
      for (const session of res) {
        const id = session.hostId;
        const host = await getUsers(id);
        res.hostName = host.username;
      }
      setSessions(res);
    } catch (error) {
      alert("Error while loading the sessions. Please try again.");
    }
  };

  useEffect(() => {
    fetchUser();
    fetchSessions("ALL");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Modal for creating a new session
  const [show, setShow] = useState(false);
  const handleClose = () => {
    setShow(false);
    setRecipe(undefined);
    setSessionsName(undefined);
    setStart(undefined);
    setDuration(undefined);
    setParticipants(undefined);
  };
  const handleShow = async () => {
    setShow(true);
  };

  // Get all recipes for the New-Session-PopUp/Session-Overview
  const [cookbookRecipes, setCookbookRecipes] = useState<any[]>([]);
  const [allRecipes, setAllRecipes] = useState<any[]>([]);
  const recipeTitles: { [key: number]: string } = {};

  const fetchAllRecipes = async (userId: number) => {
    try {
      // fetching cookbook
      const res1 = await getCookbookAPI(userId);
      setCookbookRecipes(res1);
      // fetching all recipes
      const res2 = await getRecipesAPI({ limit: 10000 });
      setAllRecipes(res2);
      res2.forEach((recipe: { id: number; title: string }) => {
        recipeTitles[recipe.id] = recipe.title;
      });
    } catch (e) {
      alert("Error while fetching all recipes. Please reload the page.");
    }
  };

  // Filter sessions
  const [sessionNameFilter, setSessionNameFilter] = useState<string>("");
  const [hostFilter, setHostFilter] = useState<string>("");
  const [recipeFilter, setRecipeFilter] = useState<string>("");
  const [dateFilter, setDateFilter] = useState<Date | undefined>(undefined);

  useEffect(() => {
    fetchSessions(pageView);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sessionNameFilter, recipeFilter, hostFilter, dateFilter]);

  const deleteFilter = async () => {
    setSessionNameFilter("");
    setDateFilter(undefined);
    setRecipeFilter("");
    setHostFilter("");
  };

  // Vars for creating a new session
  const [recipe, setRecipe] = useState<number>();
  const [start, setStart] = useState<Date>();
  const [duration, setDuration] = useState<number>();
  const [participants, setParticipants] = useState<number>();
  const [sessionName, setSessionsName] = useState<string>();

  // Function to save a new session
  const saveNewSession = async () => {
    const body = {
      recipe: recipe,
      sessionName: sessionName,
      date: start,
      duration: duration,
      maxParticipantCount: participants,
    };
    try {
      await postSessionAPI(body);
      handleClose();
      await fetchSessions("ALL");
    } catch (error) {
      alert("Error while saving the session. Please try again.");
    }
  };
  const requestParticipation = async (sessionId: number) => {
    try {
      await postSessionRequestAPI(sessionId);
    } catch (error) {
      alert("You have already sent a session request for this session.");
    }
  };

  const [pageView, setPageView] = useState<"ALL" | "MY">("ALL");
  const [sessions, setSessions] = useState<any[]>([]);

  const changeView = async (view: "ALL" | "MY") => {
    setPageView(view);
    await fetchSessions(view);
  };

  const buttonTopBar =
    pageView === "ALL" ? (
      <>
        <PrimaryButton
          style={{
            width: "50%",
            borderTopRightRadius: "0",
            borderBottomRightRadius: "0",
          }}
        >
          All sessions
        </PrimaryButton>
        <SecondaryButton
          onClick={() => changeView("MY")}
          style={{
            width: "50%",
            borderTopLeftRadius: "0",
            borderBottomLeftRadius: "0",
          }}
        >
          My sessions
        </SecondaryButton>
      </>
    ) : (
      <>
        <SecondaryButton
          onClick={() => changeView("ALL")}
          style={{
            width: "50%",
            borderTopRightRadius: "0",
            borderBottomRightRadius: "0",
          }}
        >
          All sessions
        </SecondaryButton>
        <PrimaryButton
          style={{
            width: "50%",
            borderTopLeftRadius: "0",
            borderBottomLeftRadius: "0",
          }}
        >
          My sessions
        </PrimaryButton>
      </>
    );
  // Return
  return (
    <>
      <MainLayout
        sidebarContent={
          <>
            <PrimaryButton onClick={handleShow} style={{ width: "100%" }}>
              Create new session
            </PrimaryButton>

            <HLine />

            <Header3 style={{ marginBottom: "20px" }}>Filter:</Header3>

            <Label htmlFor="sessionNameFilter" style={{ marginLeft: "0" }}>
              Session name
            </Label>
            <Input
              id="sessionNameFilter"
              type="text"
              maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
              style={{ width: "100%", marginTop: "0", marginLeft: "0" }}
              value={sessionNameFilter}
              onChange={(e) => setSessionNameFilter(e.target.value)}
            />

            <Label htmlFor="recipeFilter" style={{ marginLeft: "0" }}>
              Recipe
            </Label>
            <Input
              id="recipeFilter"
              type="text"
              maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
              style={{ width: "100%", marginTop: "0", marginLeft: "0" }}
              value={recipeFilter}
              onChange={(e) => setRecipeFilter(e.target.value)}
            />

            <Label htmlFor="hostFilter" style={{ marginLeft: "0" }}>
              Host
            </Label>
            <Input
              id="hostFilter"
              type="text"
              maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
              style={{ width: "100%", marginTop: "0", marginLeft: "0" }}
              value={hostFilter}
              onChange={(e) => setHostFilter(e.target.value)}
            />

            <Label htmlFor="dateFilter" style={{ marginLeft: "0" }}>
              After date
            </Label>
            <Input
              id="dateFilter"
              type="date"
              style={{ width: "100%", marginTop: "0", marginLeft: "0" }}
              value={
                dateFilter ? dateFilter.toISOString().substring(0, 10) : ""
              }
              onChange={(e) => setDateFilter(new Date(e.target.value))}
            />

            <SecondaryButton onClick={deleteFilter} style={{ width: "100%" }}>
              Delete all filter
            </SecondaryButton>
          </>
        }
      >
        <ButtonGroup style={{ marginBottom: "20px" }}>
          {buttonTopBar}
        </ButtonGroup>
        <Container fluid>
          <Row>
            <Accordion style={{ padding: "0" }}>
              {sessions.map((session, index) => (
                <Accordion.Item
                  key={index}
                  eventKey={String(index)}
                  style={{
                    width: "100%",
                    background: "#f0f0f0",
                    marginTop: "5px",
                  }}
                >
                  <Accordion.Header
                    style={{ display: "flex", background: "#f0f0f0" }}
                  >
                    <Container>
                      <Row>
                        <Col xs={11}>
                          <Header2>{session.sessionName}</Header2>
                        </Col>
                        <Col xs={1}>
                          <JoinButton
                            onClick={() => navigate("/sessions/" + session.id)}
                          >
                            Join
                          </JoinButton>
                          <JoinButton
                            onClick={() => requestParticipation(session.id)}
                          >
                            Request participation
                          </JoinButton>
                        </Col>
                      </Row>
                    </Container>
                  </Accordion.Header>
                  <Accordion.Body style={{ background: "#f0f0f0" }}>
                    <div>Date & start time: {formatDateTime(session.date)}</div>
                    <div>Host: {session.hostName}</div>
                    <div>Max Participants: {session.maxParticipantCount}</div>
                    {allRecipes.map((recipe) => {
                      if (recipe.id === session.recipe) {
                        return (
                          <div key={recipe.id}>Recipe: {recipe.title}</div>
                        );
                      }
                      return null;
                    })}
                  </Accordion.Body>
                </Accordion.Item>
              ))}
            </Accordion>
          </Row>
        </Container>
      </MainLayout>

      {/* Modal for creating a new session */}
      <Modal show={show} onHide={handleClose}>
        <Modal.Header>
          <Modal.Title>Create new session</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Label htmlFor="recipe">Recipe</Label>
          <Select
            id="recipe"
            onChange={(e) => setRecipe(Number(e.target.value))}
          >
            <Option disabled selected>
              Select a recipe
            </Option>
            <Option disabled>{"-".repeat(40)}</Option>
            {cookbookRecipes.map((e) => (
              <Option
                key={e.id}
                value={e.id}
                selected={recipe === Number(e.id)}
                disabled={e.privacyStatus === 0}
              >
                {e.title}
              </Option>
            ))}
          </Select>

          <Label htmlFor="sessionname">Sessionname</Label>
          <Input
            id="sessionname"
            type="text"
            maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
            placeholder="Session name"
            value={sessionName}
            onChange={(e) => setSessionsName(String(e.target.value))}
          />

          <Label htmlFor="start">Start date</Label>
          <Input
            id="start"
            type="datetime-local"
            value={start?.toISOString().slice(0, 16) || ""}
            onChange={(e) => setStart(new Date(e.target.value + ":00.000Z"))}
          />

          <Label htmlFor="duration">Duration (minutes)</Label>
          <Input
            id="duration"
            type="number"
            min={ENV.MIN_NUMBER_MINUTES_LENGTH}
            max={ENV.MAX_NUMBER_MINUTES_LENGTH}
            placeholder="90"
            value={duration}
            onChange={(e) => setDuration(Number(e.target.value))}
          />

          <Label htmlFor="participants">Max. number of participants</Label>
          <Input
            id="participants"
            type="number"
            min={1}
            max={30}
            placeholder="10"
            value={participants}
            onChange={(e) => setParticipants(Number(e.target.value))}
          />
        </Modal.Body>
        <Modal.Footer>
          <SecondaryButton onClick={handleClose}>Cancel</SecondaryButton>
          <PrimaryButton
            onClick={saveNewSession}
            disabled={
              !(
                recipe &&
                sessionName &&
                start &&
                duration &&
                ENV.MIN_NUMBER_MINUTES_LENGTH <= duration &&
                duration <= ENV.MAX_NUMBER_MINUTES_LENGTH &&
                participants &&
                1 <= participants &&
                participants <= 30
              )
            }
          >
            Save
          </PrimaryButton>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default SessionsPage;
