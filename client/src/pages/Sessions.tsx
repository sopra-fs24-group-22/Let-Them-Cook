import {useState, ChangeEvent, useEffect} from 'react';
import {PrimaryButton, SecondaryButton, ButtonGroup, JoinButton} from "../components/ui/Button";
import { Label, Input, Select, Option } from "../components/ui/Input";
import {Accordion, Container, Modal, Row} from 'react-bootstrap';
import {
  faTrashCan,
  faCircleChevronDown,
  faCircleChevronUp
} from "@fortawesome/free-solid-svg-icons";
import { SecondaryIconButton } from '../components/ui/Icon';
import MainLayout from "../components/Layout/MainLayout";
import {getAllRecipesAPI, getAllSessionsAPI, postSessionAPI} from "../api/app.api";
import { getMyUser } from '../api/user.api';
import { useNavigate } from 'react-router-dom';


const SessionsPage = () => {
  const navigate = useNavigate();
  const [userName, setUserName] = useState<String>("");
  const fetchUser = async () => {
    try {
      const user = await getMyUser();
      setUserName(user.username);
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
          await getAllSessionsAPI() : //await getAllSessionsAPI({ host: });//! DEV ONLY
          [{"recipe": "Kottu Roti", "Start": "01.05.2024, 18:20", "Duration": "2h", "Participants": 2, "Chef": "Chef Dave"},
          {"recipe": "Shawarma", "Start": "01.05.2024, 18:30", "Duration": "13h", "Participants": 14, "Chef": "Chef Ali"}]
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
    setStart(undefined);
    setDuration(undefined);
    setParticipants(undefined);
    setSingleSteps(['']);
  }
  const handleShow = async () => {
    await fetchAllRecipes();
    setShow(true);
  }

  // Get all recipes for the New-Session-PopUp
  const [ownRecipes, setOwnRecipes] = useState<any[]>([]);
  const fetchAllRecipes = async () => {
    try {
      const res = await getAllRecipesAPI(null, null, {"creatorName": userName});
      setOwnRecipes(res);
    } catch(e) {
      alert("Error while fetching all recipes. Please reload the page.");
    }
  };

  // Vars for creating a new session
  const [recipe, setRecipe] = useState<number>();
  const [start, setStart] = useState<Date>();
  const [duration, setDuration] = useState<number>();
  const [participants, setParticipants] = useState<number>();
  const [singleSteps, setSingleSteps] = useState<string[]>(['']);

  // Function to save a new session
  const saveNewSession = async () => {
    const body = {
      recipe: recipe,
      start: start,
      duration: duration,
      participants: participants,
      singleSteps: singleSteps
    };
      try {
        await postSessionAPI(body);
        handleClose();
        await fetchSessions("ALL");
      } catch(error) {
        alert("Error while saving the session. Please try again.");
      }
  };

  // Functions for single steps
  const addSingleStep = () => setSingleSteps([...singleSteps, '']);
  const handleSingleStepsInputChange = (index: number, event: ChangeEvent<HTMLInputElement>) => {
    const values = [...singleSteps];
    values[index] = event.target.value;
    setSingleSteps(values);
  };
  const removeSingleStep = (index: number) => {
    const values = [...singleSteps];
    values.splice(index, 1);
    setSingleSteps(values);
  };
  const moveSingleStepDown = (index: number) => {
    if (index === singleSteps.length - 1) return;
    const values = [...singleSteps];
    [values[index], values[index + 1]] = [values[index + 1], values[index]];
    setSingleSteps(values);
  };
  const moveSingleStepUp = (index: number) => {
    if (index === 0) return;
    const values = [...singleSteps];
    [values[index], values[index - 1]] = [values[index - 1], values[index]];
    setSingleSteps(values);
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
                  <Accordion.Header style={{ display: 'flex', justifyContent: 'space-between', background: '#f0f0f0' }}>
                  <div style={{ fontSize: '20px' }}>{session.recipe}</div>
                  <div style={{ marginLeft: "90%" }}>
                    <JoinButton onClick={() => navigate("/sessions/" + session.id)}>Join</JoinButton>
                  </div>
                </Accordion.Header>
                  <Accordion.Body style={{ background: '#f0f0f0' }}>
                    <div>{session.Start}</div>
                    <div>{session.Duration}</div>
                    <div>{session.Participants}</div>
                    <div>{session.Chef}</div>
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
            {ownRecipes.map((e) => (
              <Option key={e.id} value={e.id} selected={recipe === Number(e.id)}>{e.title}</Option>
            ))}
          </Select>

          <Label htmlFor="start">Start date</Label>
          <Input id="start" type="datetime-local" value={start?.toISOString().slice(0, 16) || ''}
            onChange={(e) => setStart(new Date(e.target.value))} />

          <Label htmlFor="duration">Duration</Label>
          <Input id="duration" type="number" placeholder="2.5 hours" value={duration}
            onChange={(e) => setDuration(Number(e.target.value))} />

          <Label htmlFor="participants">Max. number of participants</Label>
          <Input id="participants" type="number" placeholder="10" value={participants}
            onChange={(e) => setParticipants(Number(e.target.value))} />

          <Label htmlFor="singleSteps">Single steps</Label>
          {singleSteps.map((input, index) => (
            <div key={index}>
              <Input
                type="text"
                value={input}
                onChange={(event) => handleSingleStepsInputChange(index, event)}
                style = {{width: '80%', marginBottom: '0'}}
                placeholder="Chop the carrots"
              />
              <SecondaryIconButton
                icon={faCircleChevronDown}
                style={{
                  cursor: (index === singleSteps.length-1) ? '' : 'pointer',
                  marginLeft: '5px',
                  color: (index === singleSteps.length-1) ? '#ccc' : '#878787'}}
                onClick={(index === singleSteps.length-1) ? () => {} : () => moveSingleStepDown(index)}
              />
              <SecondaryIconButton
                icon={faCircleChevronUp}
                style={{
                  cursor: (index === 0) ? '' : 'pointer',
                  marginLeft: '5px',
                  color: (index === 0) ? '#ccc' : '#878787'}}
                onClick={(index === 0) ? () => {} : () => moveSingleStepUp(index)}
              />
              <SecondaryIconButton
                icon={faTrashCan}
                style={{cursor: 'pointer', marginLeft: '5px'}}
                onClick={() => removeSingleStep(index)}
              />
            </div>
          ))}
          <SecondaryButton onClick={addSingleStep} style={{margin: '10px 5px'}}>
            Add step
          </SecondaryButton>
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