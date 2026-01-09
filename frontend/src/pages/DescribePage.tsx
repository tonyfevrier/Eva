import { Input } from "../components/Input";
import { Button } from "../components/Button";
import { useState, type Dispatch, type SetStateAction } from "react";
import { useNavigate, type NavigateFunction } from "react-router-dom";
import { useTheme } from "../hooks/useTheme";

type DescribeFormData = {
    affiliation: string,
    street: string,
    postcode: string,
    town: string,
    phone: string,
    acceptMap: boolean,
    acceptContact: boolean,
}

export function DescribePage(){
    /*
    Etats nécessaires : 
    texte des input textes
    Etat de valeur des deux checkbox

    */
    const {setIsProfileCompleted} = useTheme();
    const initialformData = {affiliation: "", street: "",  postcode: "", town: "",
                             phone: "", acceptMap: false, acceptContact: false};
    const [formData, setFormData] = useState<DescribeFormData>(initialformData);
    const [fetchError, setFetchError] = useState<Error|null>(null);
    const navigate = useNavigate();

    const handleSubmit = (e:React.FormEvent<HTMLFormElement>) => {
        e.preventDefault(); 
        const data = {affiliation: formData.affiliation, street: formData.street,  
                postcode: formData.postcode, town: formData.town,
                phone: formData.phone, acceptMap: formData.acceptMap, 
                acceptContact: formData.acceptContact}
        sendPostRequest(data, setFetchError, navigate, setIsProfileCompleted);
    }

    return <>
                <h1>Te décrire</h1>
                <p> Votre inscription a bien été réalisée. 
                    Il vous reste quelques informations de profils à compléter avant de pouvoir accéder à l'application.
                </p>
                <form onSubmit={handleSubmit}>
                    <Input title="Affiliation" name="affiliation" type="text" value={formData.affiliation} onChange={(e)=>{setFormData({...formData, affiliation: e.target.value})}}/>
                    <Input title="Etes-vous d'accord pour que votre localisation apparaisse sur une carte?" name="card-accept" type="checkbox" onChange={() => setFormData({...formData, acceptMap: !formData.acceptMap})}/>
                    <Input title="Rue" name="rue" type="text" value={formData.street} onChange={(e)=>{setFormData({...formData, street: e.target.value})}} disabled={!formData.acceptMap}/>
                    <Input title="Code postal" name="postcode" type="text" value={formData.postcode} onChange={(e)=>{setFormData({...formData, postcode: e.target.value})}} disabled={!formData.acceptMap}/>
                    <Input title="Ville" name="ville" type="text" value={formData.town} onChange={(e)=>{setFormData({...formData, town: e.target.value})}} disabled={!formData.acceptMap}/>
                    <Input title="Etes-vous d'accord pour que d'autres enseignants puissent vous contacter par email? Si oui vous pourrez rentrer votre numéro de téléphone" name="card-accept" type="checkbox" onChange={() => setFormData({...formData, acceptContact: !formData.acceptContact})}/>
                    <Input title="Téléphone (facultatif)" name="téléphone" type="tel" value={formData.phone} onChange={(e)=>{setFormData({...formData, phone: e.target.value})}} disabled={!formData.acceptContact}/>
                    <Button disabled={formData.affiliation === ""}>Sauvegarder les informations</Button>
                    {fetchError?.message && <p>{fetchError?.message}</p>}
                </form>
           </>
}


async function sendPostRequest(data: DescribeFormData, setFetchError:Dispatch<SetStateAction<Error|null>>, navigate: NavigateFunction, setIsProfileCompleted:Dispatch<SetStateAction<boolean>>){
    const response = await fetch("http://localhost:9000/user/addData", {
            method: "POST",
            headers:{
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify(data),
            credentials: "include"})
            .catch(error => {
                setFetchError(new Error(error.getMessage()))
                throw error;
        });
     
    console.log(response.ok)
    if (response.ok){
        setIsProfileCompleted(true);
        navigate("/");    
    } else {
        setFetchError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
    return response
}