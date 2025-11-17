export type CircleArgs = {
    href: string,
    Id: string,
    circleDimensions: CircleDimensions,
    color?: string,
};

export type CircleDimensions = {
    startingPoint: Array<string>,
    endPoint: Array<string>,
    rotationSense: number,
}