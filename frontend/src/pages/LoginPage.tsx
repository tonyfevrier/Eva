import { useRef, type FormEvent } from "react";
import { useLoginForm } from "../hooks/useLoginForm";
import { useTheme } from "../hooks/useTheme";
import { Form } from "../components/Form";
import { LoginFormHandler } from "../utils/authentication/LoginFormHandler";
import { Goto } from "../components/Goto";
import { DescribePage } from "./DescribePage";
import { HomePage } from "./HomePage";

export function LoginPage({}){
    const loginForm = useRef<HTMLFormElement>(null);
    const {inputToStateMapping, setFormState, sendingState, setSendingState, inputToStateKeyMapping} = useLoginForm();
    const {isAuthenticated, toggleIsAuthenticated, setExpirationTime, isProfileCompleted, setIsProfileCompleted} = useTheme();
    
    const handleClick = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (loginForm.current !== null){
            const formData = new FormData(loginForm.current); 
            const formHandler = new LoginFormHandler({formData, setFormState, setSendingState, inputToStateKeyMapping}, 
                                                                  {toggleIsAuthenticated, setExpirationTime, setIsProfileCompleted});
            if (formHandler.allInputsAreFilled()){
                await formHandler.sendFormData("http://localhost:9000/auth/login");
            } else {
                formHandler.displayEmptyInputs();
            }
        }
    }
    
    if (isAuthenticated && !isProfileCompleted){
        return <DescribePage/>
    }

    if (isAuthenticated && isProfileCompleted){
        return  <>
                    <Goto href="/" label="Vous êtes connecté." buttonLabel="Retournez à l'accueil"/>
                </>
    }
     
    return  <>
                <h2 style={{'margin': '1em'}}> Se connecter</h2>
                <Form ref={loginForm} mapping={inputToStateMapping} sendingState={sendingState} onSubmit={handleClick}></Form>
                <Goto href="/register" label="Vous n'avez pas encore de compte?" buttonLabel="Inscrivez-vous ici."/>
                <Goto href="/pwdForget" label="Vous avez oublié votre mot de passe?"/>
            </>
}