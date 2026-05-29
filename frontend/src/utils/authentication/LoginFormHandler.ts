import type { Dispatch, SetStateAction } from "react";
import type { AuthContextSetterType, FormHandlerInput, LoginFormBoolean} from "../../types/types";
import { FormHandler } from "./FormHandler";

export class LoginFormHandler extends FormHandler<LoginFormBoolean> {

    private toggleIsAuthenticated: () => void;
    private setExpirationTime: React.Dispatch<React.SetStateAction<number>>;
    private setIsProfileCompleted: Dispatch<SetStateAction<boolean>>;
    

    constructor(formHandler:FormHandlerInput<LoginFormBoolean>, authSetterContext:AuthContextSetterType){
        super(formHandler);
        this.toggleIsAuthenticated = authSetterContext.toggleIsAuthenticated;
        this.setExpirationTime = authSetterContext.setExpirationTime;
        this.setIsProfileCompleted = authSetterContext.setIsProfileCompleted
    }

    async sendFormData(endpointPath:string){
        const response = await this._fetchData(endpointPath); 
        const text = await response.text();
        this.displayEmptyInputs(); // évite qu'une erreur de non complétion d'inputs reste affichée après soumission du formulaire
        if (response.ok) {
            const data = JSON.parse(text);
            this.setSendingState(prev => ({...prev, data: data}));
            this.toggleIsAuthenticated();
            this.setExpirationTime(Date.now() + data.accessExpiresIn);
            this.setIsProfileCompleted(data.additionalData !== "null"? true: false);
        } else {
            this.setSendingState(prev => ({...prev, error: text}))
        }
    }
 
}