import { useState } from "react";
import type { FormBoolean, SendingStatus } from "../types/types";

export function useRegisterForm(){
    const [formState, setFormState] = useState<FormBoolean>({
            isFirstnameEmpty : false,
            isLastnameEmpty : false,
            isUsernameEmpty : false,
            isPasswordEmpty : false, 
        });
    
    const [sendingState, setSendingState] = useState<SendingStatus<any>>({
        data: null,
        error: null
    })
    
    const inputToStateKeyMapping: Record<string, keyof FormBoolean> = {
                          "firstname": "isFirstnameEmpty", 
                          "lastname": "isLastnameEmpty",
                          "mail": "isUsernameEmpty",
                          "password": "isPasswordEmpty"};
    
    const inputToStateMapping: Record<string, boolean> = {
                          "firstname": formState.isFirstnameEmpty, 
                          "lastname": formState.isLastnameEmpty,
                          "mail": formState.isUsernameEmpty,
                          "password": formState.isPasswordEmpty};

    return {inputToStateMapping, setFormState, sendingState, setSendingState, inputToStateKeyMapping};
}