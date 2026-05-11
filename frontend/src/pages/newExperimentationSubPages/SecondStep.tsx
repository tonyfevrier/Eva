import type { Dispatch, SetStateAction } from "react";
import { Select } from "../../components/Select"
import type { ExperimentationData } from "../NewExperimentationPage";
import { PostButton } from "../../components/PostButton";
import variantData from "../../data/variants.json";
import styles from "./SecondStep.module.css";
import type React from "react";

type StepState = {
    state: ExperimentationData;
    setState: Dispatch<SetStateAction<ExperimentationData>>;
}

export function SecondStep({state, setState}:StepState){

    const chooseVariant = (e:React.MouseEvent<HTMLInputElement>) => {
        /*On récupère le nom de la variante
        on le met dans protocol
        on change le style du bouton correspondant et on remet celui des autres au style normal */
        e.stopPropagation();
        if (e.currentTarget.dataset.key){
            setState({...state, protocol: e.currentTarget.dataset.key});
        }
    };

    return <div>
                <h4 className={styles.h4}>Pour évaluer votre pratique, nous vous donnons le choix entre les protocoles suivants</h4>
                
                <div className={styles.container}>
                    {variantData.map((variant, index) => <PostButton key={index} title={variant.title} text={variant.text} notices={variant.notices} onClick={chooseVariant} protocol={state.protocol}/>)}
                </div>
            </div>
}


