import type { Dispatch, SetStateAction } from "react";
import type { RegisterFormBoolean } from "../../types/types";
import { FormHandler } from "./FormHandler";

export class RegisterFormHandler extends FormHandler<RegisterFormBoolean> {

    async sendFormData(endpointPath:string, setRegistrationSent: Dispatch<SetStateAction<Boolean>>){
        const response = await this._fetchData(endpointPath);         
        const text = await response.text();
        this.displayEmptyInputs(); // évite qu'une erreur de non complétion d'inputs reste affichée après soumission du formulaire
        if (response.ok) {
            const data = JSON.parse(text);
            this.setSendingState(prev => ({...prev, data: data}));
            setRegistrationSent(true);
        } else {
            this.setSendingState(prev => ({...prev, error: text}))
        }
    }

    displayPasswordIncongruent(){ 
        this.setSendingState(prev => ({...prev, error: "Les mots de passe doivent avoir au moins 8 caractères et être identiques"}));
    }

    passwordAreCongruent(){
        /*Les mots de passe doivent être identiques et de longueur >= 8*/
        const password = this.formData.get("password");
        const passwordCopy = this.formData.get("passwordCopy");
        if (typeof password === "string" && typeof passwordCopy === "string"){
            return password.length >= 8 && password === passwordCopy;
        }
        return false;
    }
 
}