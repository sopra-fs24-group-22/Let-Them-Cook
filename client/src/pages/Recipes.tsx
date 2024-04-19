import { useState, ChangeEvent } from 'react';
import { PrimaryButton, SecondaryButton } from "../components/ui/Button";
import { Label, Input } from "../components/ui/Input";
import Modal from 'react-bootstrap/Modal';
import { postRecipeAPI } from "../api/app.api";
import MainLayout from '../components/Layout/MainLayout';
import {
  faTrashCan,
  faCircleChevronDown,
  faCircleChevronUp
} from "@fortawesome/free-solid-svg-icons";
import { SecondaryIconButton } from '../components/ui/Icon';

const RecipesPage = () => {
  // Vars for creating a new recipe
  const [show, setShow] = useState(false);
  const [dishName, setDishName] = useState<string>();
  const [cookingTime, setCookingTime] = useState<number>();
  const [ingredients, setIngredients] = useState<string[]>(['']);
  const [singleSteps, setSingleSteps] = useState<string[]>(['']);
  const handleClose = () => {
    setShow(false);
    setDishName(undefined);
    setCookingTime(undefined);
    setIngredients(['']);
    setSingleSteps(['']);
  }
  const handleShow = () => setShow(true);

  // Function to save a new session
  const saveNewRecipe = async () => {
    const body = {
      "title": dishName,
      "cookingTimeMin": cookingTime,
      "ingredients": ingredients,
      "checklist": singleSteps
    };
    try {
      await postRecipeAPI(body);
      handleClose();
    } catch (error) {
      alert("Error while saving the recipe. Please try again.");
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
    if (index === singleSteps.length-1) return;
    const values = [...singleSteps];
    [values[index], values[index+1]] = [values[index+1], values[index]];
    setSingleSteps(values);
  };
  const moveSingleStepUp = (index: number) => {
    if (index === 0) return;
    const values = [...singleSteps];
    [values[index], values[index-1]] = [values[index-1], values[index]];
    setSingleSteps(values);
  };

  // Functions for ingredients
  const addIngredients = () => setIngredients([...ingredients, '']);
  const handleIngredientsInputChange = (index: number, event: ChangeEvent<HTMLInputElement>) => {
    const values = [...ingredients];
    values[index] = event.target.value;
    setIngredients(values);
  };
  const removeIngredients = (index: number) => {
    const values = [...ingredients];
    values.splice(index, 1);
    setIngredients(values);
  };
  const moveIngredientsDown = (index: number) => {
    if (index === ingredients.length-1) return;
    const values = [...ingredients];
    [values[index], values[index+1]] = [values[index+1], values[index]];
    setIngredients(values);
  };
  const moveIngredientsUp = (index: number) => {
    if (index === 0) return;
    const values = [...ingredients];
    [values[index], values[index-1]] = [values[index-1], values[index]];
    setIngredients(values);
  };
  
  return (<>
    <MainLayout
      sidebarContent = {
        <PrimaryButton onClick={handleShow} style={{width: '100%'}}>
          Create new recipe
        </PrimaryButton>}>
      Recipes
    </MainLayout>

    {/* Modal for creating a new recipe */}
    <Modal show={show} onHide={handleClose}>
      <Modal.Header>
        <Modal.Title>Create new recipe</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Label htmlFor="dishName">Dish name</Label>
        <Input id="dishName" type="text" value={dishName} style={{width: '80%'}}
          onChange={(e) => setDishName(e.target.value)} />

        <Label htmlFor="cookingTime">Cooking time (minutes)</Label>
        <Input id="cookingTime" type="number" placeholder="90" value={cookingTime}
          onChange={(e) => setCookingTime(Number(e.target.value))} />

        <Label htmlFor="ingredients">Ingredients</Label>
        {ingredients.map((input, index) => (
          <div key={index}>
            <Input
              type="text"
              value={input}
              onChange={(event) => handleIngredientsInputChange(index, event)}
              style = {{width: '80%', marginBottom: '0'}}
              placeholder="Carrots"
            />
            <SecondaryIconButton
              icon={faCircleChevronDown}
              style={{
                cursor: (index === ingredients.length-1) ? '' : 'pointer',
                marginLeft: '5px',
                color: (index === ingredients.length-1) ? '#ccc' : '#878787'}}
              onClick={(index === ingredients.length-1) ? () => {} : () => moveIngredientsDown(index)}
            />
            <SecondaryIconButton
              icon={faCircleChevronUp}
              style={{
                cursor: (index === 0) ? '' : 'pointer',
                marginLeft: '5px',
                color: (index === 0) ? '#ccc' : '#878787'}}
              onClick={(index === 0) ? () => {} : () => moveIngredientsUp(index)}
            />
            <SecondaryIconButton
              icon={faTrashCan}
              style={{cursor: 'pointer', marginLeft: '5px'}}
              onClick={() => removeIngredients(index)}
            />
          </div>
        ))}
        <SecondaryButton onClick={addIngredients} style={{margin: '10px 5px'}}>
          Add ingredient
        </SecondaryButton>

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
        <PrimaryButton onClick={saveNewRecipe} disabled={!(dishName && cookingTime && ingredients && singleSteps)}>
          Save
        </PrimaryButton>
      </Modal.Footer>
    </Modal>
  </>);
};
export default RecipesPage;
