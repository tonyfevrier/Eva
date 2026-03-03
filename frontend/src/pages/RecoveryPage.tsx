import React, { useState, type Dispatch, type SetStateAction } from "react";
import { Input } from "../components/Input";
import { Button } from "../components/Button";
import { Goto } from "../components/Goto";

export function RecoveryPage(){
    const [mailSent, setMailSent] = useState<Boolean>(false);
    const [mailValue, setMailValue] = useState<string>("");
    const [fetchError, setFetchError] = useState<Error|null>(null);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setMailValue(e.target.value);
    }
    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (mailValue === ""){
            setFetchError(new Error("Vous devez remplir le champ"));
            return;
        }
        sendPostRequest(mailValue, setFetchError, setMailSent);
    }

    if (!mailSent ){
        return  <form action="" onSubmit={handleSubmit}>
                    <Input title="Veuillez entrer votre email" placeholder="email" name="email" value={mailValue} onChange={handleChange} type="text" disabled={false}/>
                    <Button>Envoyer un mail à l'adresse indiquée</Button>
                    {fetchError?.message && <p>{fetchError?.message}</p> }
                </form>
    }
    return <Goto href="/login" label="Un courriel vous a été envoyé, veuillez cliquer sur le lien présent dans ce courriel." buttonLabel="Revenir à la page de login"/>
}

async function sendPostRequest(mailValue:string, setFetchError:Dispatch<SetStateAction<Error|null>>, setMailSent:Dispatch<SetStateAction<Boolean>>){    
    const response = await fetch("http://localhost:9000/auth/resetMail", {
            method: "POST",
            headers:{
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({mail: mailValue})})
            .catch(error => {
                setFetchError(new Error(error.getMessage()))
                throw error;
        });
    if (response.ok){
        setMailSent(true);    
    } else {
        setFetchError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
    return response
}