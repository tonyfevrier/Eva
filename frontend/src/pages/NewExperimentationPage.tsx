import { Input } from "../components/Input";
import { Select } from "../components/Select";
import { Textarea } from "../components/Textarea";
import { Cloud } from "../components/Cloud";
import { NavBar } from "../components/NavBar"
import { Button } from "../components/Button"
import { useState, type ReactEventHandler } from "react";
import { MultiStep } from "../components/MultiStep";

type ExperimentationData = {
    keywords: Map<string, Boolean>; //Array<string>;
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

export function NewExperimentationPage(){
     /*
    Etats nécessaires : 
    - un état traduisant le fait qu'on soit dans la liste ou nouvelle expé
    - Pour la partie Nouvelle expérimentation :
        - Un état formulaire contenant l'ensemble des données à entrer.
    - des états liés au useFetch qui récupère la liste des expés
    - A compléter après
    */

    const initialExpeData = {keywords: new Map([["1", false], ["2", false]]), personalKeywords: "", problem: "",
                                 affiliation: "", classroom: "", oldPedagogy: "",
                                 newPedagogy: "", groupsDescription: "",
                                 protocol: "", isSharingData: false};
    
    const [expeData, setExpeData] = useState<ExperimentationData>(initialExpeData);
    
    const firstPageIsFilled = expeData.problem !== "" && expeData.oldPedagogy !== "" && expeData.newPedagogy !== "";
    const secondPageIsFilled = expeData.protocol !== "";
    const thirdPageIsFilled = expeData.affiliation !== "" && expeData.classroom !== "" && expeData.groupsDescription !== "";

    const clickableSteps = new Map([["1: Pédagogies impliquées", true],
                               ["2: Choix du protocole", firstPageIsFilled],
                               ["3: Contexte", secondPageIsFilled],
                               ["4: Comment évaluer?", thirdPageIsFilled]]);

    const handleClickOnCloud = (e:React.MouseEvent<HTMLButtonElement>) => {
        const newKeyWords = new Map(expeData.keywords);
        const targetIsClicked = expeData.keywords.get(e.currentTarget.innerText);
        newKeyWords.set(e.currentTarget.innerText, !targetIsClicked);
        setExpeData({...expeData, keywords: newKeyWords});
    }

    return <>
                <MultiStep clickableSteps={clickableSteps} >
                    <div>
                        <Cloud title="Choisissez éventuellement des mots clés" options={expeData.keywords} onClick={handleClickOnCloud}/>
                        <Input title="Autres mots clés personnalisés" value={expeData.personalKeywords} onChange={e => {setExpeData({...expeData, personalKeywords: e.target.value})}}/>
                        <Textarea title="Problème rencontré en classe" variant="withErrorMsg" value={expeData.problem} onChange={e => {setExpeData({...expeData, problem: e.target.value})}}/>
                        <Textarea title="Ancienne pédagogie" variant="withErrorMsg" value={expeData.oldPedagogy} onChange={e => {setExpeData({...expeData, oldPedagogy: e.target.value})}}/>
                        <Textarea title="Nouvelle pédagogie" variant="withErrorMsg" value={expeData.newPedagogy} onChange={e => {setExpeData({...expeData, newPedagogy: e.target.value})}}/>
                    </div>
                    <div>
                        <h2>Pour évaluer votre pratique, nous vous donnons le choix entre les protocoles suivants</h2>
                        <p>blablabbffbfhsfjhsfkfhs</p>
                        <Select title="Quel protocole choisissez-vous pour votre évaluation" value={expeData.protocol} onChange={(e) => {setExpeData({...expeData, protocol:e.target.value})}}>
                            <option value="">--Please choose an option--</option>
                            <option value="2">1</option>
                            <option value="3">1</option>
                            <option value="4">1</option>
                        </Select>
                    </div>
                    <div>
                        <p>Veuillez entrer quelques précisions sur votre contexte pédagogique</p>
                        <Input title="Affiliation" variant="withErrorMsg" value={expeData.affiliation} onChange={e => {setExpeData({...expeData, affiliation: e.target.value})}}/>
                        <Input title="Classe" variant="withErrorMsg" value={expeData.classroom} onChange={e => {setExpeData({...expeData, classroom: e.target.value})}}/>
                        <Textarea title="Veuillez décrire les différences entre vos deux groupes" variant="withErrorMsg" value={expeData.groupsDescription} onChange={e => {setExpeData({...expeData, groupsDescription: e.target.value})}}/>
                    </div> 
                </MultiStep>
            </>
}