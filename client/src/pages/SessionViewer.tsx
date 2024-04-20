import {
  MeetingProvider,
} from "@videosdk.live/react-sdk";
import MainLayout from "../components/Layout/MainLayout";
import { MeetingView } from "../components/VideoCall/MeetingView";

const SessionViewer = () => {
  return (
    <MainLayout>
      <MeetingProvider
        config={{
          meetingId: "g46m-k33x-0yqe",
          micEnabled: true,
          webcamEnabled: true,
          name: "Let them cook",
          debugMode: true, //! DEV
        }}
        token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiI1NGJmOTg2Ny0wMjZjLTQ1MGEtYmZkYy1hYzNlZTBiNmJmN2QiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTcxMzYxMjg1MSwiZXhwIjoxNzEzNjk5MjUxfQ.22WcFqaarjATdFYsB6BxeDpV747giYUtN_Y4cbv3cu0"
      >
        <MeetingView />
      </MeetingProvider>
    </MainLayout>
  )
};

export default SessionViewer;