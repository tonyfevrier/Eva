import { useState } from "react";
import { MultiStep } from "../components/MultiStep";
import { FirstStep } from "./newExperimentationSubPages/FirstStep";
import { SecondStep } from "./newExperimentationSubPages/SecondStep";
import { ThirdStep } from "./newExperimentationSubPages/ThirdStep";
import { FourthStep } from "./newExperimentationSubPages/FourthStep";

export type ExperimentationData = {
    keywords: Map<string, Boolean>; //Array<string>;
    personalKeywords: string;
    learningDifficulty: string;
    learningDifficultyOrigin: string;
    affiliation: string;
    studyField: string;
    teachingTitle: string;
    knowledges: string;
    prerequisite: string;
    organisationParticularities: string;
    yearOfStudy: string;
    classesFrequencies: string;
    classesDates: string;
    studentsSpecificities: string;
    studentsNumber: string;
    oldPedagogy: string;
    newPedagogy: string;
    protocol: string;
    isSharingData: boolean;
    initialEvaluationOld: string;
    immediateEvaluationOld: string;
    delayedEvaluationOld: string;
    accountedEvaluationOld: string;
    initialEvaluationNew: string;
    immediateEvaluationNew: string;
    delayedEvaluationNew: string;
    accountedEvaluationNew: string;
}


export function NewExperimentationPage(){
    const initialExpeData: ExperimentationData = {
        keywords: new Map([["Attention", false], ["Motivation", false],
                           ["Compréhension", false], ["Raisonnement", false],
                           ["Gestion de classe", false], ["Evaluation", false],
                           ["Mémorisation", false]]), 
        personalKeywords: "", 
        learningDifficulty: "",
        learningDifficultyOrigin: "",
        affiliation: "", 
        studyField: "",
        teachingTitle: "",
        knowledges: "",
        prerequisite: "",
        organisationParticularities: "",
        yearOfStudy: "",
        classesFrequencies: "",
        classesDates: "",
        studentsSpecificities: "",
        studentsNumber: "",
        oldPedagogy: "",
        newPedagogy: "", 
        protocol: "", 
        isSharingData: false,
        initialEvaluationOld: "",
        immediateEvaluationOld: "",
        delayedEvaluationOld: "",
        accountedEvaluationOld: "",
        initialEvaluationNew: "",
        immediateEvaluationNew: "",
        delayedEvaluationNew: "",
        accountedEvaluationNew: ""
    };
    
    const [expeData, setExpeData] = useState<ExperimentationData>(initialExpeData);
    
    const oneKeyWordIsChosen = Array.from(expeData.keywords.values()).some(value => value === true) || expeData.personalKeywords !== "";
    const firstPageIsFilled = oneKeyWordIsChosen && expeData.affiliation !== "" && expeData.learningDifficulty !== "" && expeData.learningDifficultyOrigin !== "" && expeData.oldPedagogy !== "" && expeData.newPedagogy !== "";
    const secondPageIsFilled = expeData.protocol !== "";
    const thirdPageIsFilled = expeData.studyField !== "" && expeData.teachingTitle !== "" && expeData.knowledges !== "" && expeData.prerequisite !== "" && expeData.organisationParticularities !== "" && expeData.classesFrequencies !== "" && expeData.classesDates !== "" && expeData.yearOfStudy !== "" && expeData.studentsNumber !== "" && expeData.studentsSpecificities !== "" ;
    const fourthPageIsFilled = expeData.initialEvaluationOld !== "" && expeData.immediateEvaluationOld !== "" && expeData.delayedEvaluationOld !== "" && expeData.accountedEvaluationOld !== "" && expeData.initialEvaluationNew !== "" && expeData.immediateEvaluationNew !== "" && expeData.delayedEvaluationNew !== "" && expeData.accountedEvaluationNew !== "";

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
                    <FourthStep state={expeData} setState={setExpeData}/>
                </MultiStep>
            </>
}