import React, { useState, type Dispatch, type SetStateAction } from "react";
import { Goto } from "../components/Goto";
import { useParams } from "react-router-dom";
import { Textarea } from "../components/Textarea";
import { Button } from "../components/Button";
import { exportFile } from "../utils/request/fileExport";
import { ModalList } from "../components/ModalList";
import { Checkbox } from "../components/CheckBox";

export function EndExperimentationPage(){
    const [isFileModalOpen, setIsFileModalOpen] = useState<boolean>(false);
    const [importType, setImportType] = useState<string>("xls");
    const [error, setError] = useState<Error|null>(null);
    const {id} = useParams();
    const [interpretation, setInterpretation] = useState<string>("");

    const handleImportFile = () => {
        const fileInput = document.createElement("input");
        fileInput.type = "file";
        fileInput.accept = importType == "xls" ?".xls,.xlsx,.ods": ".pdf";

        fileInput.onchange = async () => {
            const selectedFile = fileInput.files?.[0];

            if (!selectedFile){
                return;
            }

            setError(null);
            sendImportRequest(selectedFile, id, setError, importType);
        }

        fileInput.click();
    }

    const handleImportXls = () => {
        setImportType("xls");
        handleImportFile();
    }

    const handleInterpretation = () => {
        const body = JSON.stringify({"interpretation": interpretation});
        sendInterpretationRequest(id, body, setError, setInterpretation);
    }

    const handlePdf = () => {
        generatePdf(id, setError);
    }

    const handleEnd = () => {
        endExperimentation(id, setError);
    }

    const handleDisplayModal = (e: React.MouseEvent<HTMLButtonElement>) => {
        setIsFileModalOpen(true);
        setImportType(e.currentTarget.id);
    }
    
    return <>  
                <h2>Ajouter les données de l'expérimentation</h2>
                <Goto id="xls" variant="export" label="Importer le fichier de données brutes" buttonLabel="Importer" onClick={handleImportXls}/>
                <Textarea title="Dans cet encart, vous pouvez interpréter vos données." value={interpretation} onChange={(e) => setInterpretation(e.target.value)}></Textarea>
                <Button onClick={handleInterpretation}>Soumettre l'interprétation des données</Button>
                <Goto id="test" variant="export" label="Importez au format pdf les tests administrés aux étudiants." buttonLabel="Importer" onClick={handleDisplayModal}/>
                <Goto id="questionnaire" variant="export" label="Importez au format pdf les questionnaires de recherche administrés aux étudiants." buttonLabel="Importer" onClick={handleDisplayModal}/>
                {error?.message && <p>{error?.message}</p>}
                <Goto variant="export" label="Vous pouvez générer le pdf récapitulant votre expérimentation avec ou sans interprétation de données." buttonLabel="Générer le pdf" onClick={handlePdf}/>
                <Goto variant="export" label="Vous pouvez marquer l'expérimentation comme terminée. Tout utilisateur pourra télécharger le pdf généré." buttonLabel="Terminer" onClick={handleEnd}/>
                {isFileModalOpen && 
                    <ModalList title="Ajouter ou supprimer des tests" onClose={() => {setIsFileModalOpen(false)}}>
                        <Checkbox title="" options={[""]}/>
                        <Button onClick={handleImportFile}>Ajouter un fichier</Button>
                        <Button onClick={()=>{}}>Supprimer les fichiers sélectionnés</Button>
                    </ModalList>
                }
           </>
}


async function sendImportRequest(file: File, id: string|undefined, setError: Dispatch<SetStateAction<Error|null>>, importType: string){
    const supportedExtensions = importType == "xls" ? "xls, xlsx ou ods" : "pdf";
    const extension = file.name.split(".").pop()?.toLowerCase();

    if (!extension || !supportedExtensions.includes(extension)){
        setError(new Error(`Le fichier doit être au format ${supportedExtensions}`));
        return;
    }

    const formData = new FormData();
    formData.append("file", file);
    formData.append("importType", importType);
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

export async function generatePdf(id: string|undefined, setError: Dispatch<SetStateAction<Error|null>>){
    const response = await fetch(`http://localhost:9000/pdf/generate/${id}`, {
        headers: {
            'Accept': 'application/json'
        },
        method: "get",
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