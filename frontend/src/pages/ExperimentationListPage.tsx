import { Spinner } from "../components/Spinner";
import { useFetch } from "../hooks/useFetch";
import { ExperimentationPostButton } from "../components/ExperimentationPostButton";
import styles from "./ExperimentationListPage.module.css"
import { Input } from "../components/Input";
import { useState } from "react";
import { Database } from "../components/Database";
import { Button } from "../components/Button";

export type Data = {
    id: string,
    institutionName: string,
    yearOfStudy: string,
    teachingTitle: string,
    keywords: Array<string>,
    personalKeywords: string,
    inProgress: boolean,
    studyField: string,
    expeWorked?: boolean,
    newPedagogy?: string
}

export function ExperimentationListPage({isUserExpeList=true}:{isUserExpeList?: boolean}){
    const [filterState, setFilterState] = useState({keyword: "", institution: "", studyField: ""});
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
        const filteredExpes = data.map(expe => {
            const institutionIncludesInput = expe.institutionName.toLowerCase().includes(filterState.institution.trim().toLowerCase());
            const studyFieldIncludesInput = expe.studyField.toLowerCase().includes(filterState.studyField.trim().toLowerCase());
            const keywordIncludesInput = expe.keywords.some(keyword => keyword.toLowerCase().includes(filterState.keyword.trim().toLowerCase()));
            const isAFilteredExperimentation = institutionIncludesInput && keywordIncludesInput && studyFieldIncludesInput;
            if (isAFilteredExperimentation){
                return expe;
            }
        });
        return  <>
                    <Input title="Filtrer par discipline" value={filterState.studyField}  onChange={(e) => {setFilterState({...filterState, studyField: e.target.value})}}/>
                    <Input title="Filtrer par mot-clé" value={filterState.keyword} onChange={(e) => {setFilterState({...filterState, keyword: e.target.value})}}/>
                    <Input title="Filtrer par institution" value={filterState.institution} onChange={(e) => {setFilterState({...filterState, institution: e.target.value})}}/>
                    {isUserExpeList && <div className={styles.container}>
                        {filteredExpes.map(expe => expe !== undefined && <ExperimentationPostButton key={expe.id} data={expe}/>)};
                    </div>}
                    {!isUserExpeList &&  <>
                        <Database experimentations={filteredExpes}/>
                        <Button>Télécharger les expérimentations filtrées</Button>
                    </>}
                </>
    }
}