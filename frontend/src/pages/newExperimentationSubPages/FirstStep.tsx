import type { Dispatch, SetStateAction } from "react"
import { Cloud } from "../../components/Cloud"
import { Input } from "../../components/Input"
import { Textarea } from "../../components/Textarea"
import type { ExperimentationData } from "../NewExperimentationPage"
import { useFetch } from "../../hooks/useFetch"
import { Spinner } from "../../components/Spinner"
import { Datalist } from "../../components/Datalist"

type FirstStepState = {
    state: ExperimentationData;
    setState: Dispatch<SetStateAction<ExperimentationData>>;
    handleClickOnCloud: (e:React.MouseEvent<HTMLButtonElement>)=>void;
}

export function FirstStep({state, setState, handleClickOnCloud}:FirstStepState){
    const {loading, data, error} = useFetch<{institutions: Array<Record<string, any>>}>("http://localhost:9000/institution/getAll");
    
    if (loading){
        return <Spinner/>
    }

    if (error){
        return <p>{error.message}</p>
    }

    if (data){

        return <div>
                <Datalist title="Etablissement de l'expérimentation" value={state.affiliation.name} onChange={e => setState({...state, affiliation:{id:e.target.id, name:e.target.value}})}>
                    {data["institutions"].map(institution => (
                        <option id={institution.id} key={institution.id} value={institution.name} />
                    ))}
                </Datalist>
                <Cloud title="Choisissez éventuellement des mots clés" options={state.keywords} onClick={handleClickOnCloud}/>
                <Input title="Autres mots clés personnalisés" value={state.personalKeywords} onChange={e => {setState({...state, personalKeywords: e.target.value})}}/>
                <Textarea title="Ancienne pédagogie" variant="withErrorMsg" value={state.oldPedagogy} onChange={e => {setState({...state, oldPedagogy: e.target.value})}}/>
                <Textarea title="Nouvelle pédagogie" variant="withErrorMsg" value={state.newPedagogy} onChange={e => {setState({...state, newPedagogy: e.target.value})}}/>
                <Textarea title="Quelle difficulté d'apprentissage cette nouvelle pédagogie est-il supposé résoudre?" variant="withErrorMsg" value={state.learningDifficulty} onChange={e => {setState({...state, learningDifficulty: e.target.value})}}/>
                <Textarea title="Cette difficulté étant probablement multimodale, quelle cause de cette difficulté est particulièrement visée par votre nouvelle pédagogie?" variant="withErrorMsg" value={state.learningDifficultyOrigin} onChange={e => {setState({...state, learningDifficultyOrigin: e.target.value})}}/>
            </div>          
    }
}