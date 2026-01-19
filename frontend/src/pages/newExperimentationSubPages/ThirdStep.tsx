import type { Dispatch, SetStateAction } from "react";
import type { ExperimentationData } from "../NewExperimentationPage";
import { Input } from "../../components/Input";
import { Textarea } from "../../components/Textarea";

type StepState = {
    state: ExperimentationData;
    setState: Dispatch<SetStateAction<ExperimentationData>>;
}

export function ThirdStep({state, setState}:StepState){
    return <div>
                <p>Veuillez entrer quelques précisions sur votre contexte pédagogique</p>
                <Input title="Affiliation" variant="withErrorMsg" value={state.affiliation} onChange={e => {setState({...state, affiliation: e.target.value})}}/>
                <Input title="Classe" variant="withErrorMsg" value={state.classroom} onChange={e => {setState({...state, classroom: e.target.value})}}/>
                <Textarea title="Veuillez décrire les différences entre vos deux groupes" variant="withErrorMsg" value={state.groupsDescription} onChange={e => {setState({...state, groupsDescription: e.target.value})}}/>
            </div> 
}


