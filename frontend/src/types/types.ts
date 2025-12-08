import type { ReactNode } from "react";

export type CircleArgs = {
    href: string,
    Id: string,
    circleDimensions: CircleDimensions,
    color?: string
};

export type CircleDimensions = {
    startingPoint: Array<string>,
    endPoint: Array<string>,
    rotationSense: number
}

export type RegisterFormBoolean = {
    isFirstnameEmpty: boolean,
    isLastnameEmpty: boolean,
    isUsernameEmpty: boolean,
    isPasswordEmpty: boolean,
};

export type LoginFormBoolean = { 
    isUsernameEmpty: boolean,
    isPasswordEmpty: boolean,
};

export type SendingStatus<T> = {
    data: T | null,
    error: string | null
}

export type FormAction = {
    type: "submitForm";
    formRef: HTMLFormElement | null;
};

export type FormHandlerInput<T> = {
    formData: FormData,
    setFormState: React.Dispatch<React.SetStateAction<T>>,
    setSendingState:React.Dispatch<React.SetStateAction<SendingStatus<any>>>,
    inputToStateKeyMapping: Record<string, keyof T>  
}

export type PrivateRouteProps = {
    children: ReactNode
}

export type AuthContextSetterType = {
    toggleIsAuthenticated: () => void;
    setExpirationTime: React.Dispatch<React.SetStateAction<number>>;
}