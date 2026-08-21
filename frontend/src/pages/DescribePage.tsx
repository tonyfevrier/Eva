import { Input } from "../components/Input";
import { Button } from "../components/Button";
import { useState, type Dispatch, type SetStateAction } from "react";
import { useNavigate, type NavigateFunction } from "react-router-dom";
import { useTheme } from "../hooks/useTheme";
import { Textarea } from "../components/Textarea";
import { Select } from "../components/Select";
import { Goto } from "../components/Goto";
import { apiFetch } from "../utils/apiFetch";
import { Alert } from "../components/Alert";

type DescribeFormData = {
    acceptMap: boolean,
    acceptContact: boolean,
    gender: string,
    birthYear: string,
    job: string,
    specializedTopics: string,
    otherSpecialization: string,
    teacherBehaviour: string,
    freeField: string,
}

export function DescribePage(){
    const {isProfileCompleted} = useTheme();

    const initialformData = {acceptMap: false, acceptContact: false,
                             gender:"", birthYear:"", job:"", specializedTopics:"",
                             otherSpecialization: "",teacherBehaviour: "", freeField: ""};
    const [formData, setFormData] = useState<DescribeFormData>(initialformData);
    const [fetchError, setFetchError] = useState<Error|null>(null);
    const navigate = useNavigate();

    const handleSubmit = (e:React.FormEvent<HTMLFormElement>) => {
        e.preventDefault(); 
        const data = {acceptMap: formData.acceptMap, 
                      acceptContact: formData.acceptContact,
                      gender: formData.gender, birthYear: formData.birthYear,
                      job: formData.job, specializedTopics: formData.specializedTopics,
                      otherSpecialization: formData.otherSpecialization,
                      teacherBehaviour: formData.teacherBehaviour, freeField: formData.freeField}
        sendPostRequest(data, setFetchError, navigate);
    }

    if (isProfileCompleted){
        return <Goto href="/application/profile" label="Cette page n'est plus accessible." buttonLabel="Cliquez ici pour modifier votre profil"/>
    }

    return <>
                <h2>Vous décrire</h2>
                <p> Votre inscription a bien été réalisée. 
                    Il vous reste quelques informations de profils à compléter avant de pouvoir accéder à l'application.
                </p>
                <form onSubmit={handleSubmit}>
                    <Input title="Acceptez-vous d'apparaître sur la carte dans vos établissements?" name="card-accept" type="checkbox" onChange={() => setFormData({...formData, acceptMap: !formData.acceptMap})}/>
                    <Input title="Etes-vous d'accord pour que d'autres enseignants puissent vous contacter par email?" name="card-accept" type="checkbox" onChange={() => setFormData({...formData, acceptContact: !formData.acceptContact})}/>
                    <h4 style={{"marginTop": "2em"}}>Les champs qui suivent sont facultatifs</h4>
                    <Select title="Genre" value={formData.gender} onChange={(e)=>{setFormData({...formData, gender: e.target.value})}}>
                        <option value="">Choisissez une des options suivantes</option>
                        <option value="Femme">Femme</option>
                        <option value="Homme">Homme</option>
                        <option value="Autre">Autre</option>
                        <option value="Ne souhaite pas répondre">Ne souhaite pas répondre</option>
                    </Select>
                    <Input title="Année de naissance" type="text" name="birthYear" value={formData.birthYear} onChange={(e)=>{setFormData({...formData, birthYear: e.target.value})}}/>
                    <Input title="Profession/Type de poste actuel" name="job" type="text" value={formData.job} onChange={(e)=>{setFormData({...formData, job: e.target.value})}}/>
                    <Input title="Discipline(s)/Spécialité(s)" name="specializedTopics" type="text" value={formData.specializedTopics} onChange={(e)=>{setFormData({...formData, specializedTopics: e.target.value})}}/>
                    <Input title="Autre spécialisation/Formation à mentionner" name="otherSpecialization" type="text" value={formData.otherSpecialization} onChange={(e)=>{setFormData({...formData, otherSpecialization: e.target.value})}}/>                    
                    <Textarea title="Comment vous décririez-vous en tant qu'enseignant (personnalité en classe, interactions avec les apprenants, philosophie de l'éducation)?" name="teacherBehaviour" value={formData.teacherBehaviour} onChange={(e)=>{setFormData({...formData, teacherBehaviour: e.target.value})}}/>
                    <Textarea title="Souhaitez-vous ajouter quelque chose sur vous en tant qu'enseignant? Nous vous y invitons dans cette partie de commentaire libre" name="freeField" value={formData.freeField} onChange={(e)=>{setFormData({...formData, freeField: e.target.value})}}/>

                    <Button>Sauvegarder les informations</Button>
                    {fetchError?.message && <Alert message={fetchError?.message} onClose={() => {setFetchError(null)}}/>}
                </form>
           </>
}


async function sendPostRequest(data: DescribeFormData, setFetchError:Dispatch<SetStateAction<Error|null>>, navigate: NavigateFunction){
    const response = await apiFetch("/user/addData", {
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
     
    if (response.ok){
        navigate("/application/institution");    
    } else {
        setFetchError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
    return response
}