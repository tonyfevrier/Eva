import { useState } from "react";
import { MultiStep } from "../components/MultiStep";
import { FirstStep } from "./newExperimentationSubPages/FirstStep";
import { SecondStep } from "./newExperimentationSubPages/SecondStep";
import { ThirdStep } from "./newExperimentationSubPages/ThirdStep";

export type ExperimentationData = {
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
        /*Met le mot clé cliqué à true afin qu'il soit coloré */
        const newKeyWords = new Map(expeData.keywords);
        const targetIsClicked = expeData.keywords.get(e.currentTarget.innerText);
        newKeyWords.set(e.currentTarget.innerText, !targetIsClicked);
        setExpeData({...expeData, keywords: newKeyWords});
    }

    return <>
                <MultiStep clickableSteps={clickableSteps} >
                    <FirstStep state={expeData} setState={setExpeData} handleClickOnCloud={handleClickOnCloud}/>
                    <SecondStep state={expeData} setState={setExpeData}/>
                    <ThirdStep state={expeData} setState={setExpeData}/>
                </MultiStep>
            </>
}