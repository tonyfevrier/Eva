import { useFetch } from "../hooks/useFetch";
import { useParams } from "react-router-dom";
import { Spinner } from "../components/Spinner";
import { Button } from "../components/Button";
 
export function ExperimentationProfilePage(){
    const {id} = useParams();
    const {loading, data, error} = useFetch<Record<string, any>>(`http://localhost:9000/experimentation/get/${id}`);

    if (loading){
        return <Spinner/>
    }

    if (error){
        return <p>{error?.message}</p>
    }

    if (data){
        return <>
                    <h1>Récapitulatif de l'expérimentation</h1>
                    {Object.entries(data).map(([key, value]) => <p key={key}>{value}</p>)}
                    <div>
                        <Button href="/modifyExpe/:id">Modifier l'expérimentation</Button>
                        <Button href="/application/expe">Confirmer l'expérimentation</Button>
                    </div>
               </>
    }
    
}

 