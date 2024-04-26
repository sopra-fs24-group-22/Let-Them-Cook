import {useState, useEffect} from 'react';
import {PrimaryButton, SecondaryButton, ButtonGroup, JoinButton} from "../components/ui/Button";
import { Label, Input, Select, Option } from "../components/ui/Input";
import {Accordion, Container, Modal, Row} from 'react-bootstrap';
import MainLayout from "../components/Layout/MainLayout";
import {getAllRecipesAPI, getAllSessionsAPI, getCookbookAPI, postSessionAPI} from "../api/app.api";
import { getMyUser } from '../api/user.api';
import { useNavigate } from 'react-router-dom';


const SessionsPage = () => {
  const navigate = useNavigate();
  const fetchUser = async () => {
    try {
      const user = await getMyUser();
      await fetchAllRecipes(user.id);
    } catch(e) {
      alert("Error while fetching the user. Please reload the page.");
    }
  }

  //Session Overview
  const fetchSessions = async (view: "ALL" | "MY") => {
    try {
      // TODO: API CALL
      // await getAllSessionsAPI();
      const res = (view === "ALL") ?
          await getAllSessionsAPI() : await getAllSessionsAPI();//! DEV ONLY

      setSessions(res);
    } catch (error) {
      alert("Error while loading the sessions. Please try again.");
    }
  }

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
  }
  const handleShow = async () => {
    setShow(true);
  }

  // Get all recipes for the New-Session-PopUp/Session-Overview
  const [cookbookRecipes, setCookbookRecipes] = useState<any[]>([]);
  const [allRecipes, setAllRecipes] = useState<any[]>([]);

  const fetchAllRecipes = async (userId: number) => {
    try {
      // fetching cookbook
      const res1 = await getCookbookAPI(userId);
      setCookbookRecipes(res1);
      // fetching all recipes
      const res2 = await getAllRecipesAPI();
      setAllRecipes(res2);
    } catch(e) {
      alert("Error while fetching all recipes. Please reload the page.");
    }
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
      } catch(error) {
        alert("Error while saving the session. Please try again.");
      }
  };

  const [pageView, setPageView] = useState<"ALL" | "MY">('ALL');
  const [sessions, setSessions] = useState<any[]>([]);

  const changeView = async (view: "ALL" | "MY") => {
    setPageView(view);
    await fetchSessions(view);
  }

  const buttonTopBar = (pageView === "ALL") ? (
      <>
        <PrimaryButton style={{
          width: '50%',
          borderTopRightRadius: '0',
          borderBottomRightRadius: '0',
        }}>
          All sessions
        </PrimaryButton>
        <SecondaryButton onClick={() => changeView("MY")} style={{
          width: '50%',
          borderTopLeftRadius: '0',
          borderBottomLeftRadius: '0',
        }}>
          My sessions
        </SecondaryButton>
      </>
  ) : (
      <>
        <SecondaryButton onClick={() => changeView("ALL")} style={{
          width: '50%',
          borderTopRightRadius: '0',
          borderBottomRightRadius: '0',
        }}>
          All sessions
        </SecondaryButton>
        <PrimaryButton style={{
          width: '50%',
          borderTopLeftRadius: '0',
          borderBottomLeftRadius: '0',
        }}>
          My sessions
        </PrimaryButton>
      </>
  );
  // Return
  return (
    <>
      <MainLayout
        sidebarContent = {
          <PrimaryButton onClick={handleShow} style={{width: '100%'}}>
            Create new session
          </PrimaryButton>}>
        <ButtonGroup style={{marginBottom: '20px'}}>
          { buttonTopBar }
        </ButtonGroup>
        <Container fluid>
        <Row>
          <Accordion>
            {sessions.map((session, index) => (
                <Accordion.Item key={index} eventKey={String(index)} style={{ width: '100%', background: '#f0f0f0', marginTop: '5px'}}>
                  <Accordion.Header style={{ display: 'flex', background: '#f0f0f0' }}>
                    <div style={{ fontSize: '20px' }}>{session.sessionName}</div>
                    {/* Move the Join button to the right */}
                    <div style={{ marginLeft: 'auto' }}>
                      <JoinButton onClick={() => navigate("/sessions/" + session.id)}>Join</JoinButton>
                    </div>
                  </Accordion.Header>
                  <Accordion.Body style={{ background: '#f0f0f0' }}>
                    <div>Date & start time: {session.date}</div>
                    <div>Host: {session.host}</div>
                    <div>Max Participants: {session.maxParticipantCount}</div>
                    {allRecipes.map(recipe => {
                      if (recipe.id === session.recipe) {
                        return <div key={recipe.id}>Recipe: {recipe.title}</div>;
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
          <Select id="recipe"
            onChange={(e) => setRecipe(Number(e.target.value))}>
            <Option disabled selected>Select a recipe</Option>
            <Option disabled>{"-".repeat(40)}</Option>
            {cookbookRecipes.map((e) => (
              <Option key={e.id} value={e.id} selected={recipe === Number(e.id)}>{e.title}</Option>
            ))}
          </Select>

          <Label htmlFor="sessionname">Sessionname</Label>
          <Input id="sessionname" type="text" placeholder="Session name" value={sessionName}
            onChange={(e) => setSessionsName(String(e.target.value))}/>

          <Label htmlFor="start">Start date</Label>
          <Input id="start" type="datetime-local" value={start?.toISOString().slice(0, 16) || ''}
            onChange={(e) => setStart(new Date(e.target.value))} />

          <Label htmlFor="duration">Duration</Label>
          <Input id="duration" type="number" placeholder="2.5 hours" value={duration}
            onChange={(e) => setDuration(Number(e.target.value))} />

          <Label htmlFor="participants">Max. number of participants</Label>
          <Input id="participants" type="number" placeholder="10" value={participants}
            onChange={(e) => setParticipants(Number(e.target.value))} />

        </Modal.Body>
        <Modal.Footer>
          <SecondaryButton onClick={handleClose}>
            Cancel
          </SecondaryButton>
          <PrimaryButton onClick={saveNewSession} disabled={!(recipe && start && duration && participants)}>
            Save
          </PrimaryButton>
        </Modal.Footer>
      </Modal>
    </>
  );
};


export default SessionsPage;