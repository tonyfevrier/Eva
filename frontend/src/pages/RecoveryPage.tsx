import React, { useState, type Dispatch, type SetStateAction } from "react";
import { Input } from "../components/Input";
import { Button } from "../components/Button";
import { useNavigate, type NavigateFunction } from "react-router-dom";

export function RecoveryPage(){
    const [mailValue, setMailValue] = useState<string>("");
    const [fetchError, setFetchError] = useState<Error|null>(null);
    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setMailValue(e.target.value);
    }
    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (mailValue === ""){
            setFetchError(new Error("Vous devez remplir le champ"));
            return;
        }
        sendPostRequest(mailValue, setFetchError, navigate);
    }

    return  <form action="" onSubmit={handleSubmit}>
                <Input title="Veuillez entrer votre email" placeholder="email" name="email" value={mailValue} onChange={handleChange} type="text" disabled={false}/>
                <Button>Envoyer un mail à l'adresse indiquée</Button>
                {fetchError?.message && <p>{fetchError?.message}</p> }
            </form>
}

async function sendPostRequest(mailValue:string, setFetchError:Dispatch<SetStateAction<Error|null>>, navigate: NavigateFunction){    
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
        navigate("/seeMail");    
    } else {
        setFetchError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
    return response
}