import { NavBar } from "../components/NavBar"
import { Button } from "../components/Button"
import { useState } from "react"

import { NewExperimentationPage } from "./NewExperimentationPage";


export function ExperimentationPage(){
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