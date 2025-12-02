import { useRef, useState, type FormEvent } from "react";
import type { FormBoolean, SendingStatus } from "../types/types";
import { Form } from "../components/Form";
 


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

    const InputsToStateKeyMapping: Record<string, keyof FormBoolean> = {
                          "firstname": "isFirstnameEmpty", 
                          "lastname": "isLastnameEmpty",
                          "mail": "isUsernameEmpty",
                          "password": "isPasswordEmpty"};

    const InputsToStateMapping: Record<string, boolean> = {
                          "firstname": formState.isFirstnameEmpty, 
                          "lastname": formState.isLastnameEmpty,
                          "mail": formState.isUsernameEmpty,
                          "password": formState.isPasswordEmpty};

    const handleClick = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (registerForm.current !== null){
            const formData = new FormData(registerForm.current); 
            const formKeys = Array.from(formData.keys());
            const allInputsAreFilled = formKeys.every(key => formData.get(key) !== "");
            if (allInputsAreFilled){
                sendFormData(formData, setSendingState, setFormState, InputsToStateKeyMapping);
            } else {
                displayEmptyInputs(formData, setFormState, InputsToStateKeyMapping);
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
                <Form ref={registerForm} mapping={InputsToStateMapping} sendingState={sendingState} onSubmit={handleClick}></Form>
                <div>
                    <p>Vous souhaitez vous connecter?</p>
                    <a href="/login"> Connectez-vous ici.</a>
                </div>  
           </>;
}

async function sendFormData(formData:FormData, setSendingState:React.Dispatch<React.SetStateAction<SendingStatus<any>>>, setFormState:React.Dispatch<React.SetStateAction<FormBoolean>>, InputsToStateKeyMapping: Record<string, keyof FormBoolean>){
    /* Envoie les données du formulaire et modifie data ou error dans l'état caractérisant le formulaire */
    const response = await fetchData(formData);         
    const text = await response.text();

    if (response.ok) {
        const data = JSON.parse(text);
        setSendingState(prev => ({...prev, data: data}));
    } else {
        displayEmptyInputs(formData, setFormState, InputsToStateKeyMapping);
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

function displayEmptyInputs(formData:FormData, setFormState:React.Dispatch<React.SetStateAction<FormBoolean>>, InputsToStateKeyMapping: Record<string, keyof FormBoolean>){
    /*setFormState({isFirstnameEmpty : formData.get("firstname") === "",
                isLastnameEmpty : formData.get("lastname") === "",
                isUsernameEmpty : formData.get("mail") === "",
                isPasswordEmpty : formData.get("password") === ""});*/
    
    const keys = Array.from(formData.keys());
    setFormState(prev => {
        const newState = {...prev};
        keys.forEach(key => {
            const stateKey = InputsToStateKeyMapping[key];
            newState[stateKey] = formData.get(key) === ""; 
            
        })
        return newState;
    });
}