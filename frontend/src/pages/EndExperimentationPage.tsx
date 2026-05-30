import React, { useState, type Dispatch, type SetStateAction } from "react";
import { Goto } from "../components/Goto";
import { useParams } from "react-router-dom";
import { Textarea } from "../components/Textarea";
import { Button } from "../components/Button";
import { exportFile } from "../utils/request/fileExport";
import { ModalList } from "../components/ModalList";
import { LinkCheckbox } from "../components/LinkCheckBox";
import { Spinner } from "../components/Spinner";
import styles from "./EndExperimentationPage.module.css";
import { Input } from "../components/Input";
import { useFetch } from "../hooks/useFetch";
import { apiFetch } from "../utils/apiFetch";
import { Alert } from "../components/Alert";



export function EndExperimentationPage(){
    const [isFileModalOpen, setIsFileModalOpen] = useState<boolean>(false);
    const [importType, setImportType] = useState<string>("xls");
    const [fileNames, setFileNames] = useState<Array<string>>([]);
    const [fileNamesToDelete, setFileNamesToDelete] = useState<Array<string>>([]);
    const [requestError, setRequestError] = useState<Error|null>(null);
    const [loadingPdf, setLoading] = useState<boolean>(false);
    const [expeWorked, setExpeWorked] = useState<boolean>(false);
    const {id} = useParams();
    const [interpretation, setInterpretation] = useState<string>("");
    const {data} = useFetch<Record<string, any>>(`/expe/get/${id}`);
    const [success, setSuccess] = useState<string>("");

    const handleImportFile = (type: string = importType) => {
        const fileInput = document.createElement("input");
        fileInput.type = "file";
        fileInput.accept = type == "xls" ?".xls,.xlsx,.ods": ".pdf";

        fileInput.onchange = async () => {
            const selectedFile = fileInput.files?.[0];
            if (!selectedFile){
                return;
            }

            setRequestError(null);
            sendImportRequest(selectedFile, id, setRequestError, type, setSuccess);
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
        const body = JSON.stringify({"interpretation": {"content": interpretation},
                                     "expeWorked": expeWorked});
        sendInterpretationRequest(id, body, setRequestError, setInterpretation, setSuccess);
    }

    const handlePdf = () => {
        setLoading(true); 
        generatePdf(id, setRequestError, setLoading);
    }

    const handleEnd = () => {
        endExperimentation(id, setRequestError, setSuccess);
    }

    const handleDisplayFileModal = (e: React.MouseEvent<HTMLButtonElement>) => {
        /*on change l'importType pour préparer l'import en cas d'ajout d'un fichier,
        on affiche également les fichiers pdf du type de l'import */
        const nextImportType = e.currentTarget.id;
        setImportType(nextImportType);
        getRegisteredFileNames(nextImportType, id, setRequestError, setFileNames, setIsFileModalOpen);
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
        sendDeleteRequest(fileNamesToDelete, setRequestError, setSuccess);
        handleCloseModal();
    }

    const handleCloseModal = () => {
        setIsFileModalOpen(false)
        setFileNamesToDelete([]);
    }

    const handleDownloadRegisteredFile = (e:React.MouseEvent<HTMLButtonElement>) => {
        sendExportRequest(e.currentTarget.value, setRequestError);
        handleCloseModal();
    }

    return <>  
                <h2>Ajouter les données de l'expérimentation</h2>
                <h4 className={styles.h4}>Import des tests administrés</h4>
                <Goto id="test" variant="export" label="Importez au format pdf les tests administrés aux étudiants." buttonLabel="Importer" onClick={handleDisplayFileModal}/>
                <Goto id="questionnaire" variant="export" label="Importez au format pdf les questionnaires de recherche administrés aux étudiants." buttonLabel="Importer" onClick={handleDisplayFileModal}/>
                <h4 className={styles.h4}>Import des résultats de l'expérimentation</h4>
                <Goto id="xls" variant="export" label="Importer le fichier de données brutes" buttonLabel="Importer" onClick={handleImportXls}/>
                <Textarea title="Dans cet encart, vous pouvez interpréter vos données." value={interpretation} onChange={(e) => setInterpretation(e.target.value)}></Textarea>
                <Input type="checkbox" title="Les résultats sont-ils significatifs" onChange={() => {setExpeWorked(!expeWorked)}}/>
                <Button onClick={handleInterpretation}>Soumettre les résultats</Button>
                <h4 className={styles.h4}>Génération du récapitulatif complet de l'expérimentation</h4>
                <Goto variant="export" label="Vous pouvez générer le pdf récapitulant votre expérimentation avec ou sans interprétation de données." buttonLabel="Générer le pdf" onClick={handlePdf}/>
                {data?.inProgress && <Goto variant="export" label="Vous pouvez marquer l'expérimentation comme terminée. Tout utilisateur pourra télécharger le pdf généré." buttonLabel="Terminer" onClick={handleEnd}/>}
                {isFileModalOpen && 
                    <ModalList title="Ajouter ou supprimer des tests" onClose={handleCloseModal}>
                        <LinkCheckbox title={fileNames.length > 0 ? "Fichiers enregistrés": ""} options={fileNames} onChange={modifyFileNamesToDelete} onButtonClick={handleDownloadRegisteredFile}/>
                        <Button onClick={() => handleImportFile()} style={{margin: '.5em'}}>Ajouter un fichier</Button>
                        <Button onClick={handleDeleteFiles} style={{margin: '.5em'}}>Supprimer les fichiers sélectionnés</Button>
                    </ModalList>
                }
                {loadingPdf && <Spinner/>}
                {requestError?.message && <Alert message={requestError?.message} onClose={() => setRequestError(null)}/>}
                {success !== "" && <Alert variant="success" title="Succès" message={success} onClose={() => setSuccess("")}/>}
           </>
}


async function sendImportRequest(file: File, id: string|undefined, setError: Dispatch<SetStateAction<Error|null>>, importType: string, setSuccess: Dispatch<SetStateAction<string>>){
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

    const response = await apiFetch(`/file/import`, {
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
        setSuccess("Fichier envoyé avec succès!");
    } else {
        setError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
}

async function sendInterpretationRequest(id: string|undefined, body: string, setError: Dispatch<SetStateAction<Error|null>>, setInterpretation: Dispatch<SetStateAction<string>>, setSuccess: Dispatch<SetStateAction<string>>){
    const response = await apiFetch(`/expe/interpret/${id}`, {
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
        setSuccess("Requête bien envoyée au serveur");
        setInterpretation("");
    }
}

export async function generatePdf(id: string|undefined, setError: Dispatch<SetStateAction<Error|null>>, setLoading: Dispatch<SetStateAction<boolean>>){
    const response = await apiFetch(`/pdf/generate/${id}`, {
        headers: {
            'Accept': 'application/json'
        },
        method: "get",
        credentials: "include"
    }).catch(requestError => {
        setError(requestError);
        setLoading(false);
        throw requestError
    });

    if (response.ok){
        exportFile(response, `experimentation_summary.pdf`);
    } else {
        const errorMessage = await response.text();
        setError(new Error(errorMessage));
    }
    setLoading(false);
}

async function endExperimentation(id: string|undefined, setError: Dispatch<SetStateAction<Error|null>>, setSuccess: Dispatch<SetStateAction<string>>){
    const response = await apiFetch(`/expe/endExpe/${id}`, {
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
        setSuccess("L'expérimentation est considérée comme terminée");
    } else {
        let errorMessage = `Erreur ${response.status}: ${await response.text()}`;
        setError(new Error(errorMessage));
    }
}

async function getRegisteredFileNames(fileType: string, id: string|undefined, setError: Dispatch<SetStateAction<Error|null>>, setFileNames: Dispatch<SetStateAction<Array<string>>>, setIsFileModalOpen: Dispatch<SetStateAction<boolean>>){
    const formData= new FormData();
    formData.append("importType", fileType);
    
    const response = await apiFetch(`/file/getFileNames/${id}`, {
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

async function sendDeleteRequest(fileNames: Array<string>, setError: Dispatch<SetStateAction<Error|null>>, setSuccess: Dispatch<SetStateAction<string>>){
    const formData = new FormData();
    fileNames.forEach(fileName => formData.append("fileNames", fileName));
    
    const response = await apiFetch(`/file/delete`, {
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
        setSuccess("Fichiers supprimés avec succès")
    } else {
        setError(new Error(`Erreur ${response.status}: ${response.statusText}`))
    }
}

async function sendExportRequest(fileName: string, setError: Dispatch<SetStateAction<Error|null>>){
    const formData = new FormData();
    formData.append("entry", fileName);
    formData.append("exportType", "pdf");
    
    const response = await apiFetch(`/file/export`, {
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