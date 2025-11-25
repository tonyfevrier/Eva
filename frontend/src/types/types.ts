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

export type FormBoolean<T> = {
    isFirstnameEmpty: boolean,
    isLastnameEmpty: boolean,
    isUsernameEmpty: boolean,
    isPasswordEmpty: boolean,
    data: T | null,
    error:Error | null
};

export type FormAction = {
    type: "submitForm";
    formRef: HTMLFormElement | null;
};