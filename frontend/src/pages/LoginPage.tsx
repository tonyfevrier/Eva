import { useRef, type FormEvent } from "react";
import { useLoginForm } from "../hooks/useLoginForm";
import { useTheme } from "../hooks/useTheme";
import { Form } from "../components/Form";
import { LoginFormHandler } from "../utils/authentication/LoginFormHandler";
import { Goto } from "../components/Goto";
import { DescribePage } from "./DescribePage";

export function LoginPage({}){
    const loginForm = useRef<HTMLFormElement>(null);
    const {inputToStateMapping, setFormState, sendingState, setSendingState, inputToStateKeyMapping} = useLoginForm();
    const {isAuthenticated, toggleIsAuthenticated, setExpirationTime} = useTheme();
    
    const handleClick = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (loginForm.current !== null){
            const formData = new FormData(loginForm.current); 
            const formHandler = new LoginFormHandler({formData, setFormState, setSendingState, inputToStateKeyMapping}, 
                                                                  {toggleIsAuthenticated, setExpirationTime});
            if (formHandler.allInputsAreFilled()){
                await formHandler.sendFormData("http://localhost:9000/auth/login");
            } else {
                formHandler.displayEmptyInputs();
            }
        }
    }
    
    if (isAuthenticated){
        return <DescribePage/>
    }
     
    return  <>
                <h1> Se connecter</h1>
                <Form ref={loginForm} mapping={inputToStateMapping} sendingState={sendingState} onSubmit={handleClick}></Form>
                <Goto href="/register" label="Vous n'avez pas encore de compte?" buttonLabel="Inscrivez-vous ici."/>
                <Goto href="/pwdForget" label="Vous avez oublié votre mot de passe?"/>
            </>
}