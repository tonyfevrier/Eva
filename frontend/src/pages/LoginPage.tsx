import { useRef, type FormEvent } from "react";
import { FormHandler } from "./FormHandler";
import type { LoginFormBoolean } from "../types/types";
import { useLoginForm } from "../hooks/useLoginForm";
import { Form } from "../components/Form";

export function LoginPage({}){
    const loginForm = useRef<HTMLFormElement>(null);
    const {inputToStateMapping, setFormState, sendingState, setSendingState, inputToStateKeyMapping} = useLoginForm();
    
        const handleClick = async (e: FormEvent<HTMLFormElement>) => {
            e.preventDefault();
            if (loginForm.current !== null){
                const formData = new FormData(loginForm.current); 
                const formHandler = new FormHandler<LoginFormBoolean>({formData, setFormState, setSendingState, inputToStateKeyMapping});
    
                if (formHandler.allInputsAreFilled()){
                    formHandler.sendFormData("http://localhost:9000/api/login");
                } else {
                    formHandler.displayEmptyInputs();
                }
            }
        }
    
        if (sendingState.data !== null){
            return <>
                        <h1> Vous êtes bien connecté.</h1>
                        <a href="/"> Retournez à la page d'accueil. </a>
                   </>
        }
     
        return  <>
                    <h1> Se connecter</h1>
                    <Form ref={loginForm} mapping={inputToStateMapping} sendingState={sendingState} onSubmit={handleClick}></Form>
                    <div>
                        <p>Vous n'avez pas encore de compte?</p>
                        <a href="/register">Inscrivez-vous ici.</a>
                    </div>
                </>
}