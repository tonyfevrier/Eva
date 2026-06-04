import { useState, type Dispatch, type SetStateAction } from "react";
import { MultiStep } from "../components/MultiStep";
import { FirstStep } from "./newExperimentationSubPages/FirstStep";
import { SecondStep } from "./newExperimentationSubPages/SecondStep";
import { ThirdStep } from "./newExperimentationSubPages/ThirdStep";
import { FourthStep } from "./newExperimentationSubPages/FourthStep";
import { useNavigate, type NavigateFunction } from "react-router-dom";
import preRegisteredData from "../data/preRegisteredData.json";
import { apiFetch } from "../utils/apiFetch";
import { Alert } from "../components/Alert";


export type Affiliation = {
    id: string,
    name: string
}

export type ExperimentationData = {
    keywords: Map<string, Boolean>; //Array<string>;
    personalKeywords: string;
    learningDifficulty: string;
    learningDifficultyOrigin: string;
    affiliation: Affiliation;
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
        keywords: new Map(preRegisteredData["keywords"].map(keyword => [keyword, false])),
        personalKeywords: "", learningDifficulty: "",learningDifficultyOrigin: "",
        affiliation: {id:"", name:""}, studyField: "",teachingTitle: "",
        knowledges: "",prerequisite: "",organisationParticularities: "",
        yearOfStudy: "",classesFrequencies: "",classesDates: "",
        studentsSpecificities: "",studentsNumber: "",oldPedagogy: "",
        newPedagogy: "", protocol: "", isSharingData: true,
        initialEvaluationOld: "",immediateEvaluationOld: "",delayedEvaluationOld: "",
        accountedEvaluationOld: "",initialEvaluationNew: "",immediateEvaluationNew: "",
        delayedEvaluationNew: "",accountedEvaluationNew: ""
    };
    
    const [expeData, setExpeData] = useState<ExperimentationData>(initialExpeData);
    const [error, setError] = useState<Error|null>(null);
    const navigate = useNavigate();

    const oneKeyWordIsChosen = Array.from(expeData.keywords.values()).some(value => value === true) || expeData.personalKeywords !== "";
    const firstPageIsFilled = oneKeyWordIsChosen && expeData.affiliation.name !== "" && expeData.learningDifficulty !== "" && expeData.learningDifficultyOrigin !== "" && expeData.oldPedagogy !== "" && expeData.newPedagogy !== "";
    const secondPageIsFilled = expeData.protocol !== "";
    const thirdPageIsFilled = expeData.studyField !== "" && expeData.teachingTitle !== "" && expeData.knowledges !== "" && expeData.prerequisite !== "" && expeData.organisationParticularities !== "" && expeData.classesFrequencies !== "" && expeData.classesDates !== "" && expeData.yearOfStudy !== "" && expeData.studentsNumber !== "" && expeData.studentsSpecificities !== "" ;
    const fourthPageIsFilled = expeData.initialEvaluationOld !== "" && expeData.immediateEvaluationOld !== "" && expeData.delayedEvaluationOld !== "" && expeData.initialEvaluationNew !== "" && expeData.immediateEvaluationNew !== "" && expeData.delayedEvaluationNew !== "";

    const clickableSteps = new Map([["1: Pédagogies impliquées", firstPageIsFilled],
                               ["2: Choix du protocole", secondPageIsFilled],
                               ["3: Contexte", thirdPageIsFilled],
                               ["4: Comment évaluer?", fourthPageIsFilled]]);

    const handleClickOnCloud = (e:React.MouseEvent<HTMLButtonElement>) => {
        /*Met le mot clé cliqué à true afin qu'il soit coloré */
        const newKeyWords = new Map(expeData.keywords);
        const targetIsClicked = expeData.keywords.get(e.currentTarget.innerText);
        newKeyWords.set(e.currentTarget.innerText, !targetIsClicked);
        setExpeData({...expeData, keywords: newKeyWords});
    }

    const saveExperimentation = () => { 
        const data = buildExperimentationData(expeData);
        sendPostRequest(data, setError, navigate); 
    }

    return <>
                <MultiStep clickableSteps={clickableSteps} onLastClick={saveExperimentation} >
                    <FirstStep state={expeData} setState={setExpeData} handleClickOnCloud={handleClickOnCloud}/>
                    <SecondStep state={expeData} setState={setExpeData}/>
                    <ThirdStep state={expeData} setState={setExpeData}/>
                    <FourthStep state={expeData} setState={setExpeData}/>
                </MultiStep>
                {error?.message && <Alert message={error?.message} onClose={() => setError(null)}/>}
            </>
}

function buildExperimentationData(expeData:ExperimentationData) {
    const keywords = [];
        for (let keyword of expeData.keywords.keys()){
            const isKeywordChosen = expeData.keywords.get(keyword);
            if (isKeywordChosen){
                keywords.push(keyword)
            }
        }
    
    const oldPedagogyEvaluations =  {
                                        initialEvaluation: expeData.initialEvaluationOld,
                                        immediateEvaluation: expeData.immediateEvaluationOld,
                                        delayedEvaluation: expeData.delayedEvaluationOld,
                                        accountedEvaluation: expeData.accountedEvaluationOld
                                    }
    
    const newPedagogyEvaluations =  {
                                        initialEvaluation: expeData.initialEvaluationNew,
                                        immediateEvaluation: expeData.immediateEvaluationNew,
                                        delayedEvaluation: expeData.delayedEvaluationNew,
                                        accountedEvaluation: expeData.accountedEvaluationNew
                                    }
    
    const pedagogicalContext =  {
                                    learningDifficulty: expeData.learningDifficulty,
                                    learningDifficultyOrigin: expeData.learningDifficultyOrigin,
                                    studyField: expeData.studyField,
                                    teachingTitle: expeData.teachingTitle,
                                    knowledges: expeData.knowledges,
                                    prerequisite: expeData.prerequisite,
                                    organisationParticularities: expeData.organisationParticularities,
                                    classesFrequencies: expeData.classesFrequencies,
                                    classesDates: expeData.classesDates,
                                    yearOfStudy: expeData.yearOfStudy,
                                    studentsSpecificities: expeData.studentsSpecificities,
                                    studentsNumber: expeData.studentsNumber,
                                    oldPedagogy: expeData.oldPedagogy,
                                    newPedagogy: expeData.newPedagogy,
                                    oldPedagogyEvaluations: oldPedagogyEvaluations,
                                    newPedagogyEvaluations: newPedagogyEvaluations
                                };

    const data = {experimentation: 
                     {keywords: keywords, 
                      personalKeywords: expeData.personalKeywords,
                      protocol: expeData.protocol, 
                      isSharingData: expeData.isSharingData, 
                      affiliationID: expeData.affiliation.id,
                      pedagogicalContext: pedagogicalContext
                     },
                 affiliationID: expeData.affiliation.id};
    return data;
}

async function sendPostRequest(data: any, setError:Dispatch<SetStateAction<Error|null>>, navigate: NavigateFunction){
    const response = await apiFetch("/expe/create", {
            method: "POST",
            headers:{
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify(data),
            credentials: "include"})
            .catch(error => {
                setError(new Error(error?.message || String(error)))
                throw error;
        });
     
    if (response.ok){
        const result = await response.json();
        navigate(`/experimentationSummary/${result.id}`);
    } else {
        setError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
    return response
}