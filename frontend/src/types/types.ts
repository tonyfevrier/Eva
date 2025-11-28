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

export type FormBoolean = {
    isFirstnameEmpty: boolean,
    isLastnameEmpty: boolean,
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