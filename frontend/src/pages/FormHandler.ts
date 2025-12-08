import type { AuthContextSetterType, FormHandlerInput, SendingStatus } from "../types/types";

export class FormHandler<T> {

    private formData: FormData;
    private setFormState: React.Dispatch<React.SetStateAction<T>>;
    private setSendingState:React.Dispatch<React.SetStateAction<SendingStatus<any>>>;
    private inputToStateKeyMapping: Record<string, keyof T>;
    private toggleIsAuthenticated: () => void;
    private setExpirationTime: React.Dispatch<React.SetStateAction<number>>;
    
    constructor(formHandler:FormHandlerInput<T>, authSetterContext:AuthContextSetterType){
        this.formData = formHandler.formData;
        this.setFormState = formHandler.setFormState;
        this.setSendingState = formHandler.setSendingState;
        this.inputToStateKeyMapping = formHandler.inputToStateKeyMapping;
        this.toggleIsAuthenticated = authSetterContext.toggleIsAuthenticated;
        this.setExpirationTime = authSetterContext.setExpirationTime;
    }

    async sendFormData(url:string){
        const response = await this._fetchData(url);         
        const text = await response.text();
        this.displayEmptyInputs(); // évite qu'une erreur de non complétion d'inputs reste affichée après soumission du formulaire
        if (response.ok) {
            const data = JSON.parse(text);
            this.setSendingState(prev => ({...prev, data: data}));
            this.toggleIsAuthenticated();
            this.setExpirationTime(Date.now() + data.expiresIn);
        } else {
            this.setSendingState(prev => ({...prev, error: text}))
        }
    }

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

    async _fetchData(url:string){
        const body = this._createBody();
        const response = await fetch(url, {
            method : "POST",
            body : body,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            }
        });
        return response;    
    }

    _createBody():string {
        return JSON.stringify(Object.fromEntries(this.formData.entries()));
    }
}