import { useState } from 'react';
import { useSelector } from "react-redux";
import { State } from "../features";
import Layout from "../components/Layout/MainLayout";
import { PrimaryButton, SecondaryButton } from "../components/ui/Button";
import { Label, Input, Select, Option } from "../components/ui/Input";
import Modal from 'react-bootstrap/Modal';
import { getAllRecipesAPI, postSessionAPI } from "../api/app.api";


const SessionsPage = () => {
  // Modal for creating a new session
  const [show, setShow] = useState(false);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

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

  // Function to create a new session
  const saveNewSession = () => {
    // TODO: API call
    // postSessionAPI({ recipe, start, duration, participants });
    console.log([recipe, start, duration, participants]); //! DEV ONLY
    handleClose();
  };

  // Return
  return (
    <>
      <Layout>
        <PrimaryButton onClick={handleShow}>
          Create new session
        </PrimaryButton>
      </Layout>
      
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
              <Option key={k} value={k}>{v}</Option>
            ))}
          </Select>

          <Label htmlFor="start">Start date</Label>
          <Input id="start" type="datetime-local"
            onChange={(e) => setStart(new Date(e.target.value))} />

          <Label htmlFor="duration">Duration</Label>
          <Input id="duration" type="number" placeholder="2.5 hours"
            onChange={(e) => setDuration(Number(e.target.value))} />

          <Label htmlFor="participants">Max. number of participants</Label>
          <Input id="participants" type="number" placeholder="10"
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
