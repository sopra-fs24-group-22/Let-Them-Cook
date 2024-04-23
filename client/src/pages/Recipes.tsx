import { useState, ChangeEvent, useEffect } from 'react';
import { ButtonGroup, PrimaryButton, SecondaryButton } from "../components/ui/Button";
import { Label, Input, Select, Option } from "../components/ui/Input";
import Modal from 'react-bootstrap/Modal';
import { deleteRecipeAPI, getAllRecipesAPI, getCookbookAPI, postRecipeAPI } from "../api/app.api";
import MainLayout from '../components/Layout/MainLayout';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faTrashCan,
  faCircleChevronDown,
  faCircleChevronUp,
  faPenToSquare
} from "@fortawesome/free-solid-svg-icons";
import { SecondaryIconButton } from '../components/ui/Icon';
import { Container, Row, Col } from 'react-bootstrap';
import { Header2 } from '../components/ui/Header';
import { getMyUser } from '../api/user.api';
import { Tooltip } from 'react-tooltip'

const RecipesPage = () => {
  // Vars for creating a new recipe
  const [editingRecipeId, setEditingRecipeId] = useState<number>(0);
  const [show, setShow] = useState(false);
  const [dishName, setDishName] = useState<string>();
  const [privacyStatus, setPrivacyStatus] = useState<0|1>(0);
  const [cookingTime, setCookingTime] = useState<number>();
  const [ingredients, setIngredients] = useState<string[]>(['']);
  const [singleSteps, setSingleSteps] = useState<string[]>(['']);
  const handleClose = () => {
    setShow(false);
    setDishName(undefined);
    setPrivacyStatus(0);
    setCookingTime(undefined);
    setIngredients(['']);
    setSingleSteps(['']);
    setEditingRecipeId(0);
  }
  const handleShow = () => { setEditingRecipeId(0); setShow(true); };

  // Function to save a new session
  const saveRecipe = async () => {
    const body = {
      "title": dishName,
      "privacyStatus": privacyStatus,
      "cookingTimeMin": cookingTime,
      "ingredients": ingredients,
      "checklist": singleSteps,
    };
    try {
      if(editingRecipeId === 0) {
        // create a new recipe
        await postRecipeAPI(body);
      } else {
        // update the existing recipe
        // TODO Sprint 2: API call in
        alert("Updating an existing recipe isn't possible yet :)");
      }
      handleClose();
      await fetchRecipes(pageView);
    } catch (error) {
      alert("Error while saving the recipe. Please try again.");
    }
  };

  const deleteRecipe = async (recipeId: number) => {
    try {
      await deleteRecipeAPI(recipeId.toString());
      await fetchRecipes(pageView);
    } catch (error) {
      alert("Error while deleting the recipe. Please try again.");
    }
  }

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

  const [pageView, setPageView] = useState<"ALL"|"MY">('ALL');
  const [recipes, setRecipes] = useState<any[]>([]);

  const changeView = async (view: "ALL"|"MY") => {
    setPageView(view);
    await fetchRecipes(view);
  }

  const fetchRecipes = async (view: "ALL"|"MY") => {
    try {
      var res = [];
      if(view === "ALL") {
        res = await getAllRecipesAPI();
      } else {
        res = await getCookbookAPI(userId);
      }
      setRecipes(res);
    } catch (error) {
      alert("Error while loading the recipes. Please try again.");
    }
  }

  const [userId, setUserId] = useState<number>(0);
  const fetchUser = async () => {
    try {
      const user = await getMyUser();
      setUserId(user.id);
    } catch(e) {
      alert("Error while fetching the user. Please reload the page.");
    }
  }

  useEffect(() => {
    fetchUser();
    fetchRecipes("ALL");
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const buttonTopBar = (pageView === "ALL") ? (
    <>
      <PrimaryButton style={{
        width: '50%',
        borderTopRightRadius: '0',
        borderBottomRightRadius: '0',
      }}>
        All recipes
      </PrimaryButton>
      <SecondaryButton onClick={() => changeView("MY")} style={{
        width: '50%',
        borderTopLeftRadius: '0',
        borderBottomLeftRadius: '0',
      }}>
        My cookbook
      </SecondaryButton>
    </>
  ) : (
    <>
      <SecondaryButton onClick={() => changeView("ALL")} style={{
        width: '50%',
        borderTopRightRadius: '0',
        borderBottomRightRadius: '0',
      }}>
        All recipes
      </SecondaryButton>
      <PrimaryButton style={{
        width: '50%',
        borderTopLeftRadius: '0',
        borderBottomLeftRadius: '0',
      }}>
        My cookbook
      </PrimaryButton>
    </>
  );
  
  return (<>
    <MainLayout
      sidebarContent = {
        <PrimaryButton onClick={handleShow} style={{width: '100%'}}>
          Create new recipe
        </PrimaryButton>}>
      <ButtonGroup style={{marginBottom: '20px'}}>
        { buttonTopBar }
      </ButtonGroup>
      <Tooltip anchorSelect={".editRecipeIcon"} place="top">Edit recipe</Tooltip>
      <Tooltip anchorSelect={".deleteRecipeIcon"} place="top">Delete recipe</Tooltip>
      <Container>
        <Row>
          {recipes.map((recipe, index) => (
            <Col xs={4} style={{
              borderLeft: (index % 3 !== 0) ? '1px solid #ccc' : '',
              margin: '20px 0',
              padding: '10px 20px',
            }}>
              <Container>
                <Row>
                  <Col xs={11}>
                    <Header2>{recipe.title}</Header2>
                    <p style={{ fontSize: '10pt' }}>by {recipe.creatorName}</p>
                    <p style={{ fontSize: '10pt' }}>{recipe.cookingTimeMin} minutes</p>
                  </Col>
                  <Col xs={1}>
                    { recipe.creatorId === userId && <>
                      <FontAwesomeIcon
                        className="editRecipeIcon"
                        icon={ faPenToSquare }
                        style={{ cursor: 'pointer', fontSize: '12pt'}}
                        onClick={() => {
                          setEditingRecipeId(recipe.id);
                          setDishName(recipe.title);
                          setPrivacyStatus(recipe.privacyStatus);
                          setCookingTime(recipe.cookingTimeMin);
                          setIngredients(recipe.ingredients);
                          setSingleSteps(recipe.checklist);
                          setShow(true);
                        }} />
                      <FontAwesomeIcon
                        className="deleteRecipeIcon"
                        icon={ faTrashCan }
                        style={{ cursor: 'pointer', fontSize: '12pt'}}
                        onClick={() => {
                          // eslint-disable-next-line no-restricted-globals
                          if(confirm("Are you sure you want to delete the recipe \"" + recipe.title + "\"?")) {
                            deleteRecipe(recipe.id);
                          }
                        }} />
                    </>}
                  </Col>
                </Row>
              </Container>
              
            </Col>
          ))}
        </Row>
      </Container>
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

        <Label htmlFor="privacyStatus">Privacy status</Label>
        <Select id="privacyStatus" onChange={(e) => { (Number(e.target.value) === 1) ? setPrivacyStatus(1) : setPrivacyStatus(0)}} >
          <Option value={0} selected={privacyStatus === 0}>Private</Option>
          <Option value={1} selected={privacyStatus === 1}>Public</Option>
        </Select>

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
        <PrimaryButton onClick={saveRecipe} disabled={!(dishName && cookingTime && ingredients && singleSteps)}>
          Save
        </PrimaryButton>
      </Modal.Footer>
    </Modal>
  </>);
};
export default RecipesPage;
