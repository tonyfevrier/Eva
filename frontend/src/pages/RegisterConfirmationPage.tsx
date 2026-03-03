import { useState, type Dispatch, type SetStateAction } from "react";
import { Button } from "../components/Button";
import { Goto } from "../components/Goto";

export function RegisterConfirmationPage(){
    const [registrationConfirmed, setRegistrationConfirmed] = useState<Boolean>(false);
    const [error, setError] = useState<Error|null>(null);
    const searchParams = new URLSearchParams(window.location.search);
    const body = JSON.stringify({token: searchParams.get("token")});
    
    const handleClick = () => {
        sendPostRequest(body, setError, setRegistrationConfirmed);
    }

    if (!registrationConfirmed){
        return <> 
            <p>Veuillez confirmer la création du compte</p>
            <Button onClick={handleClick}>Confirmer</Button>
            {error?.message && <p>{error?.message}</p>}
        </>
    }
    return <>
       <Goto href="/login" label="Votre inscription a bien été réalisée." buttonLabel="Revenir à la page de login"/>
    </>
}

async function sendPostRequest(body: string, setError: Dispatch<SetStateAction<Error|null>>, setRegistrationConfirmed: Dispatch<SetStateAction<Boolean>>) {
    const response = await fetch("http://localhost:9000/auth/confirm", {
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        method: "post",
        body: body
    })
    .catch(error => {
        setError(error);
        throw error;
    });

    if (response.ok){
        setRegistrationConfirmed(true);
    } else {
        setError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
}