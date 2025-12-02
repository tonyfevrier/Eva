import { useState } from "react";
import type { LoginFormBoolean, SendingStatus } from "../types/types";

export function useLoginForm(){
    const [formState, setFormState] = useState<LoginFormBoolean>({
            isUsernameEmpty : false,
            isPasswordEmpty : false, 
        });
    
    const [sendingState, setSendingState] = useState<SendingStatus<any>>({
        data: null,
        error: null
    })
    
    const inputToStateKeyMapping: Record<string, keyof LoginFormBoolean> = { 
                          "mail": "isUsernameEmpty",
                          "password": "isPasswordEmpty"};
    
    const inputToStateMapping: Record<string, boolean> = { 
                          "mail": formState.isUsernameEmpty,
                          "password": formState.isPasswordEmpty};

    return {inputToStateMapping, setFormState, sendingState, setSendingState, inputToStateKeyMapping};
}