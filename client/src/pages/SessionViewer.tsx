import { useEffect, useState } from "react";
import MainLayout from "../components/Layout/MainLayout";
import { Header1 } from "../components/ui/Header";
import { getMyUser } from "../api/user.api";
import {
  MeetingProvider,
  MeetingConsumer,
} from "@videosdk.live/react-sdk";
import { authToken } from "../components/VideoCall/API";
import { Container } from "../components/VideoCall/Container";
import { Link, useNavigate, useParams } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {faSpinner} from "@fortawesome/free-solid-svg-icons";
import { getSessionCredentialsAPI } from "../api/app.api";

const SessionViewer = () => {
  const { sessionID } = useParams();
  const navigate = useNavigate();
  
  // States for VideoCall
  const [meetingId, setMeetingId] = useState<string|null>(null);
  const onMeetingLeave = () => { setMeetingId(null); navigate("/sessions") };
  const [mode, setMode] = useState<"CONFERENCE"|"VIEWER">("CONFERENCE");

  // Layout States
  const [dishName, setDishName] = useState<string>('');
  const [username, setUsername] = useState<string>('');

  const getMeetingAndToken = async (id: string|null) => {
    // API-Call
    try {
      const res = await getSessionCredentialsAPI(Number(sessionID));
      console.log(res);
      setDishName(res.dishName);

      // Set Meeting ID
      const meetingId = id === null ? res.roomId : id;
      setMeetingId(meetingId);

      // Set Mode (CONFERENCE if Session owner, VIEWER if not)
      const user = await getMyUser();
      setUsername(user.username);

      if(res.hostId === user.id) {
        setMode("CONFERENCE");
      } else  {
        setMode("VIEWER");
      }

    } catch(e) {
      alert("Error while loading the session. Please try again.");
      navigate('/sessions');
    }
  };

  useEffect(() => {
    getMeetingAndToken(meetingId);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <MainLayout>
      <Header1 style={{marginBottom: '20px'}}>{ dishName }</Header1>
      { meetingId !== null ? (
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
              <Container meetingId={meetingId} onMeetingLeave={onMeetingLeave} />
            )}
          </MeetingConsumer>
        </MeetingProvider>
      ) : (
        <>
          <p><FontAwesomeIcon icon={ faSpinner } spin={true} /></p>
          <p><Link to="/sessions">Back to sessions overview</Link></p>
        </>
      )}
    </MainLayout>
  )
};

export default SessionViewer;