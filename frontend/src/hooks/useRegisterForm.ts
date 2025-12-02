import { useState } from "react";
import type { RegisterFormBoolean, SendingStatus } from "../types/types";

export function useRegisterForm(){
    const [formState, setFormState] = useState<RegisterFormBoolean>({
            isFirstnameEmpty : false,
            isLastnameEmpty : false,
            isUsernameEmpty : false,
            isPasswordEmpty : false, 
        });
    
    const [sendingState, setSendingState] = useState<SendingStatus<any>>({
        data: null,
        error: null
    })
    
    const inputToStateKeyMapping: Record<string, keyof RegisterFormBoolean> = {
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