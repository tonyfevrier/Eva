import type { Dispatch, SetStateAction } from "react";
import type { ExperimentationData } from "../NewExperimentationPage";
import { Input } from "../../components/Input";

type StepState = {
    state: ExperimentationData;
    setState: Dispatch<SetStateAction<ExperimentationData>>;
}

export function FourthStep({state, setState}:StepState){
    return <div>
                <div>
                    <h4 style={{"margin" : "1em 0"}}>Pour le moyen pédagogique habituel</h4>
                    <Input type="date" title="Date de l'évaluation initiale" variant="withErrorMsg" value={state.initialEvaluationOld} onChange={e => {setState({...state, initialEvaluationOld: e.target.value})}}/>
                    <Input type="date" title="Date de l'évaluation immédiate" variant="withErrorMsg" value={state.immediateEvaluationOld} onChange={e => {setState({...state, immediateEvaluationOld: e.target.value})}}/>
                    <Input type="date" title="Date de l'évaluation différée" variant="withErrorMsg" value={state.delayedEvaluationOld} onChange={e => {setState({...state, delayedEvaluationOld: e.target.value})}}/>
                    <Input type="date" title="Date de l'évaluation comptabilisée" value={state.accountedEvaluationOld !== null?state.accountedEvaluationOld:""} onChange={e => {setState({...state, accountedEvaluationOld: e.target.value})}}/>
                </div>
                <div>
                    <h4 style={{"margin" : "1em 0"}}>Pour le nouveau moyen pédagogique</h4>
                    <Input type="date" title="Date de l'évaluation initiale" variant="withErrorMsg" value={state.initialEvaluationNew} onChange={e => {setState({...state, initialEvaluationNew: e.target.value})}}/>
                    <Input type="date" title="Date de l'évaluation immédiate" variant="withErrorMsg" value={state.immediateEvaluationNew} onChange={e => {setState({...state, immediateEvaluationNew: e.target.value})}}/>
                    <Input type="date" title="Date de l'évaluation différée" variant="withErrorMsg" value={state.delayedEvaluationNew} onChange={e => {setState({...state, delayedEvaluationNew: e.target.value})}}/>
                    <Input type="date" title="Date de l'évaluation comptabilisée" value={state.accountedEvaluationNew !== null?state.accountedEvaluationNew:""} onChange={e => {setState({...state, accountedEvaluationNew: e.target.value})}}/>
                </div>
                <Input type="checkbox" title="Acceptez-vous le partage des données de votre expérimentation?" checked={state.isSharingData} onChange={e => {setState({...state, isSharingData: e.target.checked})}}/>
            </div> 
}
 



