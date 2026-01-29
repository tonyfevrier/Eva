import type { Dispatch, SetStateAction } from "react";
import { Select } from "../../components/Select"
import type { ExperimentationData } from "../NewExperimentationPage";
import { PostButton } from "../../components/PostButton";
import variantData from "../../data/variants.json";
import styles from "./SecondStep.module.css";

type StepState = {
    state: ExperimentationData;
    setState: Dispatch<SetStateAction<ExperimentationData>>;
}

export function SecondStep({state, setState}:StepState){
    
    return <div>
                <h2>Pour évaluer votre pratique, nous vous donnons le choix entre les protocoles suivants</h2>
                
                <div className={styles.container}>
                    {variantData.map((variant, index) => <PostButton key={index} title={variant.title} text={variant.text} notices={variant.notices} />)}
                </div>
                <Select title="Quel protocole choisissez-vous pour votre évaluation" value={state.protocol} onChange={(e) => {setState({...state, protocol:e.target.value})}}>
                    <option value="">--Please choose an option--</option>
                    <option value="Variante années">Variante années</option>
                    <option value="Variante groupes">Variante groupes</option>
                    <option value="Variante enseignements">Variante enseignements</option>
                </Select>
            </div>
}


