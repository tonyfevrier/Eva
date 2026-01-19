import { NavBar } from "../components/NavBar"
import { Button } from "../components/Button"
import { useState } from "react"

import { NewExperimentationPage } from "./NewExperimentationPage";


export function ExperimentationPage(){
      /*
    Etats nécessaires : 
    - un état traduisant le fait qu'on soit dans la liste ou nouvelle expé
    - Pour la partie Nouvelle expérimentation :
        - Un état formulaire contenant l'ensemble des données à entrer.
    - des états liés au useFetch qui récupère la liste des expés
    - A compléter après
    */

    const [page, setPage] = useState<string>("list"); 
    
    return <>
                <NavBar variant="secondary">
                    <Button onClick={()=>{setPage("list")}}>Liste des expérimentations</Button>
                    <Button onClick={()=>{setPage("newExpe")}}>Nouvelle expérimentation</Button>
                </NavBar>

                {page === "list" && <>Liste des expés</>}
                {page === "newExpe" && <NewExperimentationPage/>}
           </>
}