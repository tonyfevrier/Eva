import { useRef, useState, type FormEvent } from "react";
import type { FormBoolean } from "../types/types";
 


export function RegisterPage({}){
    const registerForm = useRef(null);
    const [formState, setFormState] = useState<FormBoolean<any>>({
        isFirstnameEmpty : false,
        isLastnameEmpty : false,
        isUsernameEmpty : false,
        isPasswordEmpty : false,
        data: null,
        error: null
    });

    const handleClick = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (registerForm.current !== null){
            const formData = new FormData(registerForm.current); 
            const allInputsAreFilled = formData.get("firstname") === "" && formData.get("lastname") === "" && formData.get("mail") === "" && formData.get("password") === "";

            if (allInputsAreFilled){
                sendFormData(formData, setFormState);
            } else {
                setFormState({
                    isFirstnameEmpty : formData.get("firstname") === "",
                    isLastnameEmpty : formData.get("lastname") === "",
                    isUsernameEmpty : formData.get("mail") === "",
                    isPasswordEmpty : formData.get("password") === "",
                    data: formState.data,
                    error: formState.error});
            }
        }
    }

    return <>
                <h1> Inscription </h1>
                <form ref={registerForm} onSubmit={handleClick}>
                    <input type="text" placeholder="firstname" name="firstname"/>
                    {formState.isFirstnameEmpty && <p>Il faut remplir ce champ</p>}
                    <input type="text" placeholder="lastname" name="lastname"/>
                    {formState.isLastnameEmpty && <p>Il faut remplir ce champ</p>}
                    <input type="mail" placeholder="mail" name="mail"/>
                    {formState.isUsernameEmpty && <p>Il faut remplir ce champ</p>}
                    <input type="password" placeholder="mot de passe" name="password"/>
                    {formState.isPasswordEmpty && <p>Il faut remplir ce champ</p>}
                    <input type="submit" />
                </form>
                {formState.error !== null && `<p> ${formState.error} </p>`}
           </>;
}

async function sendFormData(formData:FormData, setFormState:React.Dispatch<React.SetStateAction<FormBoolean<any>>>){
    try {
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
                                        
        const text = await response.text();
                    
        if (response.ok) {
            const data = JSON.parse(text);
            setFormState(prev => ({...prev, data: data}));
        }
    } catch (error) {
        setFormState(prev => ({...prev, error:error as Error}));
    }
}