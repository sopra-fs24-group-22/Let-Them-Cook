import { useState } from 'react';
import { useSelector } from "react-redux";
import { State } from "../features";
import Layout from "../components/Layout/MainLayout";
import { PrimaryButton, SecondaryButton } from "../components/ui/Button";
import { Label, Input, Select, Option } from "../components/ui/Input";
import Modal from 'react-bootstrap/Modal';
import { getAllRecipesAPI, postSessionAPI } from "../api/app.api";

const SessionViewerPage = () => {
    const appState = useSelector((state: State) => state.app);
    return (
        <Layout>
            In Session view
        </Layout>
    );
};
export default SessionViewerPage;
