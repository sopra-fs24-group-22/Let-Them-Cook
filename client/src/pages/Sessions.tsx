import { useState, useEffect, useRef } from "react";
import {
  PrimaryButton,
  SimplePrimaryButton,
  SecondaryButton,
  SimpleSecondaryButton,
  ButtonGroup,
  JoinButton,
  HLine,
} from "../components/ui/Button";
import { Label, Input, Select, Option } from "../components/ui/Input";
import { Accordion, Container, Row } from "react-bootstrap";
import {
  Modal,
  ModalTitle,
  ModalBody,
  ModalFooter,
  ModalHeader,
} from "../components/ui/Modal";
import MainLayout from "../components/Layout/MainLayout";
import {
  getRecipesAPI,
  getSessionsAPI,
  getCookbookAPI,
  postSessionAPI,
  postSessionRequestAPI,
  getSessionRequestsAPI,
  postSessionRequestAcceptAPI,
  postSessionRequestDenyAPI,
  getSessionRequestsUserAPI,
  getSessionMeAPI,
  putSessionAPI,
  deleteSessionAPI,
} from "../api/app.api";
import { getUsers } from "../api/user.api";
import { useNavigate, useParams } from "react-router-dom";
import { Header3 } from "../components/ui/Header";
import { formatDateTime } from "../helpers/formatDateTime";
import { ENV } from "../env";
import { useSelector } from "react-redux";
import { State } from "../features";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCheck,
  faHourglass,
  faPenToSquare,
  faSpinner,
  faTimes,
  faTrashCan,
} from "@fortawesome/free-solid-svg-icons";
import { Tooltip } from "react-tooltip";
import { NotFoundText } from "./App";
import { ErrorModal } from "../components/ui/ErrorModal";
import { ConfirmModal } from "../components/ui/ConfirmModal";

const SessionsPage = () => {
  const { user } = useSelector((state: State) => state.app);
  const navigate = useNavigate();
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const fetchUser = async () => {
    try {
      setCurrentUserId(user.id);
      await fetchAllRecipes(user.id);
    } catch (e) {
      showErrorModal("Error while fetching the user. Please reload the page.");
    }
  };

  // Get session ID for detail view
  const { session: sessionDetailId } = useParams();
  const sessionDetailRef = useRef<null | HTMLDivElement>(null);
  useEffect(() => {
    if (sessionDetailRef.current)
      sessionDetailRef.current.scrollIntoView({ behavior: "smooth" });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

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
        view === "ALL" ? await getSessionsAPI(filter) : await getSessionMeAPI();
      for (const session of res) {
        const id = session.hostId;
        const host = await getUsers(id);
        res.hostName = host.username;
      }
      setSessions(res);
    } catch (error) {
      showErrorModal("Error while loading the sessions. Please try again.");
    }
  };

  const [deleteSessionName, setDeleteSessionName] = useState<string>("");
  const [deleteSessionId, setDeleteSessionId] = useState<number>(0);
  const [showDeleteSessionModal, setShowDeleteSessionModal] =
    useState<boolean>(false);

  const deleteSession = async (sessionId: number, sessionName: string) => {
    setDeleteSessionName(sessionName);
    setDeleteSessionId(sessionId);
    setShowDeleteSessionModal(true);
  };

  const [sessionRequestsUser, setSessionRequestsUser] = useState<any[]>([]);

  const fetchUserSessionRequests = async () => {
    try {
      const res = await getSessionRequestsUserAPI();

      setSessionRequestsUser(res.userSessions);
    } catch (error) {
      showErrorModal(
        "Error while fetching the session requests. Please try again.",
      );
    }
  };

  useEffect(() => {
    fetchUser();
    fetchSessions("ALL");
    fetchUserSessionRequests();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Modal for creating a new session
  const [show, setShow] = useState(false);
  const [showRequests, setShowRequests] = useState(false);
  const [sessionRequests, setSessionRequests] = useState<any[]>([]);
  const [currentManagedSession, setCurrentManagedSession] = useState<number>();
  const [editingSessionId, setEditingSessionId] = useState<number>(0);

  const handleClose = () => {
    setShow(false);
    setRecipe(undefined);
    setSessionsName(undefined);
    setStart(undefined);
    setDuration(undefined);
    setParticipants(undefined);
    setShowRequests(false);
    setCurrentManagedSession(undefined);
    setEditingSessionId(0);
  };
  const handleShow = async () => {
    setShow(true);
    setEditingSessionId(0);
  };

  const manageRequests = async (sessionId: number) => {
    fetchSessionRequests(sessionId);
    setShowRequests(true);
    setCurrentManagedSession(sessionId);
  };

  const acceptRequest = async (
    sessionId: number | undefined,
    userId: number,
  ) => {
    const body = {
      userId: userId,
    };
    try {
      await postSessionRequestAcceptAPI(Number(sessionId), body);
      await fetchSessionRequests(Number(sessionId));
    } catch (error: any) {
      if(error.code && error.code === "ERR_BAD_REQUEST") showErrorModal("The session is already full.");
      else showErrorModal("Error while accepting the request. Please try again.");
    }
  };

  const denyRequest = async (sessionId: number | undefined, userId: number) => {
    const body = {
      userId: userId,
    };
    try {
      await postSessionRequestDenyAPI(Number(sessionId), body);
      await fetchSessionRequests(Number(sessionId));
    } catch (error) {
      showErrorModal("Error while denying the request. Please try again.");
    }
  };

  const fetchSessionRequests = async (sessionId: number) => {
    try {
      const res = await getSessionRequestsAPI(sessionId);
      setSessionRequests(res);
    } catch (error) {
      showErrorModal(
        "Error while fetching the session requests. Please try again.",
      );
    }
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
      showErrorModal(
        "Error while fetching all recipes. Please reload the page.",
      );
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
      if (editingSessionId === 0) {
        await postSessionAPI(body);
      } else {
        const updatedBody = Object.assign(body, { id: editingSessionId });
        await putSessionAPI(updatedBody);
      }
      handleClose();
      await fetchSessions(pageView);
    } catch (error) {
      showErrorModal("Error while saving the session. Please try again.");
    }
  };

  const [currentLoadingSessionRequests, setCurrentLoadingSessionRequests] =
    useState<any[]>([]);

  const requestParticipation = async (sessionId: number) => {
    setCurrentLoadingSessionRequests([
      ...currentLoadingSessionRequests,
      sessionId,
    ]);
    try {
      await postSessionRequestAPI(sessionId).then(async () => {
        await fetchUserSessionRequests();
      });
    } catch (error: any) {
      showErrorModal(error.response.data.split('"')[1]);
    }
    setCurrentLoadingSessionRequests(
      currentLoadingSessionRequests.filter((e) => e !== sessionId),
    );
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
        <SimplePrimaryButton
          style={{
            width: "50%",
            borderTopRightRadius: "0",
            borderBottomRightRadius: "0",
          }}
        >
          All sessions
        </SimplePrimaryButton>
        <SimpleSecondaryButton
          onClick={() => changeView("MY")}
          style={{
            width: "50%",
            borderTopLeftRadius: "0",
            borderBottomLeftRadius: "0",
          }}
        >
          My sessions
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
          All sessions
        </SimpleSecondaryButton>
        <SimplePrimaryButton
          style={{
            width: "50%",
            borderTopLeftRadius: "0",
            borderBottomLeftRadius: "0",
          }}
        >
          My sessions
        </SimplePrimaryButton>
      </>
    );

  // Icon for the status of the session request
  const requestStatusIcon = (
    status: string,
    position: "top" | "right" | "bottom" | "left",
  ) => (
    <>
      <Tooltip
        anchorSelect={".requestStatusIconAccepted"}
        place={position}
        style={{ zIndex: "100" }}
      >
        Accepted
      </Tooltip>
      <Tooltip
        anchorSelect={".requestStatusIconRejected"}
        place={position}
        style={{ zIndex: "100" }}
      >
        Rejected
      </Tooltip>
      <Tooltip
        anchorSelect={".requestStatusIconPending"}
        place={position}
        style={{ zIndex: "100" }}
      >
        Pending
      </Tooltip>
      {status === "ACCEPTED" ? (
        <FontAwesomeIcon
          icon={faCheck}
          style={{ color: "green", marginLeft: "5px" }}
          onClick={(e) => e.stopPropagation()}
          className="requestStatusIconAccepted"
        />
      ) : status === "REJECTED" ? (
        <FontAwesomeIcon
          icon={faTimes}
          style={{ color: "red", marginLeft: "5px" }}
          onClick={(e) => e.stopPropagation()}
          className="requestStatusIconRejected"
        />
      ) : status === "PENDING" ? (
        <FontAwesomeIcon
          icon={faHourglass}
          style={{ color: "orange", marginLeft: "5px" }}
          onClick={(e) => e.stopPropagation()}
          className="requestStatusIconPending"
        />
      ) : (
        <></>
      )}
    </>
  );

  // Check if the input is valid
  const recipeIsValid = () => recipe && recipe !== 0 && recipe !== undefined;
  const sessionNameIsValid = () => sessionName && sessionName !== "";
  const startIsValid = () => start && start !== undefined;
  const durationIsValid = () =>
    duration &&
    ENV.MIN_NUMBER_MINUTES_LENGTH <= duration &&
    duration <= ENV.MAX_NUMBER_MINUTES_LENGTH;
  const participantsIsValid = () =>
    participants && 1 <= participants && participants <= 30;

  const inputIsValid = () =>
    recipeIsValid() &&
    sessionNameIsValid() &&
    startIsValid() &&
    durationIsValid() &&
    participantsIsValid();

  const [saveErrorMessage, setSaveErrorMessage] = useState<string>("");

  useEffect(() => {
    let fragments = [];
    if (!recipeIsValid()) fragments.push("Recipe is missing.");
    if (!sessionNameIsValid()) fragments.push("Session name is missing.");
    if (!startIsValid()) fragments.push("Start date is missing.");
    if (!durationIsValid()) fragments.push("Duration is missing or invalid.");
    if (!participantsIsValid())
      fragments.push("Participants is missing or invalid.");
    setSaveErrorMessage(fragments.join(" "));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [recipe, sessionName, start, duration, participants]);

  // Error messages
  const [errorMessageModalShown, setErrorMessageModalShown] = useState(false);
  const [errorMessageModalText, setErrorMessageModalText] = useState("");

  const showErrorModal = (message: string) => {
    setErrorMessageModalText(message);
    setErrorMessageModalShown(true);
  };

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
              onChange={(e) => {
                setDateFilter(
                  e.target.value ? new Date(e.target.value) : undefined,
                );
              }}
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
        <Tooltip
          anchorSelect={".editSessionIcon"}
          place="right"
          style={{ zIndex: "100" }}
        >
          Edit session
        </Tooltip>
        <Tooltip
          anchorSelect={".deleteSessionIcon"}
          place="right"
          style={{ zIndex: "100" }}
        >
          Delete session
        </Tooltip>
        <Container fluid>
          <Row>
            <style>
              {`
                .accordion-button { background-color: #fff !important; }
                .accordion-button:focus { box-shadow: none !important; }
              `.replace(/ /g, "")}
            </style>
            <Accordion
              style={{ padding: "0" }}
              defaultActiveKey={sessionDetailId ? sessionDetailId : ""}
            >
              {sessions.map((session) => (
                <Accordion.Item
                  key={session.id}
                  eventKey={String(session.id)}
                  style={{
                    width: "100%",
                    marginTop: "5px",
                    border: "none",
                  }}
                  ref={
                    sessionDetailId === String(session.id)
                      ? sessionDetailRef
                      : null
                  }
                >
                  <Accordion.Header
                    style={{
                      display: "flex",
                      background: "#f0f0f0",
                    }}
                  >
                    <div
                      style={{
                        textAlign: "left",
                        width: "100%",
                        color: "#000",
                      }}
                    >
                      <div
                        style={{
                          display: "inline-block",
                          marginTop: "10px",
                          marginLeft: 10,
                          fontSize: "1.6rem",
                          fontWeight: "500",
                          color: "#666",
                        }}
                      >
                        {session.sessionName}
                      </div>
                      {session.host === currentUserId && (
                        <span
                          style={{
                            fontSize: "12pt",
                            marginLeft: "10px",
                            color: "#666",
                          }}
                        >
                          <FontAwesomeIcon
                            className="editSessionIcon"
                            icon={faPenToSquare}
                            style={{
                              cursor: "pointer",
                              fontSize: "12pt",
                              color: "#666",
                            }}
                            onClick={(e) => {
                              e.stopPropagation();
                              setEditingSessionId(session.id);
                              setRecipe(session.recipe);
                              setStart(new Date(session.date));
                              setDuration(session.duration);
                              setParticipants(session.maxParticipantCount);
                              setSessionsName(session.sessionName);
                              setShow(true);
                            }}
                          />
                          <FontAwesomeIcon
                            className="deleteSessionIcon"
                            icon={faTrashCan}
                            style={{
                              cursor: "pointer",
                              fontSize: "12pt",
                              marginLeft: "10px",
                            }}
                            onClick={(e) => {
                              e.stopPropagation();
                              deleteSession(session.id, session.sessionName);
                            }}
                          />
                        </span>
                      )}
                      {currentUserId !== session.host && (
                        <span style={{ fontSize: "12pt", marginLeft: "10px" }}>
                          {requestStatusIcon(
                            sessionRequestsUser[session.id],
                            "right",
                          )}
                        </span>
                      )}
                      <span style={{ float: "right", marginRight: "10px" }}>
                        <JoinButton
                          onClick={() => navigate("/sessions/" + session.id)}
                          style={{
                            display:
                              sessionRequestsUser[session.id] === "ACCEPTED" ||
                              currentUserId === session.host
                                ? "inline-block"
                                : "none",
                          }}
                        >
                          Join
                        </JoinButton>
                        <JoinButton
                          onClick={() => requestParticipation(session.id)}
                          style={{
                            display:
                              currentUserId !== session.host &&
                              sessionRequestsUser[session.id] !== "ACCEPTED" &&
                              sessionRequestsUser[session.id] !== "REJECTED" &&
                              sessionRequestsUser[session.id] !== "PENDING"
                                ? "inline-block"
                                : "none",
                          }}
                        >
                          {currentLoadingSessionRequests.includes(
                            session.id,
                          ) ? (
                            <FontAwesomeIcon icon={faSpinner} spin={true} />
                          ) : (
                            "Request participation"
                          )}
                        </JoinButton>
                        <JoinButton
                          onClick={() => manageRequests(session.id)}
                          style={{
                            display:
                              currentUserId === session.host
                                ? "inline-block"
                                : "none",
                          }}
                        >
                          Manage requests
                        </JoinButton>
                      </span>
                    </div>
                  </Accordion.Header>
                  <Accordion.Body style={{ background: "#f0f0f0" }}>
                    <div>
                      <b>Start:</b> {formatDateTime(session.date)}
                    </div>
                    <div>
                      <b>Host:</b> {session.hostName}
                    </div>
                    <div>
                      <b>Max Participants:</b> {session.maxParticipantCount}
                    </div>
                    {allRecipes.map((recipe) => {
                      if (recipe.id === session.recipe) {
                        return (
                          <div key={recipe.id}>
                            <b>Recipe:</b> {recipe.title}
                          </div>
                        );
                      }
                      return null;
                    })}
                  </Accordion.Body>
                </Accordion.Item>
              ))}
            </Accordion>
            {sessions.length === 0 && (
              <NotFoundText style={{ textAlign: "center" }}>
                No sessions found.
              </NotFoundText>
            )}
          </Row>
        </Container>
      </MainLayout>

      {/* Modal for creating a new session */}
      <Modal show={show} onHide={handleClose}>
        <ModalHeader>
          <ModalTitle>
            {editingSessionId === 0 ? "Create new session" : "Edit session"}
          </ModalTitle>
        </ModalHeader>
        <ModalBody>
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
        </ModalBody>
        <ModalFooter>
          <SecondaryButton onClick={handleClose}>Cancel</SecondaryButton>
          {!inputIsValid() && (
            <Tooltip anchorSelect={".saveSessionButton"} place="top">
              {saveErrorMessage}
            </Tooltip>
          )}
          <PrimaryButton
            onClick={saveNewSession}
            disabled={!inputIsValid()}
            className="saveSessionButton"
          >
            Save
          </PrimaryButton>
        </ModalFooter>
      </Modal>
      <Modal show={showRequests} onHide={handleClose}>
        <ModalHeader>
          <ModalTitle>Manage requests</ModalTitle>
        </ModalHeader>
        <ModalBody>
          <Container>
            {sessionRequests.map((request, index) => (
              <div style={{ textAlign: "left", width: "100%", height: "45px" }}>
                <span style={{ lineHeight: "33px", verticalAlign: "middle" }}>
                  <span style={{ marginRight: "10px" }}>
                    {request.username}
                  </span>
                  {requestStatusIcon(request.queueStatus, "right")}
                </span>
                <span style={{ float: "right" }}>
                  <PrimaryButton
                    style={{
                      display:
                        request.queueStatus === "REJECTED" ||
                        request.queueStatus === "ACCEPTED"
                          ? "none"
                          : "inline-block",
                      fontSize: "0.8em",
                      marginRight: "5px",
                    }}
                    onClick={() => {
                      acceptRequest(currentManagedSession, request.userId);
                    }}
                  >
                    Accept
                  </PrimaryButton>
                  <SecondaryButton
                    style={{
                      display:
                        request.queueStatus === "REJECTED" ||
                        request.queueStatus === "ACCEPTED"
                          ? "none"
                          : "inline-block",
                      fontSize: "0.8em",
                    }}
                    onClick={() => {
                      denyRequest(currentManagedSession, request.userId);
                    }}
                  >
                    Deny
                  </SecondaryButton>
                </span>
              </div>
            ))}
            {sessionRequests.length === 0 && (
              <Row>
                <p>No requests yet for this session.</p>
              </Row>
            )}
          </Container>
        </ModalBody>
        <ModalFooter>
          <SecondaryButton onClick={handleClose}>Close</SecondaryButton>
        </ModalFooter>
      </Modal>

      {/* Modal for error messages */}
      <ErrorModal
        show={errorMessageModalShown}
        onClose={() => setErrorMessageModalShown(false)}
        error={errorMessageModalText}
      />

      {/* Modal for deleting session */}
      <ConfirmModal
        title="Are you sure?"
        error={
          'Are you sure you want to delete the session "' +
          deleteSessionName +
          '"?'
        }
        show={showDeleteSessionModal}
        onAccept={async () => {
          try {
            await deleteSessionAPI(deleteSessionId);
            await fetchSessions(pageView);
          } catch (error) {
            showErrorModal(
              "Error while deleting the session. Please try again.",
            );
          }
          setShowDeleteSessionModal(false);
        }}
        onDecline={() => {
          setShowDeleteSessionModal(false);
        }}
      />
    </>
  );
};

export default SessionsPage;
