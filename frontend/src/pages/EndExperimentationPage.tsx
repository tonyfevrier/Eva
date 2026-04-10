import React, { useState, type Dispatch, type SetStateAction } from "react";
import { Goto } from "../components/Goto";
import { useParams } from "react-router-dom";
import { Textarea } from "../components/Textarea";
import { Button } from "../components/Button";
import { exportFile } from "../utils/request/fileExport";

export function EndExperimentationPage(){
    const [error, setError] = useState<Error|null>(null);
    const {id} = useParams();
    const [interpretation, setInterpretation] = useState<string>("");
    
    const handleImport = () => {
            const fileInput = document.createElement("input");
            fileInput.type = "file";
            fileInput.accept = ".xls,.xlsx,.ods";

            fileInput.onchange = async () => {
                const selectedFile = fileInput.files?.[0];

                if (!selectedFile){
                    return;
                }

                setError(null);
                sendImportRequest(selectedFile, id, setError);
            }

            fileInput.click();
        }

    const handleInterpretation = (e: React.MouseEvent<HTMLInputElement>) => {
        const body = JSON.stringify({"interpretation": interpretation});
        sendInterpretationRequest(id, body, setError, setInterpretation);
    }

    const handlePdf = () => {
        generatePdf(id, setError);
    }

    const handleEnd = () => {
        endExperimentation(id, setError);
    }
    
    return <>  
                <h2>Ajouter les données de l'expérimentation</h2>
                <Goto label="Importer le fichier de données" buttonLabel="Importer" onClick={handleImport}/>
                <Textarea title="Dans cet encart, vous pouvez interpréter vos données." value={interpretation} onChange={(e) => setInterpretation(e.target.value)}></Textarea>
                <Button onClick={handleInterpretation}>Soumettre l'interprétation des données</Button>
                {error?.message && <p>{error?.message}</p>}
                <Goto label="Vous pouvez générer le pdf récapitulant votre expérimentation avec ou sans interprétation de données." buttonLabel="Générer le pdf" onClick={handlePdf}/>
                <Goto label="Vous pouvez marquer l'expérimentation comme terminée. Cela permettra à un utilisateur de télécharger le pdf récapitulant les informations, les résultats et leur interprétation." buttonLabel="Terminer" onClick={handleEnd}/>
           </>
}


async function sendImportRequest(file: File, id: string|undefined, setError: Dispatch<SetStateAction<Error|null>>){
    const supportedExtensions = ["xls", "xlsx", "ods"];
    const extension = file.name.split(".").pop()?.toLowerCase();

    if (!extension || !supportedExtensions.includes(extension)){
        setError(new Error("Le fichier doit être au format .xls, .xlsx ou .ods"));
        return;
    }

    const formData = new FormData();
    formData.append("file", file);
    if (id !== undefined){
        formData.append("id", id);
    }

    const response = await fetch(`http://localhost:9000/file/import`, {
            method: "post",
            headers: {
                'Accept': 'application/json',
            },
            body: formData,
            credentials: "include"
        })
        .catch(requestError => {
            setError(requestError);
            throw requestError;
        });

    if (response.ok){
        alert("Fichier envoyé avec succès!");
    } else {
        setError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
}

async function sendInterpretationRequest(id: string|undefined, body: string, setError: Dispatch<SetStateAction<Error|null>>, setInterpretation: Dispatch<SetStateAction<string>>){
    const response = await fetch(`http://localhost:9000/expe/interpret/${id}`, {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: body,
            method: "post",
            credentials: "include"
        }).catch(requestError => {
            setError(requestError);
            throw requestError;
        });

    if (response.ok){
        alert("Requête bien envoyée au serveur")
        setInterpretation("");
    }
}

async function generatePdf(id: string|undefined, setError: Dispatch<SetStateAction<Error|null>>){
    const response = await fetch(`http://localhost:9000/pdf/generate/${id}`, {
        headers: {
            'Accept': 'application/json'
        },
        method: "get",
        credentials: "include"
    }).catch(requestError => {
        setError(requestError);
        throw requestError
    });

    if (response.ok){
        exportFile(response, "pdf", "experimentation_summary");
    }
}

async function endExperimentation(id: string|undefined, setError: Dispatch<SetStateAction<Error|null>>){
    const response = await fetch(`http://localhost:9000/expe/endExpe/${id}`, {
        headers: {
            'Accept': 'application/json'
        },
        method: "get",
        credentials: "include"
    }).catch(requestError => {
        setError(requestError);
        throw requestError
    });

    if (response.ok){
        alert("L'expérimentation est considérée comme terminée");
    }
}