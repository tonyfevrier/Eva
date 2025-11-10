import type { CircleDimensions } from "../types/types";
import { ArcOfCircle } from "../components/ArcOfCircle";

export function HomePage(){
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

  return (
    <>
      <svg width="400" height="420" viewBox="0 0 200 200">
        <ArcOfCircle href="/login" id="authentification" circleDimensions={northEastCircleDimensions}>S'inscrire/se connecter</ArcOfCircle>
        <ArcOfCircle href="" id="database" color="orange" circleDimensions={southEastCircleDimensions}>Base de données</ArcOfCircle> 
        <ArcOfCircle href="" id="noArcPath" color="red" circleDimensions={northWestCircleDimensions}>Clique</ArcOfCircle> 
        <ArcOfCircle href="" id="soArcPath" color="green" circleDimensions={southWestCircleDimensions}>Clique</ArcOfCircle> 
      </svg>
    </>
  )
}