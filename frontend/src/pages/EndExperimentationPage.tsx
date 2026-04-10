import React, { useState, type Dispatch, type SetStateAction } from "react";
import { Goto } from "../components/Goto";
import { useParams } from "react-router-dom";
import { Textarea } from "../components/Textarea";
import { Button } from "../components/Button";

export function EndExperimentationPage(){
    const [sendError, setSendError] = useState<Error|null>(null);
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

                setSendError(null);
                sendImportRequest(selectedFile, id, setSendError);
            }

            fileInput.click();
        }

    const handleInterpretation = (e: React.MouseEvent<HTMLInputElement>) => {
        /*envoyer  l'interp à spring, si la requête réussit, faire apparaître encart de génération de pdf*/
    }
    
    return <>  
                <h2>Ajouter les données de l'expérimentation</h2>
                <Goto label="Importer le fichier de données" buttonLabel="Importer" onClick={handleImport}/>
                <Textarea title="Dans cet encart, vous pouvez interpréter vos données." value={interpretation} onChange={(e) => setInterpretation(e.target.value)}></Textarea>
                <Button onClick={handleInterpretation}>Soumettre l'interprétation des données</Button>
                {sendError?.message && <p>{sendError?.message}</p>}
                <Goto label="Vous pouvez générer le pdf récapitulant votre expérimentation avec ou sans interprétation de données." buttonLabel="Générer le pdf"/>
           </>
}


async function sendImportRequest(file: File, id: string|undefined, setSendError: Dispatch<SetStateAction<Error|null>>){
    const supportedExtensions = ["xls", "xlsx", "ods"];
    const extension = file.name.split(".").pop()?.toLowerCase();

    if (!extension || !supportedExtensions.includes(extension)){
        setSendError(new Error("Le fichier doit être au format .xls, .xlsx ou .ods"));
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
            setSendError(requestError);
            throw requestError;
        });

    if (response.ok){
        alert("Fichier envoyé avec succès!");
    } else {
        setSendError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
}