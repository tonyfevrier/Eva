import { ArcOfCircle } from "../components/ArcOfCircle";
import { homepageGeometry } from "../utils/geometry/homepageGeometry";
import styles from "./HomePage.module.css"

export function HomePage(){
  const circleDimensions = homepageGeometry();

  return (
    <div className={styles.homeImage}>
      <svg width="400" height="420" viewBox="0 0 200 200">
        <ArcOfCircle href="/register" Id="authentification" circleDimensions={circleDimensions.northEastCircleDimensions}>S'inscrire/se connecter</ArcOfCircle>
        <ArcOfCircle href="" Id="database" color="orange" circleDimensions={circleDimensions.southEastCircleDimensions}>Base de données</ArcOfCircle> 
        <ArcOfCircle href="" Id="noArcPath" color="red" circleDimensions={circleDimensions.northWestCircleDimensions}>Clique</ArcOfCircle> 
        <ArcOfCircle href="" Id="soArcPath" color="green" circleDimensions={circleDimensions.southWestCircleDimensions}>Clique</ArcOfCircle> 
      </svg>
      <h1>EVA</h1>
    </div>
  )
}