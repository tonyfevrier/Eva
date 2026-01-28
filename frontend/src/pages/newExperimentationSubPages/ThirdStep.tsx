import type { Dispatch, SetStateAction } from "react";
import type { ExperimentationData } from "../NewExperimentationPage";
import { Input } from "../../components/Input";
import { Textarea } from "../../components/Textarea";

type StepState = {
    state: ExperimentationData;
    setState: Dispatch<SetStateAction<ExperimentationData>>;
}

export function ThirdStep({state, setState}:StepState){
    return <div>
                <p>Veuillez entrer quelques précisions sur votre contexte pédagogique</p>
                <Input title="Discipline enseignée" variant="withErrorMsg" value={state.studyField} onChange={e => {setState({...state, studyField: e.target.value})}}/>
                <Input title="Intitulé ou thème de l'enseignement" variant="withErrorMsg" value={state.teachingTitle} onChange={e => {setState({...state, teachingTitle: e.target.value})}}/>
                <Textarea title="Connaissances et/ou compétences à acquérir par les apprenants" variant="withErrorMsg" value={state.knowledges} onChange={e => {setState({...state, knowledges: e.target.value})}}/>
                <Textarea title="Prérequis" variant="withErrorMsg" value={state.prerequisite} onChange={e => {setState({...state, prerequisite: e.target.value})}}/>
                <Textarea title="Particularités de l'organisation du cours ou de la salle" variant="withErrorMsg" value={state.organisationParticularities} onChange={e => {setState({...state, organisationParticularities: e.target.value})}}/>
                <Textarea title="Nombre, durée, horaire et fréquence des cours" variant="withErrorMsg" value={state.classesFrequencies} onChange={e => {setState({...state, classesFrequencies: e.target.value})}}/>
                <Textarea title="Date(s) de l'enseignement" variant="withErrorMsg" value={state.classesDates} onChange={e => {setState({...state, classesDates: e.target.value})}}/>
                <Input title="Année d'étude ou profession des apprenants" variant="withErrorMsg" value={state.yearOfStudy} onChange={e => {setState({...state, yearOfStudy: e.target.value})}}/>
                <Textarea title="Particularités des apprenants" variant="withErrorMsg" value={state.studentsSpecificities} onChange={e => {setState({...state,studentsSpecificities: e.target.value})}}/>
                <Input title="Nombre d'apprenants" variant="withErrorMsg" value={state.studentsNumber} onChange={e => {setState({...state, studentsNumber: e.target.value})}}/>
            </div> 
}
 