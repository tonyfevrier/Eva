import { Spinner } from "../components/Spinner";
import { useFetch } from "../hooks/useFetch";
import { ExperimentationPostButton } from "../components/ExperimentationPostButton";
import styles from "./ExperimentationListPage.module.css"
import { Input } from "../components/Input";
import { useState } from "react";
import { Database } from "../components/Database";
import { Button } from "../components/Button";
import { Select } from "../components/Select";

export type Data = {
    id: string,
    institutionName: string,
    yearOfStudy: string,
    teachingTitle: string,
    keywords: Array<string>,
    personalKeywords: string,
    inProgress: boolean,
    studyField: string,
    expeWorked?: string,
    newPedagogy?: string
}

export function ExperimentationListPage({isUserExpeList=true}:{isUserExpeList?: boolean}){
    const [filterState, setFilterState] = useState({keyword: "", institution: "", studyField: "",
                                                    yearOfStudy: "", expeWorked:"", newPedagogy:""});
    const endpoint = isUserExpeList? "getAllOfOneUser": "getAll";
    const credentials = isUserExpeList? 'include': undefined;
    const {loading, data, error} = useFetch<Array<Data>>(`http://localhost:9000/expe/${endpoint}`, credentials);

    if (loading){
        return <Spinner/>
    }

    if (error){
        return <p>{error.message}</p>
    }

    if (data){
        const filteredExpes = data.filter(expe => {
            const institutionIncludesInput = expe.institutionName.toLowerCase().includes(filterState.institution.trim().toLowerCase());
            const studyFieldIncludesInput = expe.studyField.toLowerCase().includes(filterState.studyField.trim().toLowerCase());
            const keywordIncludesInput = expe.keywords.some(keyword => keyword.toLowerCase().includes(filterState.keyword.trim().toLowerCase()));
            const isAFilteredExperimentation = institutionIncludesInput && keywordIncludesInput && studyFieldIncludesInput;
            return isAFilteredExperimentation && expe;
        });
        if (isUserExpeList){
            return  <>
                        <Input className={styles.filter} title="Filtrer par discipline" value={filterState.studyField}  onChange={(e) => {setFilterState({...filterState, studyField: e.target.value})}}/>
                        <Input className={styles.filter} title="Filtrer par mot-clé" value={filterState.keyword} onChange={(e) => {setFilterState({...filterState, keyword: e.target.value})}}/>
                        <Input className={styles.filter} title="Filtrer par institution" value={filterState.institution} onChange={(e) => {setFilterState({...filterState, institution: e.target.value})}}/>
                        <div className={styles.container}>
                            {filteredExpes.map(expe => <ExperimentationPostButton key={expe.id} data={expe}/>)};
                        </div>
                    </>
        } else {
            const filteredExpesInProgress = filteredExpes.filter(expe => {
                const yearOfStudyIncludesInput = expe.yearOfStudy.toLowerCase().includes(filterState.yearOfStudy.trim().toLowerCase());
                const newPedagogyIncludesInput = expe.newPedagogy !== undefined? expe.newPedagogy.toLowerCase().includes(filterState.newPedagogy.trim().toLowerCase()):true;
                const keepExpeIndependentlyOfSuccess = filterState.expeWorked === "";
                const userSuccessChoiceEqualsExpeSuccess = filterState.expeWorked == expe.expeWorked;
                const isAFilteredExperimentation = ( keepExpeIndependentlyOfSuccess || userSuccessChoiceEqualsExpeSuccess) && !expe.inProgress && yearOfStudyIncludesInput && newPedagogyIncludesInput;
                return isAFilteredExperimentation && expe; 
            });
            return  <>
                        <Input className={styles.filter} title="Filtrer par discipline" value={filterState.studyField}  onChange={(e) => {setFilterState({...filterState, studyField: e.target.value})}}/>
                        <Input className={styles.filter} title="Filtrer par mot-clé" value={filterState.keyword} onChange={(e) => {setFilterState({...filterState, keyword: e.target.value})}}/>
                        <Input className={styles.filter} title="Filtrer par institution" value={filterState.institution} onChange={(e) => {setFilterState({...filterState, institution: e.target.value})}}/>
                        <Input className={styles.filter} title="Filtrer par année scolaire" value={filterState.yearOfStudy} onChange={(e) => {setFilterState({...filterState, yearOfStudy: e.target.value})}}/>
                        <Input className={styles.filter} title="Filtrer par pédagogie" value={filterState.newPedagogy} onChange={(e) => {setFilterState({...filterState, newPedagogy: e.target.value})}}/>
                        <Select className={styles.filterSelect} value={filterState.expeWorked} onChange={(e) => {setFilterState({...filterState, expeWorked: e.target.value})}} title="Filtrer par succès">
                            <option value=""> </option>
                            <option value="Oui"> L'expérimentation a fonctionné </option>
                            <option value="Non"> L'expérimentation n'a pas fonctionné </option>
                        </Select>
                        <>
                            <Database experimentations={filteredExpesInProgress}/>
                            <Button>Télécharger les expérimentations filtrées</Button>
                        </>
                    </>
        }
    }
}