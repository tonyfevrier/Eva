import { useRef, useState, type FormEvent } from "react";
import { Form } from "../components/Form";
import { useRegisterForm } from "../hooks/useRegisterForm";
import { RegisterFormHandler } from "../utils/authentication/RegisterFormHandler";
import { Goto } from "../components/Goto";
import { useTheme } from "../hooks/useTheme";

export function RegisterPage(){
    const {isAuthenticated} = useTheme();
    const [registrationSent, setRegistrationSent] = useState<Boolean>(false);
    const registerForm = useRef<HTMLFormElement>(null);
    const {inputToStateMapping, setFormState, sendingState, setSendingState, inputToStateKeyMapping} = useRegisterForm();

    const handleClick = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (registerForm.current !== null){
            //On enlève les erreurs de mauvais remplissage du formulaire éventuellement affichées.
            setFormState({isFirstnameEmpty : false, isLastnameEmpty : false, isUsernameEmpty : false, isPasswordEmpty : false, isPasswordCopyEmpty: false});
            setSendingState(prev => ({...prev, error:""})); 

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
            formHandler.sendFormData("/auth/register");
            setRegistrationSent(true);
        }
    }

    if (isAuthenticated){
        return  <Goto href="/" label="Vous êtes connecté." buttonLabel="Retournez à l'accueil"/>
    }
    
    if (!registrationSent ){
        return <>
                <h2 style={{'margin': '1em'}}> Inscription </h2>
                <Form ref={registerForm} mapping={inputToStateMapping} sendingState={sendingState} onSubmit={handleClick}></Form>
                <Goto href="/login" label="Vous souhaitez vous connecter?"/>
           </>;
    }

    return <Goto href="/login" label="Un courriel vous a été envoyé, veuillez cliquer sur le lien présent dans ce courriel." buttonLabel="Revenir à la page de login"/>
}
