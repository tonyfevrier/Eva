import { useState, type Dispatch, type SetStateAction } from "react";
import type { ExperimentationData } from "../NewExperimentationPage";
import { Input } from "../../components/Input";
import { Textarea } from "../../components/Textarea";
import { LabeledFilteredInput } from "../../components/LabeledFilteredInput";
import preRegisteredData from "../../data/preRegisteredData.json";
import { Button } from "../../components/Button";
import { ModalFilteredSelector } from "../../components/ModalFilteredSelector";
import styles from "./ThirdStep.module.css"
import { Spinner } from "../../components/Spinner";
import { useFetch } from "../../hooks/useFetch";
import { Goto } from "../../components/Goto";

type StepState = {
    state: ExperimentationData;
    setState: Dispatch<SetStateAction<ExperimentationData>>;
}

export function ThirdStep({state, setState}:StepState){
    const [isModalOpen, setIsModalOpen] = useState(false);
    const {loading, data, error} = useFetch<{institutions: Array<Record<string, any>>}>("/institution/getAll");
    
    if (loading){
        return <Spinner/>
    }
    
    if (error){
        return <p>{error.message}</p>
    }
    
    if (data){
    
        const handleChooseAffiliation = (e: React.MouseEvent<HTMLButtonElement>) => {
            setState({...state, affiliation:{id:e.currentTarget.id, name:e.currentTarget.innerText}});
            setIsModalOpen(false);
        }
    

            return <div>
                <h4 style={{"margin" : "1em 0"}}>Veuillez entrer quelques précisions sur votre contexte pédagogique</h4>
                <div className={styles.container}>
                    <p>Choisissez votre affiliation</p>
                    {state.affiliation.name !== "" && <p className={styles.institution}>{state.affiliation.name}</p>}
                    <Button className={styles.button} onClick={() => setIsModalOpen(true)}> {state.affiliation.name !== ""?"Modifiez votre choix":"Cliquez pour choisir"}</Button>
                </div>  
                {isModalOpen && <ModalFilteredSelector title="Choisissez votre affiliation" items={data["institutions"]} onClick={handleChooseAffiliation} setIsModalOpen={setIsModalOpen}/>}
                <Goto variant="export" href="/application/institution" label="Si vous n'avez pas trouvé votre établissement" buttonLabel="Créez un établissement"/>
                <LabeledFilteredInput title="Discipline enseignée" listTitle="De préférence, choisissez parmi" items={preRegisteredData["studyField"]} variant="withErrorMsg" value={state.studyField} onChange={e => {setState({...state, studyField: e.target.value})}}/>
                <Input title="Intitulé ou thème de l'enseignement (des enseignements pour la variante enseignement)" variant="withErrorMsg" value={state.teachingTitle} onChange={e => {setState({...state, teachingTitle: e.target.value})}}/>
                <Textarea title="Connaissances et/ou compétences à acquérir durant l'enseignement (des enseignements pour la variante enseignement)" variant="withErrorMsg" value={state.knowledges} onChange={e => {setState({...state, knowledges: e.target.value})}}/>
                <Textarea title="Prérequis de l'enseignement (des enseignements pour la variante enseignement)" variant="withErrorMsg" value={state.prerequisite} onChange={e => {setState({...state, prerequisite: e.target.value})}}/>
                <Textarea title="Particularités de l'organisation du cours ou de la salle" variant="withErrorMsg" value={state.organisationParticularities} onChange={e => {setState({...state, organisationParticularities: e.target.value})}}/>
                <Textarea title="Nombre, durée, horaire et fréquence des cours" variant="withErrorMsg" value={state.classesFrequencies} onChange={e => {setState({...state, classesFrequencies: e.target.value})}}/>
                <Textarea title="Date(s) de l'enseignement" variant="withErrorMsg" value={state.classesDates} onChange={e => {setState({...state, classesDates: e.target.value})}}/>
                <LabeledFilteredInput title="Année d'étude ou profession des apprenants" listTitle="De préférence, choisissez parmi" items={preRegisteredData["yearOfStudy"]} variant="withErrorMsg" value={state.yearOfStudy} onChange={e => {setState({...state, yearOfStudy: e.target.value})}}/>
                <Textarea title="Particularités des apprenants" variant="withErrorMsg" value={state.studentsSpecificities} onChange={e => {setState({...state,studentsSpecificities: e.target.value})}}/>
                <Input title="Nombre d'apprenants (des deux groupes pour les variantes groupe et année)" variant="withErrorMsg" value={state.studentsNumber} onChange={e => {setState({...state, studentsNumber: e.target.value})}}/>
            </div> 
        }
}
 