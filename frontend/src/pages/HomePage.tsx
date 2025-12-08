import type { CircleDimensions } from "../types/types";
import { ArcOfCircle } from "../components/ArcOfCircle";
import { homepageGeometry } from "../geometry/homepageGeometry";

export function HomePage(){
  const circleDimensions = homepageGeometry();

  return (
    <>
      <svg width="400" height="420" viewBox="0 0 200 200">
        <ArcOfCircle href="/register" Id="authentification" circleDimensions={circleDimensions.northEastCircleDimensions}>S'inscrire/se connecter</ArcOfCircle>
        <ArcOfCircle href="" Id="database" color="orange" circleDimensions={circleDimensions.southEastCircleDimensions}>Base de données</ArcOfCircle> 
        <ArcOfCircle href="" Id="noArcPath" color="red" circleDimensions={circleDimensions.northWestCircleDimensions}>Clique</ArcOfCircle> 
        <ArcOfCircle href="" Id="soArcPath" color="green" circleDimensions={circleDimensions.southWestCircleDimensions}>Clique</ArcOfCircle> 
      </svg>
    </>
  )
}