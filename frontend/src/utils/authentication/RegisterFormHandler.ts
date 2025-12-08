import type { RegisterFormBoolean } from "../../types/types";
import { FormHandler } from "./FormHandler";

export class RegisterFormHandler extends FormHandler<RegisterFormBoolean> {

    async sendFormData(url:string){
        const response = await this._fetchData(url);         
        const text = await response.text();
        this.displayEmptyInputs(); // évite qu'une erreur de non complétion d'inputs reste affichée après soumission du formulaire
        if (response.ok) {
            const data = JSON.parse(text);
            this.setSendingState(prev => ({...prev, data: data}));
        } else {
            this.setSendingState(prev => ({...prev, error: text}))
        }
    }
 
}