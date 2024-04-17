import { useState, ChangeEvent } from 'react';
import { useSelector } from "react-redux";
import { State } from "../features";
import Layout from "../components/Layout/MainLayout";
import { PrimaryButton, SecondaryButton, ButtonGroup } from "../components/ui/Button";
import { Label, Input, Select, Option } from "../components/ui/Input";
import {Accordion, Col, Container, Modal, Row, Stack} from 'react-bootstrap';
import { getAllRecipesAPI, postSessionAPI, getAllSessionsAPI } from "../api/app.api";
import {
  faTrashCan,
  faCircleChevronDown,
  faCircleChevronUp
} from "@fortawesome/free-solid-svg-icons";
import { SecondaryIconButton } from '../components/ui/Icon';
import MainLayout from "../components/Layout/MainLayout";
import {useNavigate} from "react-router-dom";

//Defined interface for session
interface Session {
  recipe: string;
  Start: string;
  Duration: string;
  Participants: number;
  Chef: string;
}
const SessionsPage = () => {
  //Session Overview
  const [sessions, setSession] = useState<Session[]>(
      // TODO: API CALL
      // await getAllSessionsAPI();
      [
        {"recipe": "Kottu Roti", "Start": "01.05.2024, 18:20", "Duration": "2h", "Participants": 2, "Chef": "Chef Dave"},
        {"recipe": "Shawarma", "Start": "01.05.2024, 18:30", "Duration": "13h", "Participants": 14, "Chef": "Chef Ali"},
        {"recipe": "Shawarma", "Start": "01.05.2024, 18:30", "Duration": "13h", "Participants": 14, "Chef": "Chef Ali"},
        {"recipe": "Shawarma", "Start": "01.05.2024, 18:30", "Duration": "13h", "Participants": 14, "Chef": "Chef Ali"},
        {"recipe": "Shawarma", "Start": "01.05.2024, 18:30", "Duration": "13h", "Participants": 14, "Chef": "Chef Ali"}
      ]//! DEV ONLY
  )

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
  const handleShow = () => setShow(true);
  const navigate = useNavigate();

  // Get all recipes
  const [recipes, setRecipes] = useState<object>(
    // TODO: API call
    // await getAllRecipesAPI();
    {1: 'Chicken curry', 2: 'Pasta', 3: 'Pizza'} //! DEV ONLY
  );

  // Vars for creating a new session
  const [recipe, setRecipe] = useState<number>();
  const [start, setStart] = useState<Date>();
  const [duration, setDuration] = useState<number>();
  const [participants, setParticipants] = useState<number>();
  const [singleSteps, setSingleSteps] = useState<string[]>(['']);

  // Function to save a new session
  const saveNewSession = async () => {
    // TODO: API call
    const body = {
      recipe: recipe,
      start: start,
      duration: duration,
      participants: participants,
      singleSteps: singleSteps
    };
    // try {
    //   await postSessionAPI(body);
    //   handleClose();
    // } catch (error) {
    //   alert("Error while saving the session. Please try again.");
    // }
    console.log(body); //! DEV ONLY
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
    if (index == singleSteps.length-1) return;
    const values = [...singleSteps];
    [values[index], values[index+1]] = [values[index+1], values[index]];
    setSingleSteps(values);
  };
  const moveSingleStepUp = (index: number) => {
    if (index == 0) return;
    const values = [...singleSteps];
    [values[index], values[index-1]] = [values[index-1], values[index]];
    setSingleSteps(values);
  };

  // Return
  return (
    <>
      <MainLayout
        sidebarContent = {
          <PrimaryButton onClick={handleShow} style={{width: '100%'}}>
            Create new session
          </PrimaryButton>}>

        <ButtonGroup>
          <PrimaryButton onClick={() => navigate("/sessions")} style={{
            width: '50%',
            borderTopRightRadius: '0',
            borderBottomRightRadius: '0',
          }}>
            All sessions
          </PrimaryButton>
          <SecondaryButton onClick={() => navigate("/sessions")} style={{
            width: '50%',
            borderTopLeftRadius: '0',
            borderBottomLeftRadius: '0',
          }}>
            My sessions
          </SecondaryButton>
        </ButtonGroup>
        <Container fluid>
        <Row>
          <Accordion>
            {sessions.map((session, index) => (
                <Accordion.Item key={index} eventKey={String(index)} style={{ width: '100%', background: '#f0f0f0', marginTop: '5px'}}>
                  <Accordion.Header style={{ background: '#f0f0f0' }}>
                    <div style={{ fontSize: '20px' }}>{session.recipe}</div>
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
            {Object.entries(recipes).map(([k, v]) => (
              <Option key={k} value={k} selected={recipe == Number(k)}>
                {v}</Option>
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
                  cursor: (index == singleSteps.length-1) ? '' : 'pointer',
                  marginLeft: '5px',
                  color: (index == singleSteps.length-1) ? '#ccc' : '#878787'}}
                onClick={(index == singleSteps.length-1) ? () => {} : () => moveSingleStepDown(index)}
              />
              <SecondaryIconButton
                icon={faCircleChevronUp}
                style={{
                  cursor: (index == 0) ? '' : 'pointer',
                  marginLeft: '5px',
                  color: (index == 0) ? '#ccc' : '#878787'}}
                onClick={(index == 0) ? () => {} : () => moveSingleStepUp(index)}
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
