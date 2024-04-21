import { Constants, useMeeting } from "@videosdk.live/react-sdk";
import { useEffect, useRef, useState } from "react";
import { SpeakerView } from "./SpeakerView";
import { ViewerView } from "./ViewerView";
import { PrimaryButton } from "../ui/Button";

const Container = (props: any) => {
  const [joined, setJoined] = useState<"JOINED"|"JOINING"|null>(null);
  //Get the method which will be used to join the meeting.
  const { join } = useMeeting();
  const mMeeting = useMeeting({
    //callback for when a meeting is joined successfully
    onMeetingJoined: () => {
      //Pin the local participant if he joins in CONFERENCE mode
      if (mMeetingRef.current.localParticipant.mode == "CONFERENCE") {
        mMeetingRef.current.localParticipant.pin("CAM");
      }
      setJoined("JOINED");
    },
    //callback for when a meeting is left
    onMeetingLeft: () => {
      props.onMeetingLeave();
    },
    //callback for when there is an error in a meeting
    onError: (error) => {
      alert(error.message);
    },
  });
  const joinMeeting = () => {
    setJoined("JOINING");
    join();
  };

  const mMeetingRef = useRef(mMeeting);
  useEffect(() => {
    mMeetingRef.current = mMeeting;
  }, [mMeeting]);

  return (
    <div className="container">
      {joined && joined == "JOINED" ? (
        mMeeting.localParticipant.mode == Constants.modes.CONFERENCE ? (
          <SpeakerView />
        ) : mMeeting.localParticipant.mode == Constants.modes.VIEWER ? (
          <ViewerView />
        ) : null
      ) : joined && joined == "JOINING" ? (
        <p>Joining the session...</p>
      ) : (
        <PrimaryButton onClick={joinMeeting}>Join the session</PrimaryButton>
      )}
    </div>
  );
}

export { Container };