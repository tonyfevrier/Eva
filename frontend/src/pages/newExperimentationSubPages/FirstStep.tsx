import { useState, type Dispatch, type SetStateAction } from "react"
import { Cloud } from "../../components/Cloud"
import { Input } from "../../components/Input"
import { Textarea } from "../../components/Textarea"
import type { ExperimentationData } from "../NewExperimentationPage"
import { useFetch } from "../../hooks/useFetch"
import { Spinner } from "../../components/Spinner"
import { Goto } from "../../components/Goto"
import { ModalFilteredSelector } from "../../components/ModalFilteredSelector"
import { Button } from "../../components/Button"
import styles from "./FirstStep.module.css"

type FirstStepState = {
    state: ExperimentationData;
    setState: Dispatch<SetStateAction<ExperimentationData>>;
    handleClickOnCloud: (e:React.MouseEvent<HTMLButtonElement>)=>void;
}

export function FirstStep({state, setState, handleClickOnCloud}:FirstStepState){
    const {loading, data, error} = useFetch<{institutions: Array<Record<string, any>>}>("http://localhost:9000/institution/getAll");
    const [isModalOpen, setIsModalOpen] = useState(false);

    const handleChooseAffiliation = (e: React.MouseEvent<HTMLButtonElement>) => {
        setState({...state, affiliation:{id:e.currentTarget.id, name:e.currentTarget.innerText}});
        setIsModalOpen(false);
    }

    if (loading){
        return <Spinner/>
    }

    if (error){
        return <p>{error.message}</p>
    }

    if (data){

        return  <div>
                    <div className={styles.container}>
                        <p>Choisissez votre affiliation</p>
                        {state.affiliation.name !== "" && <p className={styles.institution}>{state.affiliation.name}</p>}
                        <Button onClick={() => setIsModalOpen(true)}> {state.affiliation.name !== ""?"Modifiez votre choix":"Cliquez pour choisir"}</Button>
                    </div>  
                    {isModalOpen && <ModalFilteredSelector title="Choisissez votre affiliation" items={data["institutions"]} onClick={handleChooseAffiliation} setIsModalOpen={setIsModalOpen}/>}
                    <Goto href="/application/institution" label="Si vous n'avez pas trouvé votre établissement" buttonLabel="Créez-le ici"/>
                    <Cloud title="Choisissez éventuellement des mots clés" options={state.keywords} onClick={handleClickOnCloud}/>
                    <Input title="Autres mots clés personnalisés" value={state.personalKeywords} onChange={e => {setState({...state, personalKeywords: e.target.value})}}/>
                    <Textarea title="Ancienne pédagogie" variant="withErrorMsg" value={state.oldPedagogy} onChange={e => {setState({...state, oldPedagogy: e.target.value})}}/>
                    <Textarea title="Nouvelle pédagogie" variant="withErrorMsg" value={state.newPedagogy} onChange={e => {setState({...state, newPedagogy: e.target.value})}}/>
                    <Textarea title="Quelle difficulté d'apprentissage cette nouvelle pédagogie est-il supposé résoudre?" variant="withErrorMsg" value={state.learningDifficulty} onChange={e => {setState({...state, learningDifficulty: e.target.value})}}/>
                    <Textarea title="Cette difficulté étant probablement multimodale, quelle cause de cette difficulté est particulièrement visée par votre nouvelle pédagogie?" variant="withErrorMsg" value={state.learningDifficultyOrigin} onChange={e => {setState({...state, learningDifficultyOrigin: e.target.value})}}/>
                </div>          
    }
}