import type { CircleDimensions } from "../../types/types";

export function homepageGeometry(){
    const northEastCircleDimensions: CircleDimensions = {
        startingPoint: ['100', '25'],
        endPoint: ['175', '100'],
        rotationSense: 1
      };
    
    const southEastCircleDimensions: CircleDimensions = {
      startingPoint: ['100', '175'],
      endPoint: ['175', '100'],
      rotationSense: 0
    };
    
    const northWestCircleDimensions: CircleDimensions = {
      startingPoint: ['25', '100'],
      endPoint: ['100', '25'],
      rotationSense: 1
    };
    
    const southWestCircleDimensions: CircleDimensions = {
      startingPoint: ['25', '100'],
      endPoint: ['100', '175'],
      rotationSense: 0
    };
    return {northEastCircleDimensions,
            southEastCircleDimensions, 
            northWestCircleDimensions, 
            southWestCircleDimensions};
}