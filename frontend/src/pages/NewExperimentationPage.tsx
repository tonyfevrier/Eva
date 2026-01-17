import { Input } from "../components/Input";
import { Select } from "../components/Select";
import { Textarea } from "../components/Textarea";
import { Cloud } from "../components/Cloud";
import { NavBar } from "../components/NavBar"
import { Button } from "../components/Button"
import { useState } from "react";
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

    const initialExpeData = {keywords: new Map([["1", true], ["2", false]]), personalKeywords: "", problem: "",
                                 affiliation: "", classroom: "", oldPedagogy: "",
                                 newPedagogy: "", groupsDescription: "",
                                 protocol: "", isSharingData: false};
    
    const [expeData, setExpeData] = useState<ExperimentationData>(initialExpeData);
    
    const firstPageIsFilled = expeData.problem !== "" && expeData.oldPedagogy !== "" && expeData.newPedagogy !== "";
    //const secondPageIsFilled;
    //const thirdPageIsFilled;
    const clickableSteps = new Map([["1: Pédagogies impliquées", true],
                               ["2: Choix du protocole", firstPageIsFilled],
                               ["3: Contexte", false],
                               ["4: Comment évaluer?", false]]);

    return <>
                <MultiStep clickableSteps={clickableSteps} >
                    <div>
                        <Cloud title="Choisissez éventuellement des mots clés" options={expeData.keywords}/>
                        <p>Ici il faudra mettre la sélection de mots clés</p>
                        <Input title="Autres mots clés personnalisés" value={expeData.personalKeywords} onChange={e => {setExpeData({...expeData, personalKeywords: e.target.value})}}/>
                        <Textarea title="Problème rencontré en classe" variant="withErrorMsg" value={expeData.problem} onChange={e => {setExpeData({...expeData, problem: e.target.value})}}/>
                        <Textarea title="Ancienne pédagogie" variant="withErrorMsg" value={expeData.oldPedagogy} onChange={e => {setExpeData({...expeData, oldPedagogy: e.target.value})}}/>
                        <Textarea title="Nouvelle pédagogie" variant="withErrorMsg" value={expeData.newPedagogy} onChange={e => {setExpeData({...expeData, newPedagogy: e.target.value})}}/>
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
                    <div>
                        <p>Veuillez entrer quelques précisions sur votre contexte pédagogique</p>
                        <Input title="Affiliation" variant="withErrorMsg" value={expeData.affiliation} onChange={e => {setExpeData({...expeData, affiliation: e.target.value})}}/>
                        <Input title="Classe" variant="withErrorMsg" value={expeData.classroom} onChange={e => {setExpeData({...expeData, classroom: e.target.value})}}/>
                        <Textarea title="Veuillez décrire les différences entre vos deux groupes" variant="withErrorMsg" value={expeData.groupsDescription} onChange={e => {setExpeData({...expeData, groupsDescription: e.target.value})}}/>
                    </div> 
                </MultiStep>
                
                {/*    <NavBar variant="primary">
                        <Button>1: Pédagogies impliquées</Button>
                        <Button disabled={true}>2: Choix du protocole</Button>
                        <Button disabled={true}>3: Contexte</Button>
                        <Button disabled={true}>4: Comment évaluer?</Button>
                    </NavBar>
                    <div>
                        <Cloud title="Choisissez éventuellement des mots clés" options={expeData.keywords}/>
                        <p>Ici il faudra mettre la sélection de mots clés</p>
                        <Input title="Autres mots clés personnalisés" value={expeData.personalKeywords} onChange={e => {setExpeData({...expeData, personalKeywords: e.target.value})}}/>
                        <Textarea title="Problème rencontré en classe" variant="withErrorMsg" value={expeData.problem} onChange={e => {setExpeData({...expeData, problem: e.target.value})}}/>
                        <Textarea title="Ancienne pédagogie" variant="withErrorMsg" value={expeData.oldPedagogy} onChange={e => {setExpeData({...expeData, oldPedagogy: e.target.value})}}/>
                        <Textarea title="Nouvelle pédagogie" variant="withErrorMsg" value={expeData.newPedagogy} onChange={e => {setExpeData({...expeData, newPedagogy: e.target.value})}}/>
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
                    <div>
                        <p>Veuillez entrer quelques précisions sur votre contexte pédagogique</p>
                        <Input title="Affiliation" variant="withErrorMsg" value={expeData.affiliation} onChange={e => {setExpeData({...expeData, affiliation: e.target.value})}}/>
                        <Input title="Classe" variant="withErrorMsg" value={expeData.classroom} onChange={e => {setExpeData({...expeData, classroom: e.target.value})}}/>
                        <Textarea title="Veuillez décrire les différences entre vos deux groupes" variant="withErrorMsg" value={expeData.groupsDescription} onChange={e => {setExpeData({...expeData, groupsDescription: e.target.value})}}/>
                    </div>
                    <div>
                        <Button>Précédent</Button>
                        <Button>Suivant</Button>
                    </div> */}
                </>
}