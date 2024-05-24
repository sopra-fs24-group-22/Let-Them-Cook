import { useState, ChangeEvent, useEffect } from "react";
import {
  ButtonGroup,
  HLine,
  PrimaryButton,
  SecondaryButton,
  SimplePrimaryButton,
  SimpleSecondaryButton,
} from "../components/ui/Button";
import { Label, Input, Select, Option } from "../components/ui/Input";
import {
  deleteRecipeAPI,
  getRecipesAPI,
  getCookbookAPI,
  postRecipeAPI,
  getRecipeAPI,
  addRecipeToCookbookAPI,
  removeRecipeFromCookbookAPI,
  putRecipeAPI,
  postRateRecipeAPI,
} from "../api/app.api";
import MainLayout from "../components/Layout/MainLayout";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faTrashCan,
  faCircleChevronDown,
  faCircleChevronUp,
  faPenToSquare,
  faHourglass,
  faMinus,
  faPlus,
} from "@fortawesome/free-solid-svg-icons";
import { SecondaryIconButton } from "../components/ui/Icon";
import { Container, Row, Col, ListGroup } from "react-bootstrap";
import {
  Modal,
  ModalBody,
  ModalHeader,
  ModalTitle,
  ModalFooter,
} from "../components/ui/Modal";
import { Header2, Header3, SecondaryText } from "../components/ui/Header";
import { Tooltip } from "react-tooltip";
import { useParams } from "react-router-dom";
import { StarRating } from "../components/ui/StarRating";
import { ENV } from "../env";
import { useSelector } from "react-redux";
import { State } from "../features";
import styled from "styled-components";
import { NotFoundText } from "./App";

const RecipesPage = () => {
  const { user } = useSelector((state: State) => state.app);

  // Parse the URL parameters
  const { param: URLparam } = useParams();
  const URLrecipeId =
    URLparam !== undefined && URLparam.startsWith("id=")
      ? URLparam.split("id=")[1]
      : undefined;
  const URLchefName =
    URLparam !== undefined && URLparam.startsWith("chef=")
      ? URLparam.split("chef=")[1]
      : undefined;

  // Vars for creating a new recipe
  const [editingRecipeId, setEditingRecipeId] = useState<number>(0);
  const [viewingRecipeId, setViewingRecipeId] = useState<number>(0);
  const [showForm, setShowForm] = useState(false);
  const [showDetails, setShowDetails] = useState(false);
  const [dishName, setDishName] = useState<string>();
  const [privacyStatus, setPrivacyStatus] = useState<0 | 1>(0);
  const [cookingTime, setCookingTime] = useState<number>();
  const [ingredients, setIngredients] = useState<string[]>([""]);
  const [singleSteps, setSingleSteps] = useState<string[]>([""]);
  const [dishCreator, setDishCreator] = useState<string>("");

  // only for view
  const [nrRating, setNrRating] = useState<number>(0);
  const [avgTotalRating, setAvgTotalRating] = useState<number>(0);

  const handleClose = () => {
    setShowForm(false);
    setShowDetails(false);
    setDishName(undefined);
    setPrivacyStatus(0);
    setCookingTime(undefined);
    setIngredients([""]);
    setSingleSteps([""]);
    setDishCreator("");
    setEditingRecipeId(0);
    setNrRating(0);
    setAvgTotalRating(0);
  };
  const handleShowForm = () => {
    setEditingRecipeId(0);
    setShowForm(true);
  };

  const handleShowDetails = (id: number) => {
    fetchRecipe(id);
    setViewingRecipeId(id);
    setShowDetails(true);
  };

  const [errorMessageModalShown, setErrorMessageModalShown] = useState(false);
  const [errorMessageModalText, setErrorMessageModalText] = useState("");

  const showErrorModal = (message: string) => {
    setErrorMessageModalText(message);
    setErrorMessageModalShown(true);
  };

  const rateRecipe = async (recipeId: number, rating: number) => {
    await postRateRecipeAPI(recipeId, rating).catch((error) => {
      if (error.code === "ERR_BAD_REQUEST")
        showErrorModal("You cannot rate your own recipe.");
      else showErrorModal("Error while rating the recipe. Please try again.");
    });
  };

  const rateRecipeAndReloadSingleRecipe = async (
    recipeId: number,
    rating: number,
  ) => {
    await rateRecipe(recipeId, rating).then(async () => {
      await fetchRecipe(recipeId);
    });
  };

  // Function to save a new session
  const saveRecipe = async () => {
    const body = {
      title: dishName,
      privacyStatus: privacyStatus,
      cookingTimeMin: cookingTime,
      ingredients: ingredients,
      checklist: singleSteps,
    };
    try {
      if (editingRecipeId === 0) {
        // create a new recipe
        await postRecipeAPI(body);
      } else {
        // update the existing recipe
        const updatedBody = Object.assign(body, { id: editingRecipeId });
        await putRecipeAPI(updatedBody);
      }
      handleClose();
      await fetchRecipes(pageView, user.id);
    } catch (error) {
      showErrorModal("Error while saving the recipe. Please try again.");
    }
  };

  // Function to fetch a recipe
  const fetchRecipe = async (id: number) => {
    try {
      const res = await getRecipeAPI(id);
      setDishName(res.title);
      setPrivacyStatus(res.privacyStatus);
      setCookingTime(res.cookingTimeMin);
      setIngredients(res.ingredients);
      setSingleSteps(res.checklist);
      setDishCreator(res.creatorName);
      setNrRating(res.nrRatings);
      setAvgTotalRating(res.avgTotalRating);
    } catch (e) {
      showErrorModal("Error while loading the recipe. Please try again.");
    }
  };

  const deleteRecipe = async (recipeId: number) => {
    try {
      await deleteRecipeAPI(recipeId.toString());
      await fetchRecipes(pageView, user.id);
    } catch (error) {
      showErrorModal("Error while deleting the recipe. Please try again.");
    }
  };

  // Functions for single steps
  const addSingleStep = () => setSingleSteps([...singleSteps, ""]);
  const handleSingleStepsInputChange = (
    index: number,
    event: ChangeEvent<HTMLInputElement>,
  ) => {
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

  // Functions for ingredients
  const addIngredients = () => setIngredients([...ingredients, ""]);
  const handleIngredientsInputChange = (
    index: number,
    event: ChangeEvent<HTMLInputElement>,
  ) => {
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
    if (index === ingredients.length - 1) return;
    const values = [...ingredients];
    [values[index], values[index + 1]] = [values[index + 1], values[index]];
    setIngredients(values);
  };
  const moveIngredientsUp = (index: number) => {
    if (index === 0) return;
    const values = [...ingredients];
    [values[index], values[index - 1]] = [values[index - 1], values[index]];
    setIngredients(values);
  };

  const [pageView, setPageView] = useState<"ALL" | "MY">("ALL");
  const [recipes, setRecipes] = useState<any[]>([]);
  const [cookbookRecipeIds, setCookbookRecipeIds] = useState<any[]>([]);

  // Filter variables for recipe fetch
  const [recipeTitleFilter, setRecipeTitleFilter] = useState<
    string | undefined
  >(undefined);
  const [creatorNameFilter, setCreatorNameFilter] = useState<
    string | undefined
  >(URLchefName);
  const [maxCookingTimeFilter, setMaxCookingTimeFilter] = useState<
    number | undefined
  >(undefined);

  useEffect(() => {
    if (user.id && user.id !== 0) reloadRecipes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [recipeTitleFilter, creatorNameFilter, maxCookingTimeFilter]);

  const deleteFilter = async () => {
    setRecipeTitleFilter(undefined);
    setCreatorNameFilter(undefined);
    setMaxCookingTimeFilter(undefined);
  };

  const changeView = async (view: "ALL" | "MY") => {
    setPageView(view);
    await fetchRecipes(view, user.id);
  };

  const fetchRecipes = async (view: "ALL" | "MY", UserId: number) => {
    try {
      var resRecipes = [];
      var resCookbook = [];

      // build object for filtering
      var filter = {};
      filter = { ...filter, limit: 10000 };
      if (maxCookingTimeFilter)
        filter = { ...filter, cookingTimeMin: maxCookingTimeFilter };
      if (recipeTitleFilter) filter = { ...filter, title: recipeTitleFilter };
      if (creatorNameFilter)
        filter = { ...filter, creatorName: creatorNameFilter };

      if (view === "ALL") {
        resRecipes = await getRecipesAPI(filter);
        resCookbook = await getCookbookAPI(UserId);
      } else {
        resRecipes = await getCookbookAPI(UserId);
        resCookbook = resRecipes;
      }
      setRecipes(resRecipes);
      setCookbookRecipeIds(resCookbook.map((e: any) => e.id));
    } catch (error) {
      showErrorModal("Error while loading the recipes. Please try again.");
    }
  };

  const reloadRecipes = async () => {
    await fetchRecipes(pageView, user.id);
  };

  const addRecipeToCookbook = async (recipeId: number) => {
    try {
      await addRecipeToCookbookAPI(recipeId);
      await reloadRecipes();
    } catch (error) {
      showErrorModal(
        "Error while adding the recipe to your cookbook. Please try again.",
      );
    }
  };

  const removeRecipeFromCookbook = async (recipeId: number) => {
    try {
      await removeRecipeFromCookbookAPI(recipeId);
      await reloadRecipes();
    } catch (error) {
      showErrorModal(
        "Error while adding the recipe to your cookbook. Please try again.",
      );
    }
  };

  const initFetch = async () => {
    try {
      fetchRecipes("ALL", user.id);
    } catch (e) {
      showErrorModal("Error while fetching the data. Please reload the page.");
    }
  };

  const initShowRecipeDetails = () => {
    if (URLrecipeId && URLrecipeId !== "0") {
      handleShowDetails(Number(URLrecipeId));
    }
  };

  useEffect(() => {
    initFetch();
    initShowRecipeDetails();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const buttonTopBar =
    pageView === "ALL" ? (
      <>
        <SimplePrimaryButton
          style={{
            width: "50%",
            borderTopRightRadius: "0",
            borderBottomRightRadius: "0",
          }}
        >
          All recipes
        </SimplePrimaryButton>
        <SimpleSecondaryButton
          onClick={() => changeView("MY")}
          style={{
            width: "50%",
            borderTopLeftRadius: "0",
            borderBottomLeftRadius: "0",
          }}
        >
          My cookbook
        </SimpleSecondaryButton>
      </>
    ) : (
      <>
        <SimpleSecondaryButton
          onClick={() => changeView("ALL")}
          style={{
            width: "50%",
            borderTopRightRadius: "0",
            borderBottomRightRadius: "0",
          }}
        >
          All recipes
        </SimpleSecondaryButton>
        <SimplePrimaryButton
          style={{
            width: "50%",
            borderTopLeftRadius: "0",
            borderBottomLeftRadius: "0",
          }}
        >
          My cookbook
        </SimplePrimaryButton>
      </>
    );

  // Recipe modal validity messages
  const dishNameIsValid = () => dishName && dishName !== "";
  const cookingTimeIsValid = () =>
    cookingTime &&
    cookingTime > 0 &&
    cookingTime <= ENV.MAX_NUMBER_MINUTES_LENGTH;
  const ingredientsIsValid = () =>
    ingredients && !ingredients.includes("") && ingredients.length > 0;
  const singleStepsIsValid = () =>
    singleSteps && !singleSteps.includes("") && singleSteps.length > 0;

  const inputIsValid = () =>
    dishNameIsValid() &&
    cookingTimeIsValid() &&
    ingredientsIsValid() &&
    singleStepsIsValid();

  const [saveErrorMessage, setSaveErrorMessage] = useState<string>("");

  useEffect(() => {
    let fragments = [];
    if (!dishNameIsValid()) fragments.push("Dish name is missing.");
    if (!cookingTimeIsValid())
      fragments.push("Cooking time is missing or invalid.");
    if (!ingredientsIsValid())
      fragments.push("Ingredients are missing or empty.");
    if (!singleStepsIsValid())
      fragments.push("Single steps are missing or empty.");
    setSaveErrorMessage(fragments.join(" "));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [dishName, cookingTime, ingredients, singleSteps]);

  return (
    <>
      <MainLayout
        sidebarContent={
          <>
            <PrimaryButton onClick={handleShowForm} style={{ width: "100%" }}>
              Create new recipe
            </PrimaryButton>

            {pageView === "ALL" && (
              <>
                <HLine />

                <Header3 style={{ marginBottom: "10px" }}>Filter:</Header3>

                <Label htmlFor="recipeTitleFilter" style={{ marginLeft: "0" }}>
                  Name of the recipe
                </Label>
                <Input
                  id="recipeTitleFilter"
                  type="text"
                  maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
                  style={{ width: "100%", marginTop: "0", marginLeft: "0" }}
                  value={recipeTitleFilter ? recipeTitleFilter : ""}
                  onChange={(e) => setRecipeTitleFilter(e.target.value)}
                />

                <Label htmlFor="creatorNameFilter" style={{ marginLeft: "0" }}>
                  Creator name
                </Label>
                <Input
                  id="creatorNameFilter"
                  type="text"
                  maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
                  style={{ width: "100%", marginTop: "0", marginLeft: "0" }}
                  value={creatorNameFilter ? creatorNameFilter : ""}
                  onChange={(e) => setCreatorNameFilter(e.target.value)}
                />

                <Label htmlFor="cookingTimeFilter" style={{ marginLeft: "0" }}>
                  Max cooking time (minutes)
                </Label>
                <Input
                  id="cookingTimeFilter"
                  type="number"
                  min={ENV.MIN_NUMBER_MINUTES_LENGTH}
                  max={ENV.MAX_NUMBER_MINUTES_LENGTH}
                  style={{ width: "100%", marginTop: "0", marginLeft: "0" }}
                  value={maxCookingTimeFilter ? maxCookingTimeFilter : ""}
                  onChange={(e) =>
                    setMaxCookingTimeFilter(Number(e.target.value))
                  }
                />

                <SecondaryButton
                  onClick={deleteFilter}
                  style={{ width: "100%" }}
                >
                  Delete all filter
                </SecondaryButton>
              </>
            )}
          </>
        }
      >
        <ButtonGroup style={{ marginBottom: "20px" }}>
          {buttonTopBar}
        </ButtonGroup>
        <Tooltip anchorSelect={".editRecipeIcon"} place="top">
          Edit recipe
        </Tooltip>
        <Tooltip anchorSelect={".deleteRecipeIcon"} place="top">
          Delete recipe
        </Tooltip>
        <Tooltip anchorSelect={".addRecipeToCookbookIcon"} place="top">
          Add recipe to cookbook
        </Tooltip>
        <Tooltip anchorSelect={".removeRecipeFromCookbookIcon"} place="top">
          Remove recipe from cookbook
        </Tooltip>
        <Container>
          <Row>
            {recipes.map((recipe, index) => (
              <Col xs={4}>
                <Item>
                  <Container>
                    <Row>
                      <Col xs={11}>
                        <Header2
                          style={{ cursor: "pointer" }}
                          onClick={() => handleShowDetails(recipe.id)}
                        >
                          {recipe.title}
                        </Header2>
                        <p
                          style={{
                            fontSize: "1.5rem",
                            color: "#918173",
                            marginBottom: 20,
                          }}
                        >
                          by {recipe.creatorName}
                        </p>
                        <p style={{ fontSize: "1.5rem", color: "#918173" }}>
                          <FontAwesomeIcon
                            icon={faHourglass}
                            style={{ margin: "0 5px 0 0" }}
                          />
                          {recipe.cookingTimeMin} minutes |
                          <StarRating
                            id={recipe.id}
                            avgRating={recipe.avgTotalRating}
                            nrRating={recipe.nrRatings}
                            callbackFunction={async (
                              recipeId: number,
                              rating: number,
                            ) => {
                              await rateRecipe(recipeId, rating).then(
                                async () => {
                                  await reloadRecipes();
                                },
                              );
                            }}
                          />
                        </p>
                      </Col>
                      <Col xs={1}>
                        {recipe.creatorId === user.id && (
                          <>
                            <FontAwesomeIcon
                              className="editRecipeIcon"
                              icon={faPenToSquare}
                              style={{
                                cursor: "pointer",
                                fontSize: "1.7rem",
                                color: "#878787",
                              }}
                              onClick={() => {
                                setEditingRecipeId(recipe.id);
                                setDishName(recipe.title);
                                setPrivacyStatus(recipe.privacyStatus);
                                setCookingTime(recipe.cookingTimeMin);
                                setIngredients(recipe.ingredients);
                                setSingleSteps(recipe.checklist);
                                setShowForm(true);
                              }}
                            />
                            <FontAwesomeIcon
                              className="deleteRecipeIcon"
                              icon={faTrashCan}
                              style={{
                                cursor: "pointer",
                                fontSize: "1.7rem",
                                color: "#878787",
                              }}
                              onClick={() => {
                                if (
                                  // eslint-disable-next-line no-restricted-globals
                                  confirm(
                                    'Are you sure you want to delete the recipe "' +
                                      recipe.title +
                                      '"?',
                                  )
                                ) {
                                  deleteRecipe(recipe.id);
                                }
                              }}
                            />
                          </>
                        )}
                        {recipe.creatorId !== user.id &&
                          !cookbookRecipeIds.includes(recipe.id) && (
                            <FontAwesomeIcon
                              className="addRecipeToCookbookIcon"
                              icon={faPlus}
                              style={{
                                cursor: "pointer",
                                fontSize: "1.8rem",
                                color: "#878787",
                              }}
                              onClick={() => {
                                addRecipeToCookbook(recipe.id);
                              }}
                            />
                          )}
                        {recipe.creatorId !== user.id &&
                          cookbookRecipeIds.includes(recipe.id) && (
                            <FontAwesomeIcon
                              className="removeRecipeFromCookbookIcon"
                              icon={faMinus}
                              style={{
                                cursor: "pointer",
                                fontSize: "1.8rem",
                                color: "#878787",
                              }}
                              onClick={() => {
                                removeRecipeFromCookbook(recipe.id);
                              }}
                            />
                          )}
                      </Col>
                    </Row>
                  </Container>
                </Item>
              </Col>
            ))}
            {recipes.length === 0 && (
              <NotFoundText style={{ textAlign: "center" }}>
                No recipes found.
              </NotFoundText>
            )}
          </Row>
        </Container>
      </MainLayout>

      {/* Modal for creating a new recipe */}
      <Modal show={showForm} onHide={handleClose}>
        <ModalHeader>
          <ModalTitle>
            {editingRecipeId === 0
              ? "Create new recipe"
              : 'Edit recipe "' + dishName + '"'}
          </ModalTitle>
        </ModalHeader>
        <ModalBody>
          <Label htmlFor="dishName">Dish name</Label>
          <Input
            id="dishName"
            type="text"
            maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
            value={dishName}
            placeholder="Add name"
            style={{ width: "80%" }}
            onChange={(e) => setDishName(e.target.value)}
          />

          <Label htmlFor="privacyStatus">Privacy status</Label>
          <Select
            id="privacyStatus"
            onChange={(e) => {
              Number(e.target.value) === 1
                ? setPrivacyStatus(1)
                : setPrivacyStatus(0);
            }}
          >
            <Option value={0} selected={privacyStatus === 0}>
              Private
            </Option>
            <Option value={1} selected={privacyStatus === 1}>
              Public
            </Option>
          </Select>

          <Label htmlFor="cookingTime">Max cooking time (minutes)</Label>
          <Input
            id="cookingTime"
            type="number"
            min={ENV.MIN_NUMBER_MINUTES_LENGTH}
            max={ENV.MAX_NUMBER_MINUTES_LENGTH}
            placeholder="90"
            value={cookingTime}
            onChange={(e) => setCookingTime(Number(e.target.value))}
          />

          <Label htmlFor="ingredients">Ingredients</Label>
          {ingredients.map((input, index) => (
            <div key={index}>
              <Input
                type="text"
                maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
                value={input}
                onChange={(event) => handleIngredientsInputChange(index, event)}
                style={{ width: "80%", marginBottom: "0" }}
                placeholder="Add ingredient"
              />
              <SecondaryIconButton
                icon={faCircleChevronDown}
                style={{
                  cursor: index === ingredients.length - 1 ? "" : "pointer",
                  marginLeft: "5px",
                  color: index === ingredients.length - 1 ? "#ccc" : "#878787",
                }}
                onClick={
                  index === ingredients.length - 1
                    ? () => {}
                    : () => moveIngredientsDown(index)
                }
              />
              <SecondaryIconButton
                icon={faCircleChevronUp}
                style={{
                  cursor: index === 0 ? "" : "pointer",
                  marginLeft: "5px",
                  color: index === 0 ? "#ccc" : "#878787",
                }}
                onClick={
                  index === 0 ? () => {} : () => moveIngredientsUp(index)
                }
              />
              <SecondaryIconButton
                icon={faTrashCan}
                style={{ cursor: "pointer", marginLeft: "5px" }}
                onClick={() => removeIngredients(index)}
              />
            </div>
          ))}
          <SecondaryButton
            onClick={addIngredients}
            style={{ margin: "10px 5px" }}
          >
            Add ingredient
          </SecondaryButton>

          <Label htmlFor="singleSteps">Single steps</Label>
          {singleSteps.map((input, index) => (
            <div key={index}>
              <Input
                type="text"
                maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
                value={input}
                onChange={(event) => handleSingleStepsInputChange(index, event)}
                style={{ width: "80%", marginBottom: "0" }}
                placeholder="Add step"
              />
              <SecondaryIconButton
                icon={faCircleChevronDown}
                style={{
                  cursor: index === singleSteps.length - 1 ? "" : "pointer",
                  marginLeft: "5px",
                  color: index === singleSteps.length - 1 ? "#ccc" : "#878787",
                }}
                onClick={
                  index === singleSteps.length - 1
                    ? () => {}
                    : () => moveSingleStepDown(index)
                }
              />
              <SecondaryIconButton
                icon={faCircleChevronUp}
                style={{
                  cursor: index === 0 ? "" : "pointer",
                  marginLeft: "5px",
                  color: index === 0 ? "#ccc" : "#878787",
                }}
                onClick={index === 0 ? () => {} : () => moveSingleStepUp(index)}
              />
              <SecondaryIconButton
                icon={faTrashCan}
                style={{ cursor: "pointer", marginLeft: "5px" }}
                onClick={() => removeSingleStep(index)}
              />
            </div>
          ))}
          <SecondaryButton
            onClick={addSingleStep}
            style={{ margin: "10px 5px" }}
          >
            Add step
          </SecondaryButton>
        </ModalBody>
        <ModalFooter>
          <SecondaryButton onClick={handleClose}>Cancel</SecondaryButton>
          {!inputIsValid() && (
            <Tooltip anchorSelect={".saveRecipeButton"} place="top">
              {saveErrorMessage}
            </Tooltip>
          )}
          <PrimaryButton
            onClick={saveRecipe}
            disabled={!inputIsValid()}
            className="saveRecipeButton"
          >
            Save
          </PrimaryButton>
        </ModalFooter>
      </Modal>

      {/* Modal for detail view */}
      <Modal show={showDetails} onHide={handleClose}>
        <ModalHeader>
          <ModalTitle>{dishName}</ModalTitle>
        </ModalHeader>
        <ModalBody>
          <SecondaryText>
            by {dishCreator} |
            <FontAwesomeIcon icon={faHourglass} style={{ margin: "0 5px" }} />
            {cookingTime}min |
            <StarRating
              id={viewingRecipeId}
              avgRating={avgTotalRating}
              nrRating={nrRating}
              callbackFunction={(recipeId: number, rating: number) => {
                rateRecipeAndReloadSingleRecipe(recipeId, rating).then(
                  async () => {
                    await reloadRecipes();
                  },
                );
              }}
            />
          </SecondaryText>

          <Header2 style={{ margin: "20px 0" }}>Ingredients</Header2>
          <ListGroup variant="flush">
            {ingredients.map((input, index) => (
              <ListGroup.Item>{input}</ListGroup.Item>
            ))}
          </ListGroup>

          <Header2 style={{ margin: "20px 0" }}>Steps</Header2>
          <ListGroup variant="flush">
            {singleSteps.map((input, index) => (
              <ListGroup.Item>{input}</ListGroup.Item>
            ))}
          </ListGroup>
        </ModalBody>
        <ModalFooter>
          <SecondaryButton
            onClick={() => {
              setShowDetails(false);
              setDishCreator("");
              setShowForm(true);
            }}
          >
            Copy recipe
          </SecondaryButton>
          <SecondaryButton onClick={handleClose}>Close</SecondaryButton>
        </ModalFooter>
      </Modal>

      {/* Modal for error messages */}
      <Modal
        show={errorMessageModalShown}
        onHide={() => setErrorMessageModalShown(false)}
      >
        <ModalHeader>
          <ModalTitle>Error</ModalTitle>
        </ModalHeader>
        <ModalBody>
          <SecondaryText>{errorMessageModalText}</SecondaryText>
        </ModalBody>
        <ModalFooter>
          <SecondaryButton onClick={() => setErrorMessageModalShown(false)}>
            Ok
          </SecondaryButton>
        </ModalFooter>
      </Modal>
    </>
  );
};
export default RecipesPage;

const Item = styled.div`
  background-color: white;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 15px;
`;
