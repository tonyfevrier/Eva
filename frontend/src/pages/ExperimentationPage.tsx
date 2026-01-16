import { NavBar } from "../components/NavBar"
import { Button } from "../components/Button"
import { useState } from "react"
import { Input } from "../components/Input";
import { Select } from "../components/Select";
import { Textarea } from "../components/Textarea";

type ExperimentationData = {
    keywords: Array<string>;
    personalKeywords: string;
    problem: string;
    affiliation: string;
    classroom: string;
    oldPedagogy: string;
    newPedagogy: string;
    groupsDescription: string
    protocol: string;
    isSharingData: boolean;
}


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
    const initialExpeData = {keywords: [], personalKeywords: "", problem: "",
                             affiliation: "", classroom: "", oldPedagogy: "",
                             newPedagogy: "", groupsDescription: "",
                             protocol: "", isSharingData: false};
    const [expeData, setExpeData] = useState<ExperimentationData>(initialExpeData);
    
    return <>
                <NavBar variant="secondary">
                    <Button onClick={()=>{setPage("list")}}>Liste des expérimentations</Button>
                    <Button onClick={()=>{setPage("newExpe")}}>Nouvelle expérimentation</Button>
                </NavBar>

                {page === "list" && <>Liste des expés</>}
                {page === "newExpe" && 
                <>
                    <NavBar variant="primary">
                        <Button>1: Pédagogies impliquées</Button>
                        <Button disabled={true}>2: Choix du protocole</Button>
                        <Button disabled={true}>3: Contexte</Button>
                        <Button disabled={true}>4: Comment évaluer?</Button>
                    </NavBar>
                    <div>
                        <p>Ici il faudra mettre la sélection de mots clés</p>
                        <Input title="Autres mots clés personnalisés"/>
                        <Textarea title="Problème rencontré en classe" variant="withErrorMsg"/>
                        <Textarea title="Ancienne pédagogie" variant="withErrorMsg"/>
                        <Textarea title="Nouvelle pédagogie" variant="withErrorMsg"/>
                    </div>
                    <div>
                        <h2>Pour évaluer votre pratique, nous vous donnons le choix entre les protocoles suivants</h2>
                        <p>blablabbffbfhsfjhsfkfhs</p>
                        <Select title="Quel protocole choisissez-vous pour votre évaluation">
                            <option value="1optiopj kjfkljsk">optiopj kjfkljsk1</option>
                            <option value="2">1</option>
                            <option value="3">1</option>
                            <option value="4">1</option>
                        </Select>
                    </div>
                </>}
           </>
}