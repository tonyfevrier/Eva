import { type Dispatch, type SetStateAction } from "react";
import { FilteredSelector } from "../components/FilteredSelector"
import { Spinner } from "../components/Spinner";
import { useFetch } from "../hooks/useFetch";
import type { InstitutionSelectionData } from "./InstitutionPage";

type PageData = {
    setData: Dispatch<SetStateAction<InstitutionSelectionData>>
}

export function InstitutionSelectionPage({setData}:PageData){
    const {loading, data, error} = useFetch<{institutions: Array<Record<string, any>>}>("http://localhost:9000/institution/getAll");

    const handleChooseAffiliation = (e: React.MouseEvent<HTMLButtonElement>) => {
        setData({affiliationId: e.currentTarget.id});
    }
    
    if (loading){
        return <Spinner/>
    }
    
    if (error){
        return <p>{error.message}</p>
    }
    
    if (data){
        return <FilteredSelector items={data["institutions"]} onClick={handleChooseAffiliation}/>
    }
}