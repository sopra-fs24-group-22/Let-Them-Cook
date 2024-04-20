import { useMeeting } from "@videosdk.live/react-sdk";
import { PrimaryButton } from "../ui/Button";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner, faCircle } from "@fortawesome/free-solid-svg-icons";
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

const Controls = () => {
  const { leave, startHls, stopHls, hlsState } = useMeeting();

  return (
    <>
    <Container><Row>
      <Col xs={11}>
        <PrimaryButton onClick={() => { stopHls(); leave() }} style={{marginRight: '5px'}}>Leave</PrimaryButton>
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
      <Col xs={1}>
      Status:&nbsp;
      { hlsState === "HLS_PLAYABLE" ? <FontAwesomeIcon icon={faCircle} color="#28a745" /> : <></> }
      { hlsState === "HLS_STOPPED" ? <FontAwesomeIcon icon={faCircle} color="#dc3545" /> : <></> }
      { hlsState !== "HLS_PLAYABLE" && hlsState !== "HLS_STOPPED" ?
        <FontAwesomeIcon icon={faCircle} color="#ffc107" /> : <></> }
      </Col>
    </Row></Container>
    </>
  );
}

export { Controls };