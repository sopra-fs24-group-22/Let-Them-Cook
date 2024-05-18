import Layout from "../components/Layout/MainLayout";
import { useEffect, useState } from "react";
import { Input, Label } from "../components/ui/Input";
import { ENV } from "../env";
import { eMailIsValid } from "../helpers/eMailIsValid";
import { PrimaryButton } from "../components/ui/Button";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner } from "@fortawesome/free-solid-svg-icons";
import { putUserMeAPI } from "../api/app.api";
import { useSelector } from "react-redux";
import { State } from "../features";

const UserPage = () => {
  const { user } = useSelector((state: State) => state.app);
  const [firstname, setFirstname] = useState<string>("");
  const [lastname, setLastname] = useState<string>("");
  const [username, setUsername] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [emailIsValid, setEmailIsValid] = useState<boolean>(true);

  const userdataIsValid = () =>
    firstname && lastname && username && email && emailIsValid;

  const [userdataIsLoading, setUserdataIsLoading] = useState<boolean>(false);
  const [userdataSaved, setUserdataSaved] = useState<boolean>(false);

  const initFetch = async () => {
    try {
      setFirstname(user.firstname);
      setLastname(user.lastname);
      setUsername(user.username);
      setEmail(user.email);
    } catch (e) {
      alert("Error while fetching the data. Please reload the page.");
    }
  };

  const saveUserdata = async () => {
    setUserdataIsLoading(true);
    try {
      await putUserMeAPI({ firstname, lastname, username, email });
      setUserdataSaved(true);
      setTimeout(() => {
        setUserdataSaved(false);
      }, 5000);
    } catch (e) {
      alert("Error while saving the userdata. Please try again.");
    }
    setUserdataIsLoading(false);
  };

  const [newPassword, setNewPassword] = useState<string>("");
  const [repeatNewPassword, setRepeatNewPassword] = useState<string>("");

  const passwordIsValid = () =>
    newPassword === repeatNewPassword && newPassword.length > 0;

  const [passwordIsLoading, setPasswordIsLoading] = useState<boolean>(false);
  const [passwordIsSaved, setPasswordIsSaved] = useState<boolean>(false);

  const saveNewPassword = async () => {
    setPasswordIsLoading(true);
    try {
      await putUserMeAPI({ password: newPassword });
      setPasswordIsSaved(true);
      setNewPassword("");
      setRepeatNewPassword("");
      setTimeout(() => {
        setPasswordIsSaved(false);
      }, 5000);
    } catch (e) {
      alert("Error while saving the password. Please try again.");
    }
    setPasswordIsLoading(false);
  };

  useEffect(() => {
    initFetch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <Layout>
      <h1>Change user settings</h1>

      <Label htmlFor="firstname" style={{ margin: "15px 0 5px 0" }}>
        First name
      </Label>
      <Input
        id="firstname"
        type="text"
        maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
        style={{ margin: "0", width: "250px" }}
        value={firstname ? firstname : ""}
        onChange={(e) => setFirstname(e.target.value)}
      />

      <Label htmlFor="lastname" style={{ margin: "15px 0 5px 0" }}>
        Last name
      </Label>
      <Input
        id="lastname"
        type="text"
        maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
        style={{ margin: "0", width: "250px" }}
        value={lastname ? lastname : ""}
        onChange={(e) => setLastname(e.target.value)}
      />

      <Label htmlFor="username" style={{ margin: "15px 0 5px 0" }}>
        Username
      </Label>
      <Input
        id="username"
        type="text"
        maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
        style={{ margin: "0", width: "250px" }}
        value={username ? username : ""}
        onChange={(e) => setUsername(e.target.value)}
      />

      <Label htmlFor="email" style={{ margin: "15px 0 5px 0" }}>
        E-Mail
      </Label>
      <Input
        id="email"
        type="text"
        maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
        style={{
          margin: "0",
          width: "250px",
          borderColor: !emailIsValid ? "#f00" : "",
          color: !emailIsValid ? "#f00" : "#000",
        }}
        value={email ? email : ""}
        onChange={(e) => {
          setEmail(e.target.value);
          setEmailIsValid(eMailIsValid(email));
        }}
      />

      <br></br>

      {userdataSaved && (
        <p style={{ color: "green", margin: "10px 0" }}>
          Userdata saved successfully.
        </p>
      )}

      <PrimaryButton
        disabled={!userdataIsValid()}
        style={{ marginTop: userdataSaved ? "" : "20px" }}
        onClick={saveUserdata}
      >
        {!userdataIsLoading ? (
          "Save user data"
        ) : (
          <FontAwesomeIcon icon={faSpinner} spin={true} />
        )}
      </PrimaryButton>

      <h1 style={{ marginTop: "30px" }}>Change password</h1>

      <Label htmlFor="newPassword" style={{ margin: "15px 0 5px 0" }}>
        New password
      </Label>
      <Input
        id="newPassword"
        type="password"
        maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
        style={{ margin: "0", width: "250px" }}
        value={newPassword ? newPassword : ""}
        onChange={(e) => setNewPassword(e.target.value)}
      />

      <Label htmlFor="repeatNewPassword" style={{ margin: "15px 0 5px 0" }}>
        Repeat new password
      </Label>
      <Input
        id="repeatNewPassword"
        type="password"
        maxLength={ENV.MAX_TEXT_INPUT_LENGTH}
        style={{ margin: "0", width: "250px" }}
        value={repeatNewPassword ? repeatNewPassword : ""}
        onChange={(e) => setRepeatNewPassword(e.target.value)}
      />

      <br></br>

      {passwordIsSaved && (
        <p style={{ color: "green", margin: "10px 0" }}>
          Password saved successfully.
        </p>
      )}

      <PrimaryButton
        disabled={!passwordIsValid()}
        style={{ marginTop: passwordIsSaved ? "" : "20px" }}
        onClick={saveNewPassword}
      >
        {!passwordIsLoading ? (
          "Save password"
        ) : (
          <FontAwesomeIcon icon={faSpinner} spin={true} />
        )}
      </PrimaryButton>
    </Layout>
  );
};
export default UserPage;
