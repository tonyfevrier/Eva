import { useRef, useState, type FormEvent } from "react";
import type { FormBoolean, SendingStatus } from "../types/types";
 


export function RegisterPage(){
    const registerForm = useRef<HTMLFormElement>(null);
    const [formState, setFormState] = useState<FormBoolean>({
        isFirstnameEmpty : false,
        isLastnameEmpty : false,
        isUsernameEmpty : false,
        isPasswordEmpty : false, 
    });

    const [sendingState, setSendingState] = useState<SendingStatus<any>>({
        data: null,
        error: null
    })

    /*const InputsToStateMapping = {"firstname": formState.isFirstnameEmpty, 
                          "lastname": formState.isLastnameEmpty,
                          "mail": formState.isUsernameEmpty,
                          "password": formState.isPasswordEmpty};*/

    const handleClick = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (registerForm.current !== null){
            const formData = new FormData(registerForm.current); 
            const allInputsAreFilled = !(formData.get("firstname") === "" || formData.get("lastname") === "" || formData.get("mail") === "" || formData.get("password") === "");

            if (allInputsAreFilled){
                sendFormData(formData, setSendingState);
            } else {
                displayEmptyInputs(formData, setFormState);
            }
        }
    }

    if (sendingState.data !== null){
        return <>
                    <h1> Votre inscription a bien été réalisée.</h1>
                    <a href="/">Retournez à la page d'accueil.</a>
               </>
    }

    return <>
                <h1> Inscription </h1>
                <form ref={registerForm} onSubmit={handleClick}>
                    <input type="text" placeholder="firstname" name="firstname"/>
                    {formState.isFirstnameEmpty && <p>Il faut remplir ce champ</p>}
                    <input type="text" placeholder="lastname" name="lastname"/>
                    {formState.isLastnameEmpty && <p>Il faut remplir ce champ</p>}
                    <input type="text" placeholder="mail" name="mail"/>
                    {formState.isUsernameEmpty && <p>Il faut remplir ce champ</p>}
                    <input type="password" placeholder="mot de passe" name="password"/>
                    {formState.isPasswordEmpty && <p>Il faut remplir ce champ</p>}
                    <input type="submit" />
                </form>
                {sendingState.error !== null && <p> {sendingState.error} </p>}
                <div>
                    <p>Vous souhaitez vous connecter?</p>
                    <a href="/login"> Connectez-vous ici.</a>
                </div>  
           </>;
}

async function sendFormData(formData:FormData, setSendingState:React.Dispatch<React.SetStateAction<SendingStatus<any>>>){
    /* Envoie les données du formulaire et modifie data ou error dans l'état caractérisant le formulaire */
    const response = await fetchData(formData);         
    const text = await response.text();
               
    if (response.ok) {
        const data = JSON.parse(text);
        setSendingState(prev => ({...prev, data: data}));
    } else {
        setSendingState(prev => ({...prev, error: text}))
    }
}

async function fetchData(formData: FormData){
    const response = await fetch("http://localhost:9000/api/register", {
        method : "POST",
        body : JSON.stringify({
            firstname : formData.get("firstname"),
            lastname : formData.get("lastname"),
            mail : formData.get("mail"),
            password : formData.get("password"),
        }),
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        }
    });
    return response;
}


function displayEmptyInputs(formData:FormData, setFormState:React.Dispatch<React.SetStateAction<FormBoolean>>){
    setFormState({isFirstnameEmpty : formData.get("firstname") === "",
                isLastnameEmpty : formData.get("lastname") === "",
                isUsernameEmpty : formData.get("mail") === "",
                isPasswordEmpty : formData.get("password") === ""});
}