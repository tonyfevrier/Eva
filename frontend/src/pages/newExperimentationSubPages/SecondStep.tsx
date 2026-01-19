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
                    <option value="Variante années">Variante années</option>
                    <option value="Variante groupes">Variante groupes</option>
                    <option value="Variante enseignements">Variante enseignements</option>
                </Select>
            </div>
}


