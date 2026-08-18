import { type Dispatch, type SetStateAction } from "react"
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
    return  <div>
                <Cloud title="Choisissez éventuellement des mots clés" options={state.keywords} onClick={handleClickOnCloud}/>
                <Input title="Autres mots clés personnalisés" value={state.personalKeywords} onChange={e => {setState({...state, personalKeywords: e.target.value})}}/>
                <Textarea title="Pratique pédagogique habituelle" variant="withErrorMsg" value={state.oldPedagogy} onChange={e => {setState({...state, oldPedagogy: e.target.value})}}/>
                <Textarea title="Nouvelle pratique pédagogique" variant="withErrorMsg" value={state.newPedagogy} onChange={e => {setState({...state, newPedagogy: e.target.value})}}/>
                <Textarea title="Quelle difficulté d'apprentissage cette nouvelle pédagogie est-il supposé résoudre?" variant="withErrorMsg" value={state.learningDifficulty} onChange={e => {setState({...state, learningDifficulty: e.target.value})}}/>
                <Textarea title="Cette difficulté étant probablement multimodale, quelle cause de cette difficulté est particulièrement visée par votre nouvelle pédagogie?" variant="withErrorMsg" value={state.learningDifficultyOrigin} onChange={e => {setState({...state, learningDifficultyOrigin: e.target.value})}}/>
            </div>          
    
}