import { useEffect, useState } from "react";
import MainLayout from "../components/Layout/MainLayout";
import { Header1 } from "../components/ui/Header";
import { getMyUser } from "../api/user.api";
import { MeetingProvider, MeetingConsumer } from "@videosdk.live/react-sdk";
import { authToken } from "../components/VideoCall/API";
import { Container } from "../components/VideoCall/Container";
import { Link, useNavigate, useParams } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner } from "@fortawesome/free-solid-svg-icons";
import {
  getChecklistAPI,
  getRecipeAPI,
  getSessionAPI,
  getSessionCredentialsAPI,
  putChecklistAPI,
} from "../api/app.api";
import { ListGroup } from "react-bootstrap";

const SessionViewer = () => {
  const { sessionID } = useParams();
  const navigate = useNavigate();

  // States for VideoCall
  const [meetingId, setMeetingId] = useState<string | null>(null);
  const onMeetingLeave = () => {
    setMeetingId(null);
    navigate("/sessions");
  };
  const [mode, setMode] = useState<"CONFERENCE" | "VIEWER">("CONFERENCE");

  // Layout States
  const [username, setUsername] = useState<string>("");

  const getMeetingAndToken = async (id: string | null) => {
    // API-Call
    try {
      const res = await getSessionCredentialsAPI(Number(sessionID));

      // Set Meeting ID
      const meetingId = id === null ? res.roomId : id;
      setMeetingId(meetingId);

      // Set Mode (CONFERENCE if Session owner, VIEWER if not)
      const user = await getMyUser();
      setUsername(user.username);

      if (res.hostId === user.id) {
        setMode("CONFERENCE");
      } else {
        setMode("VIEWER");
      }
    } catch (e) {
      alert("Error while loading the session. Please try again.");
      navigate("/sessions");
    }
  };

  const [recipe, setRecipe] = useState<any>();

  //Checklist call
  const fetchRecipes = async () => {
    try {
      const session = await getSessionAPI(Number(sessionID));
      const res1 = await getRecipeAPI(Number(session.recipe));
      setRecipe(res1);
    } catch (error) {
      alert("Error while loading the recipes. Please try again.");
    }
  };
  const [currentParticipantsCount, setCurrentParticipantCount] =
    useState<number>(0);
  const fetchSessionInfo = async () => {
    try {
      const sessionInfo = await getSessionAPI(Number(sessionID));
      setCurrentParticipantCount(sessionInfo.currentParticipantCount);
    } catch (error) {
      console.error("Error fetching session info:", error);
    }
  };

  const [stepCounts, setStepCounts] = useState<{ [key: number]: number }>({});

  const fetchChecklistState = async () => {
    try {
      const count = await getChecklistAPI(Number(sessionID));

      setStepCounts(count);
    } catch (error) {
      console.error("Error fetching checklist state:", error);
      // Handle error, e.g., show an error message to the user
    }
  };

  const [checkedItems, setCheckedItems] = useState<{ [key: number]: boolean }>(
    {},
  );
  const handleCheckboxChange = async (stepIndex: number) => {
    const isChecked = userChecklistData[stepIndex] || false;
    setUserChecklistData({
      ...userChecklistData,
      [stepIndex]: !isChecked,
    });
    const body = {
      stepIndex,
      isChecked: !isChecked,
    };
    try {
      await putChecklistAPI(Number(sessionID), body);
    } catch (error) {
      console.error("Error updating checklist item:", error);
    }
  };

  const [userChecklistData, setUserChecklistData] = useState<{
    [key: number]: boolean;
  }>({});

  const fetchChecklistData = async (): Promise<void> => {
    try {
      const checklistData = await getChecklistAPI(Number(sessionID));
      const { currentStepValues, recipeSteps } = checklistData;

      // Ensure userID is not null before accessing currentStepValues
      const userChecklist = userID ? currentStepValues[userID] || {} : {};
      setUserChecklistData(userChecklist);

      const stepCounts: { [key: number]: number } = {};
      for (let stepIndex = 0; stepIndex < recipeSteps; stepIndex++) {
        let count = 0;
        for (const userId in currentStepValues) {
          if (currentStepValues[userId][stepIndex]) {
            count++;
          }
        }
        stepCounts[stepIndex] = count;
      }

      setStepCounts(stepCounts);
    } catch (error) {
      console.error("Error fetching checklist state:", error);
    }

    // Fetch session info and schedule next fetch
    await fetchSessionInfo();
    if (window.location.pathname.startsWith("/sessions/")) {
      setTimeout(fetchChecklistData, 3000);
    }
  };

  const [userID, setUserID] = useState<number | null>(null);

  const fetchData = async () => {
    try {
      const user = await getMyUser();
      setUserID(user.id);

      // Fetch checklist data only after user ID is set
      await fetchChecklistData();

      // Other API calls or data fetching can also be placed here
    } catch (error) {
      console.error("Error fetching user data:", error);
    }
  };

  useEffect(() => {
    getMeetingAndToken(meetingId);
    fetchRecipes();
    fetchChecklistData();
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <MainLayout
      sidebarContent={
        recipe ? (
          <>
            <h1>Checklist</h1>
            <ListGroup variant="flush">
              {recipe.checklist.map((item: string, index: number) => (
                <ListGroup.Item
                  key={index}
                  style={{ backgroundColor: "transparent" }}
                >
                  <input
                    type="checkbox"
                    checked={userChecklistData[index] === true}
                    onChange={() => handleCheckboxChange(index)}
                    style={{ marginRight: "10px" }}
                  />
                  {item} ({stepCounts[index] || 0} / {currentParticipantsCount})
                </ListGroup.Item>
              ))}
            </ListGroup>
          </>
        ) : (
          <p>Loading checklist...</p>
        )
      }
    >
      {recipe && recipe.title ? (
        <Header1 style={{ marginBottom: "20px" }}>{recipe.title}</Header1>
      ) : (
        <></>
      )}
      {meetingId !== null ? (
        <MeetingProvider
          config={{
            meetingId,
            micEnabled: true,
            webcamEnabled: true,
            name: username,
            mode: mode,
            debugMode: true, // TODO Sprint 2: turn off in production
          }}
          token={authToken}
        >
          <MeetingConsumer>
            {() => (
              <Container
                meetingId={meetingId}
                onMeetingLeave={onMeetingLeave}
              />
            )}
          </MeetingConsumer>
        </MeetingProvider>
      ) : (
        <>
          <p>
            <FontAwesomeIcon icon={faSpinner} spin={true} />
          </p>
          <p>
            <Link to="/sessions">Back to sessions overview</Link>
          </p>
        </>
      )}
    </MainLayout>
  );
};

export default SessionViewer;
