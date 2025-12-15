import { useEffect, useState, type FormEvent } from "react";
import { useFetch } from "../hooks/useFetch"
import { Spinner } from "../components/Spinner";
import type { UpdateFormBoolean } from "../types/types";

export function ProfilePage(){
    /* Faire une requête à la bonne adresse spring, récupérer tous les champs de user 
    boucler sur ces champs pour les afficher  ou simplement les indiquer
    Le bouton sauvegarder ne devra apparaître que si on a cliqué sur modifier le profil
    : faire un état pour l'affichage
    La suppression devra faire apparaître un popup de confirmation auquel cas on supprime et 
    redirige vers la page d'accueil
    */ 

    const [saveAppear, setSaveAppear] = useState<boolean>(false);
    const [updateFormData, setUpdateFormData] = useState<UpdateFormBoolean>({firstname: "", lastname: "",username: ""});

    const {loading, data, error} = useFetch<any>("http://localhost:9000/auth/profile");
    
    useEffect(() => {
        if (data){
            setUpdateFormData({ firstname: data["firstname"], lastname: data["lastname"], username: data["mail"] })
        }
    }, [data]);

    const handleFormChange = (e:React.ChangeEvent<HTMLInputElement>) => {
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
        
        setSaveAppear(false);
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
                    </div>
                    <div>
                        <p>Nom</p>
                        <input type="text" value={updateFormData.lastname} name="lastname" onChange={handleFormChange} disabled={!saveAppear}/>
                    </div>
                    <div>
                        <p>Mail</p>
                        <input type="text" value={updateFormData.username} name="username" onChange={handleFormChange} disabled={!saveAppear}/>
                    </div>
                </form>
                
                <div className="profile-modify">
                    <button onClick={handleModifyClick} disabled={saveAppear}>Modifier</button>
                    <button onClick={handleSaveClick} disabled={!saveAppear}>Sauvegarder</button>
                    <a href="" onClick={handleDeleteClick}>Supprimer l'utilisateur</a>
                </div>
           </>
}