import { useRef, type FormEvent } from "react";
import { Form } from "../components/Form";
import { useRegisterForm } from "../hooks/useRegisterForm";
import { RegisterFormHandler } from "../utils/authentication/RegisterFormHandler";
import { Goto } from "../components/Goto";

export function RegisterPage(){
    const registerForm = useRef<HTMLFormElement>(null);
    const {inputToStateMapping, setFormState, sendingState, setSendingState, inputToStateKeyMapping} = useRegisterForm();

    const handleClick = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (registerForm.current !== null){
            setSendingState(prev => ({...prev, error:""})); //On enlève l'erreur d'envoi éventuellement affichée
            const formData = new FormData(registerForm.current); 
            const formHandler = new RegisterFormHandler({formData, setFormState, setSendingState, inputToStateKeyMapping});

            if (!formHandler.allInputsAreFilled()){
                formHandler.displayEmptyInputs();
                return;
            }
            if (!formHandler.passwordAreCongruent()){
                formHandler.displayPasswordIncongruent();
                return;
            }
            formHandler.sendFormData("http://localhost:9000/auth/register");
        }
    }

    if (sendingState.data !== null){
        return <>
                    <h2> Votre inscription a bien été réalisée.</h2>
                    <a href="/login"> Cliquez ici pour vous logger</a>
               </>
    }

    return <>
                <h1> Inscription </h1>
                <Form ref={registerForm} mapping={inputToStateMapping} sendingState={sendingState} onSubmit={handleClick}></Form>
                <Goto href="/login" label="Vous souhaitez vous connecter?"/>
           </>;
}
