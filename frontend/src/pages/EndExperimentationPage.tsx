import React, { useState, type Dispatch, type SetStateAction } from "react";
import { Goto } from "../components/Goto";
import { useParams } from "react-router-dom";
import { Textarea } from "../components/Textarea";
import { Button } from "../components/Button";
import { exportFile } from "../utils/request/fileExport";
import { ModalList } from "../components/ModalList";
import { LinkCheckbox } from "../components/LinkCheckBox";
import { Spinner } from "../components/Spinner";

export function EndExperimentationPage(){
    const [isFileModalOpen, setIsFileModalOpen] = useState<boolean>(false);
    const [importType, setImportType] = useState<string>("xls");
    const [fileNames, setFileNames] = useState<Array<string>>([]);
    const [fileNamesToDelete, setFileNamesToDelete] = useState<Array<string>>([]);
    const [error, setError] = useState<Error|null>(null);
    const [loading, setLoading] = useState<boolean>(false);
    const {id} = useParams();
    const [interpretation, setInterpretation] = useState<string>("");

    const handleImportFile = (type: string = importType) => {
        const fileInput = document.createElement("input");
        fileInput.type = "file";
        fileInput.accept = type == "xls" ?".xls,.xlsx,.ods": ".pdf";

        fileInput.onchange = async () => {
            const selectedFile = fileInput.files?.[0];
            if (!selectedFile){
                return;
            }

            setError(null);
            sendImportRequest(selectedFile, id, setError, type);
            setIsFileModalOpen(false);
        }

        fileInput.click();
    }

    const handleImportXls = () => {
        const nextImportType = "xls";
        setImportType(nextImportType);
        handleImportFile(nextImportType);
    }

    const handleInterpretation = () => {
        const body = JSON.stringify({"interpretation": interpretation});
        sendInterpretationRequest(id, body, setError, setInterpretation);
    }

    const handlePdf = () => {
        setLoading(true); 
        generatePdf(id, setError, setLoading);
    }

    const handleEnd = () => {
        endExperimentation(id, setError);
    }

    const handleDisplayFileModal = (e: React.MouseEvent<HTMLButtonElement>) => {
        /*on change l'importType pour préparer l'import en cas d'ajout d'un fichier,
        on affiche également les fichiers pdf du type de l'import */
        const nextImportType = e.currentTarget.id;
        setImportType(nextImportType);
        getRegisteredFileNames(nextImportType, id, setError, setFileNames, setIsFileModalOpen);
    }

    const modifyFileNamesToDelete = (e: React.ChangeEvent<HTMLInputElement>) => {
        const fileName = e.currentTarget.value;
        setFileNamesToDelete((previousFileNamesToDelete) => {
            if (previousFileNamesToDelete.includes(fileName)) {
                return previousFileNamesToDelete.filter((name) => name !== fileName);
            }

            return [...previousFileNamesToDelete, fileName];
        });
    }

    const handleDeleteFiles = () => {
        sendDeleteRequest(fileNamesToDelete, setError);
        handleCloseModal();
    }

    const handleCloseModal = () => {
        setIsFileModalOpen(false)
        setFileNamesToDelete([]);
    }

    const handleDownloadRegisteredFile = (e:React.MouseEvent<HTMLButtonElement>) => {
        sendExportRequest(e.currentTarget.value, setError);
        handleCloseModal();
    }

    return <>  
                <h2>Ajouter les données de l'expérimentation</h2>
                <Goto id="xls" variant="export" label="Importer le fichier de données brutes" buttonLabel="Importer" onClick={handleImportXls}/>
                <Textarea title="Dans cet encart, vous pouvez interpréter vos données." value={interpretation} onChange={(e) => setInterpretation(e.target.value)}></Textarea>
                <Button onClick={handleInterpretation}>Soumettre l'interprétation des données</Button>
                <Goto id="test" variant="export" label="Importez au format pdf les tests administrés aux étudiants." buttonLabel="Importer" onClick={handleDisplayFileModal}/>
                <Goto id="questionnaire" variant="export" label="Importez au format pdf les questionnaires de recherche administrés aux étudiants." buttonLabel="Importer" onClick={handleDisplayFileModal}/>
                {error?.message && <p>{error?.message}</p>}
                <Goto variant="export" label="Vous pouvez générer le pdf récapitulant votre expérimentation avec ou sans interprétation de données." buttonLabel="Générer le pdf" onClick={handlePdf}/>
                <Goto variant="export" label="Vous pouvez marquer l'expérimentation comme terminée. Tout utilisateur pourra télécharger le pdf généré." buttonLabel="Terminer" onClick={handleEnd}/>
                {isFileModalOpen && 
                    <ModalList title="Ajouter ou supprimer des tests" onClose={handleCloseModal}>
                        <LinkCheckbox title={fileNames.length > 0 ? "Fichiers enregistrés": ""} options={fileNames} onChange={modifyFileNamesToDelete} onButtonClick={handleDownloadRegisteredFile}/>
                        <Button onClick={() => handleImportFile()} style={{margin: '.5em'}}>Ajouter un fichier</Button>
                        <Button onClick={handleDeleteFiles} style={{margin: '.5em'}}>Supprimer les fichiers sélectionnés</Button>
                    </ModalList>
                }
                {loading && <Spinner/>}
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

export async function generatePdf(id: string|undefined, setError: Dispatch<SetStateAction<Error|null>>, setLoading: Dispatch<SetStateAction<boolean>>){
    const response = await fetch(`http://localhost:9000/pdf/generate/${id}`, {
        headers: {
            'Accept': 'application/json'
        },
        method: "get",
    }).catch(requestError => {
        setError(requestError);
        setLoading(false);
        throw requestError
    });

    if (response.ok){
        exportFile(response, `experimentation_summary.pdf`);
    } else {
        const errorMessage = await response.text();
        alert(errorMessage);
    }
    setLoading(false);
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

async function getRegisteredFileNames(fileType: string, id: string|undefined, setError: Dispatch<SetStateAction<Error|null>>, setFileNames: Dispatch<SetStateAction<Array<string>>>, setIsFileModalOpen: Dispatch<SetStateAction<boolean>>){
    const formData= new FormData();
    formData.append("importType", fileType);
    
    const response = await fetch(`http://localhost:9000/file/getFileNames/${id}`, {
        headers: {
            'Accept': 'application/json'
        },
        method: "post",
        body: formData,
        credentials: "include"
    }).catch(requestError => {
        setError(requestError);
        throw requestError
    });

    if (response.ok){
        const data = JSON.parse(await response.text());
        setFileNames(data.fileNames);
        setIsFileModalOpen(true);
    } else {
        setError(new Error(`Erreur ${response.status}: ${response.statusText}`))
    }
}

async function sendDeleteRequest(fileNames: Array<string>, setError: Dispatch<SetStateAction<Error|null>>){
    const formData = new FormData();
    fileNames.forEach(fileName => formData.append("fileNames", fileName));
    
    const response = await fetch(`http://localhost:9000/file/delete`, {
        headers: {
            'Accept': 'application/json',
        },
        method: "post",
        body: formData,
        credentials: "include"
    }).catch(requestError => {
        setError(requestError);
        throw requestError
    });

    if (response.ok){ 
        alert("Fichiers supprimés avec succès")
    } else {
        setError(new Error(`Erreur ${response.status}: ${response.statusText}`))
    }
}

async function sendExportRequest(fileName: string, setError: Dispatch<SetStateAction<Error|null>>){
    const formData = new FormData();
    formData.append("entry", fileName);
    formData.append("exportType", "pdf");
    
    const response = await fetch(`http://localhost:9000/file/export`, {
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

    if (!response.ok){
        setError(new Error(`Erreur ${response.status}: ${response.statusText}`));
        return;
    }
    exportFile(response, fileName);
}