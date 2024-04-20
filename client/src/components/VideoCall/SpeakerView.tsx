import { Constants, useMeeting } from "@videosdk.live/react-sdk";
import { useMemo } from "react";
import { ParticipantView } from "./ParticipantView";
import { Controls } from "./Controls";

const SpeakerView = () => {
  //Get the participants and HLS State from useMeeting
  const { participants, hlsState } = useMeeting();

  //Filtering the host/speakers from all the participants
  const speakers = useMemo(() => {
    const speakerParticipants = [...participants.values()].filter(
      (participant) => {
        return participant.mode == Constants.modes.CONFERENCE;
      }
    );
    return speakerParticipants;
  }, [participants]);
  return (
    <div>
      {/* Controls for the meeting */}
      <Controls />

      {/* Rendring all the HOST participants */}
      {speakers.map((participant) => (
        <ParticipantView participantId={participant.id} key={participant.id} />
      ))}
    </div>
  );
}

export { SpeakerView };