import { Children, useState, type PropsWithChildren } from "react"
import { NavBar } from "./NavBar";
import { Button } from "./Button"; 

interface MultiProps extends PropsWithChildren{
    clickableSteps: Map<string, boolean>
    onLastClick: () => void
}

export function MultiStep({clickableSteps, children, onLastClick}:MultiProps){
    /* Composant permettant de parcourir une série de pages et de revenir à souhait en arrière.
    Il doit y avoir le même nombre de children que de titre dans clickableSteps car 
    ils correspondent au nombre d'étapes, clickableSteps contient pour chaque titre l'information
    de si oui ou non le bouton est cliquable */
    const [step, setStep] = useState<number>(1);

    const clickableStepsKeys =  Array.from(clickableSteps.keys());
    const isOnTheLastStep = step === clickableStepsKeys.length;
    const nextStep = Math.min(clickableStepsKeys.length, step + 1);

    const handleNextStepClick = () => {
        setStep(nextStep);
        if (isOnTheLastStep){
            onLastClick();
        }
    }
    return  <>
                <NavBar variant="primary">
                    {clickableStepsKeys.map(title => {
                        return <Button key={title} onClick={()=>{setStep(clickableStepsKeys.indexOf(title)+1)}} disabled={!clickableSteps.get(title)}>{title}</Button>
                    })}
                </NavBar>
                {Children.map(children, (child, index) => {
                    if (index + 1 === step){
                        return child;
                    }
                    return null;
                })}
                <div>
                    <Button style={{"margin" : "1em"}} onClick={()=>setStep(c=>c-1)} disabled={step===1}>Précédent</Button>
                    <Button style={{"margin" : "1em"}} onClick={handleNextStepClick} disabled={!clickableSteps.get(clickableStepsKeys[step-1])}>{isOnTheLastStep?"Sauver l'expérimentation":"Suivant"}</Button>
                </div>
            </>
}