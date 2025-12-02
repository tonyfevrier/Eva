import type { FormBoolean, SendingStatus } from "../types/types";

export class FormHandling {

    private formData: FormData;
    private setFormState: React.Dispatch<React.SetStateAction<FormBoolean>>;
    private setSendingState:React.Dispatch<React.SetStateAction<SendingStatus<any>>>;
    private inputToStateKeyMapping: Record<string, keyof FormBoolean>;
    
    constructor(formData: FormData,
                setFormState: React.Dispatch<React.SetStateAction<FormBoolean>>,
                setSendingState:React.Dispatch<React.SetStateAction<SendingStatus<any>>>,
                inputToStateKeyMapping: Record<string, keyof FormBoolean>,){
        this.formData = formData;
        this.setFormState = setFormState;
        this.setSendingState = setSendingState;
        this.inputToStateKeyMapping = inputToStateKeyMapping;
    }   

    async sendFormData(){
        const response = await this._fetchData();         
        const text = await response.text();
        this.displayEmptyInputs(); // évite qu'une erreur de non complétion d'inputs reste affichée après soumission du formulaire

        if (response.ok) {
            const data = JSON.parse(text);
            this.setSendingState(prev => ({...prev, data: data}));
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
                newState[stateKey] = this.formData.get(key) === ""; 
            })
            return newState;
        });
    }

    async _fetchData(){
        const body = this._createBody();
        const response = await fetch("http://localhost:9000/api/register", {
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