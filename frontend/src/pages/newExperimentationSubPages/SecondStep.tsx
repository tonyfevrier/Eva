import type { Dispatch, SetStateAction } from "react";
import { Select } from "../../components/Select"
import type { ExperimentationData } from "../NewExperimentationPage";

type StepState = {
    state: ExperimentationData;
    setState: Dispatch<SetStateAction<ExperimentationData>>;
}

export function SecondStep({state, setState}:StepState){
    return <div>
                <h2>Pour évaluer votre pratique, nous vous donnons le choix entre les protocoles suivants</h2>
                <p>blablabbffbfhsfjhsfkfhs</p>
                <Select title="Quel protocole choisissez-vous pour votre évaluation" value={state.protocol} onChange={(e) => {setState({...state, protocol:e.target.value})}}>
                    <option value="">--Please choose an option--</option>
                    <option value="2">1</option>
                    <option value="3">1</option>
                    <option value="4">1</option>
                </Select>
            </div>
}


