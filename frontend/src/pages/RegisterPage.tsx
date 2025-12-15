import { useRef, type FormEvent } from "react";
import { Form } from "../components/Form";
import { useRegisterForm } from "../hooks/useRegisterForm";
import { RegisterFormHandler } from "../utils/authentication/RegisterFormHandler";
 

export function RegisterPage(){
    const registerForm = useRef<HTMLFormElement>(null);
    const {inputToStateMapping, setFormState, sendingState, setSendingState, inputToStateKeyMapping} = useRegisterForm();

    const handleClick = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (registerForm.current !== null){
            const formData = new FormData(registerForm.current); 
            const formHandler = new RegisterFormHandler({formData, setFormState, setSendingState, inputToStateKeyMapping});

            if (formHandler.allInputsAreFilled()){
                formHandler.sendFormData("http://localhost:9000/auth/register");
            } else {
                formHandler.displayEmptyInputs();
            }
        }
    }

    if (sendingState.data !== null){
        return <>
                    <h1> Votre inscription a bien été réalisée.</h1>
                    <a href="/login"> Cliquez ici pour vous logger</a>
               </>
    }

    return <>
                <h1> Inscription </h1>
                <Form ref={registerForm} mapping={inputToStateMapping} sendingState={sendingState} onSubmit={handleClick}></Form>
                <div>
                    <p>Vous souhaitez vous connecter?</p>
                    <a href="/login"> Connectez-vous ici.</a>
                </div>  
           </>;
}
