import { useMeeting } from "@videosdk.live/react-sdk";
import { PrimaryButton } from "../ui/Button";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Col, Container, Row } from "react-bootstrap";
import { faCircle, faSpinner } from "@fortawesome/free-solid-svg-icons";

const SpeakerControls = () => {
  const { leave, startHls, stopHls, hlsState } = useMeeting();

  return (
    <>
    <Container><Row>
      <Col xs={10}>
        <PrimaryButton onClick={() => { stopHls(); leave(); }} style={{marginRight: '5px'}}>Leave</PrimaryButton>
        { /* Stream is running */ }
        { hlsState === "HLS_PLAYABLE" ? 
          <PrimaryButton onClick={() => stopHls()}>Stop stream</PrimaryButton> : <></> }
        { /* Stream is turned off */ }
        { hlsState === "HLS_STOPPED" ? 
          <PrimaryButton
            onClick={() => {
              startHls({
                layout: {
                  type: "SPOTLIGHT",
                  priority: "PIN",
                  gridSize: 20,
                },
                theme: "LIGHT",
                mode: "video-and-audio",
                quality: "high",
                orientation: "landscape",
              });
            }}
          >Start stream</PrimaryButton> : <></> }
        { /* Stream is starting up or shutting down */ }
        { hlsState !== "HLS_PLAYABLE" && hlsState !== "HLS_STOPPED" ?
          <PrimaryButton disabled={true}>
            <FontAwesomeIcon icon={ faSpinner } spin={true} />
          </PrimaryButton> : <></> }
      </Col>
      <Col xs={2}>
      Status:&nbsp;
      { hlsState === "HLS_PLAYABLE" ? <FontAwesomeIcon icon={faCircle} color="#28a745" title="OK" /> : <></> }
      { hlsState === "HLS_STOPPED" ? <FontAwesomeIcon icon={faCircle} color="#dc3545" title="Not connected" /> : <></> }
      { hlsState !== "HLS_PLAYABLE" && hlsState !== "HLS_STOPPED" ?
        <FontAwesomeIcon icon={faCircle} color="#ffc107" title="Loading..." /> : <></> }
      </Col>
    </Row></Container>
    </>
  );
}

export { SpeakerControls };