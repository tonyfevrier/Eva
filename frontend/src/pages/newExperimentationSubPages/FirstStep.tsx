import type { Dispatch, SetStateAction } from "react"
import { Cloud } from "../../components/Cloud"
import { Input } from "../../components/Input"
import { Textarea } from "../../components/Textarea"
import type { ExperimentationData } from "../NewExperimentationPage"

type FirstStepState = {
    state: ExperimentationData;
    setState: Dispatch<SetStateAction<ExperimentationData>>;
    handleClickOnCloud: (e:React.MouseEvent<HTMLButtonElement>)=>void;
}

export function FirstStep({state, setState, handleClickOnCloud}:FirstStepState){
    return <div>
                <Cloud title="Choisissez éventuellement des mots clés" options={state.keywords} onClick={handleClickOnCloud}/>
                <Input title="Autres mots clés personnalisés" value={state.personalKeywords} onChange={e => {setState({...state, personalKeywords: e.target.value})}}/>
                <Textarea title="Problème rencontré en classe" variant="withErrorMsg" value={state.problem} onChange={e => {setState({...state, problem: e.target.value})}}/>
                <Textarea title="Ancienne pédagogie" variant="withErrorMsg" value={state.oldPedagogy} onChange={e => {setState({...state, oldPedagogy: e.target.value})}}/>
                <Textarea title="Nouvelle pédagogie" variant="withErrorMsg" value={state.newPedagogy} onChange={e => {setState({...state, newPedagogy: e.target.value})}}/>
            </div>
}