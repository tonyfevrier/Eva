import { Spinner } from "../components/Spinner";
import { useFetch } from "../hooks/useFetch";
import { ExperimentationPostButton, type Data } from "../components/ExperimentationPostButton";
import styles from "./ExperimentationListPage.module.css"

export function ExperimentationListPage({}){
    const {loading, data, error} = useFetch<Array<Data>>("http://localhost:9000/expe/getAllOfOneUser");

    if (loading){
        return <Spinner/>
    }

    if (error){
        return <p>{error.message}</p>
    }

    if (data){
        return <div className={styles.container}>
                    {data.map(expe => <ExperimentationPostButton key={expe.id} data={expe}/>)}
               </div>
    }
}