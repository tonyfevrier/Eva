import type {FormHandlerInput, SendingStatus } from "../../types/types";
import { apiFetch } from "../apiFetch";

export abstract class FormHandler<T> {

    protected formData: FormData;
    protected setFormState: React.Dispatch<React.SetStateAction<T>>;
    protected setSendingState:React.Dispatch<React.SetStateAction<SendingStatus<any>>>;
    protected inputToStateKeyMapping: Record<string, keyof T>;    

    constructor(formHandler:FormHandlerInput<T>){
        this.formData = formHandler.formData;
        this.setFormState = formHandler.setFormState;
        this.setSendingState = formHandler.setSendingState;
        this.inputToStateKeyMapping = formHandler.inputToStateKeyMapping;
    }

    abstract sendFormData(url: string): Promise<void>;

    displayEmptyInputs(){
        const keys = Array.from(this.formData.keys());
        this.setFormState(prev => {
            const newState = {...prev};
            keys.forEach(key => {
                const stateKey = this.inputToStateKeyMapping[key];
                newState[stateKey] = (this.formData.get(key) === "") as T[keyof T]; 
            })
            return newState;
        });
    }

    allInputsAreFilled():boolean{
        const formKeys = Array.from(this.formData.keys());
        return formKeys.every(key => this.formData.get(key) !== "");
    }

    async _fetchData(endpointPath:string){
        const body = this._createBody();
        const response = await apiFetch(endpointPath, {
            method : "POST",
            body : body,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            credentials: "include" // Pour recevoir et stocker ou envoyer des cookies
        });
        return response;    
    }

    _createBody():string {
        return JSON.stringify(Object.fromEntries(this.formData.entries()));
    }
}