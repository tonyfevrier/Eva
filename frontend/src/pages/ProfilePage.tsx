import { useEffect, useState, type Dispatch, type FormEvent, type SetStateAction } from "react";
import { useFetch } from "../hooks/useFetch"
import { Spinner } from "../components/Spinner";
import type { UpdateFormBoolean } from "../types/types";

export function ProfilePage(){
    /*   
    La suppression devra faire apparaître un popup de confirmation auquel cas on supprime et 
    redirige vers la page d'accueil
    */ 

    const [saveAppear, setSaveAppear] = useState<boolean>(false);
    const [updateFormData, setUpdateFormData] = useState<UpdateFormBoolean>({firstname: "", lastname: "",username: ""});
    const [updateError, setUpdateError] = useState<Error|null>(null);
    const {loading, data, error} = useFetch<any>("http://localhost:9000/auth/profile");
    
    /*MaJ des champs à réception des données*/
    useEffect(() => {
        if (data){
            setUpdateFormData({ firstname: data["firstname"], lastname: data["lastname"], username: data["mail"] })
        }
    }, [data]);

    const handleFormChange = (e:React.ChangeEvent<HTMLInputElement>) => {
        /*Prend en charge les changements de valeur des inputs */
        const {name, value} = e.target;
        setUpdateFormData((prev: any) => ({
            ...prev,
            [name]: value
        }));
    }

    const handleModifyClick = () => {
        setSaveAppear(true);
    }
    const handleSaveClick = () => {
        /*on regarde si aucun des éléments n'est vide, si oui on lance une requête */
        if (updateFormData.firstname !== "" && updateFormData.lastname !== ""){
            sendPutRequest(updateFormData, setUpdateError);
            setSaveAppear(false);
        }
        return;
    }

    const handleDeleteClick = () => {}

    if (error){
        return <>{error}</>
    }

    if (loading){
        return <Spinner/>
    }

    return <>
                <h1> Infos utilisateurs</h1>
                <form action="put">
                    <div>
                        <p>Prénom</p>
                        <input type="text" value={updateFormData.firstname} name="firstname" onChange={handleFormChange} disabled={!saveAppear}/>
                        {updateFormData.firstname === "" && <p> Ce champ doit être rempli </p>}
                    </div>
                    <div>
                        <p>Nom</p>
                        <input type="text" value={updateFormData.lastname} name="lastname" onChange={handleFormChange} disabled={!saveAppear}/>
                        {updateFormData.lastname === "" && <p> Ce champ doit être rempli </p>}
                    </div>
                    <div>
                        <p>Mail</p>
                        <input type="text" value={updateFormData.username} name="username" onChange={handleFormChange} disabled={true}/>
                        {updateFormData.username === "" && <p> Ce champ doit être rempli </p>}
                    </div>
                </form>
                
                <div className="profile-modify">
                    <button onClick={handleModifyClick} disabled={saveAppear}>Modifier</button>
                    <button onClick={handleSaveClick} disabled={!saveAppear}>Sauvegarder</button>
                    <a href="" onClick={handleDeleteClick}>Supprimer l'utilisateur</a>
                </div>
                {updateError && <p>{updateError.message}</p> }
           </>
}

function sendPutRequest(updateFormData:UpdateFormBoolean, setUpdateError:Dispatch<SetStateAction<Error|null>>){
    fetch("http://localhost:9000/auth/update", {
            method: "put",
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                firstname: updateFormData.firstname,
                lastname: updateFormData.lastname,
            }),
            credentials: "include"  
        })
        .catch(error => setUpdateError(error));
}