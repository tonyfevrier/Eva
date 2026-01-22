import { Input } from "../components/Input";
import { Button } from "../components/Button";
import { useState, type Dispatch, type SetStateAction } from "react";
import { useNavigate, type NavigateFunction } from "react-router-dom";
import { useTheme } from "../hooks/useTheme";
import { Textarea } from "../components/Textarea";
import { Select } from "../components/Select";

type DescribeFormData = {
    street: string,
    postcode: string,
    town: string,
    phone: string,
    acceptMap: boolean,
    acceptContact: boolean,
    gender: string,
    birthday: string,
    job: string,
    specializedTopics: string,
    otherSpecialization: string,
    teacherBehaviour: string,
    freeField: string,
}

export function DescribePage(){
    const {setIsProfileCompleted} = useTheme();
    const initialformData = {street: "",  postcode: "", town: "",
                             phone: "", acceptMap: false, acceptContact: false,
                             gender:"", birthday:"", job:"", specializedTopics:"",
                             otherSpecialization: "",teacherBehaviour: "", freeField: ""};
    const [formData, setFormData] = useState<DescribeFormData>(initialformData);
    const [fetchError, setFetchError] = useState<Error|null>(null);
    const navigate = useNavigate();

    const areRequiredInputsFilled = formData.gender !== "" && formData.birthday !== "jj/mm/aaaa" &&
                                    formData.job !== "" && formData.specializedTopics &&
                                    formData.teacherBehaviour !== "";

    const handleSubmit = (e:React.FormEvent<HTMLFormElement>) => {
        e.preventDefault(); 
        const data = {street: formData.street,  
                      postcode: formData.postcode, town: formData.town,
                      phone: formData.phone, acceptMap: formData.acceptMap, 
                      acceptContact: formData.acceptContact,
                      gender: formData.gender, birthday: formData.birthday,
                      job: formData.job, specializedTopics: formData.specializedTopics,
                      otherSpecialization: formData.otherSpecialization,
                      teacherBehaviour: formData.teacherBehaviour, freeField: formData.freeField}
        sendPostRequest(data, setFetchError, navigate, setIsProfileCompleted);
    }


    return <>
                <h1>Te décrire</h1>
                <p> Votre inscription a bien été réalisée. 
                    Il vous reste quelques informations de profils à compléter avant de pouvoir accéder à l'application.
                </p>
                <form onSubmit={handleSubmit}>
                    <Select title="Genre" value={formData.gender} onChange={(e)=>{setFormData({...formData, gender: e.target.value})}}>
                        <option value="">Choisissez une des options suivantes</option>
                        <option value="Femme">Femme</option>
                        <option value="Homme">Homme</option>
                        <option value="Autre">Autre</option>
                        <option value="Ne souhaite pas répondre">Ne souhaite pas répondre</option>
                    </Select>
                    <Input title="Date de naissance" type="date" name="birthday" value={formData.birthday} onChange={(e)=>{setFormData({...formData, birthday: e.target.value})}} max={new Date().toISOString().split('T')[0]} required/>
                    <Input title="Profession/Type de poste actuel" name="job" type="text" value={formData.job} onChange={(e)=>{setFormData({...formData, job: e.target.value})}}/>
                    <Input title="Discipline(s)/Spécialité(s)" name="specializedTopics" type="text" value={formData.specializedTopics} onChange={(e)=>{setFormData({...formData, specializedTopics: e.target.value})}}/>
                    <Input title="Autre spécialisation/Formation à mentionner" name="otherSpecialization" type="text" value={formData.otherSpecialization} onChange={(e)=>{setFormData({...formData, otherSpecialization: e.target.value})}}/>                    
                    <Input title="Etes-vous d'accord pour que votre localisation apparaisse sur une carte?" name="card-accept" type="checkbox" onChange={() => setFormData({...formData, acceptMap: !formData.acceptMap})}/>
                    <Input title="Rue" name="rue" type="text" value={formData.street} onChange={(e)=>{setFormData({...formData, street: e.target.value})}} disabled={!formData.acceptMap}/>
                    <Input title="Code postal" name="postcode" type="text" value={formData.postcode} onChange={(e)=>{setFormData({...formData, postcode: e.target.value})}} disabled={!formData.acceptMap}/>
                    <Input title="Ville" name="ville" type="text" value={formData.town} onChange={(e)=>{setFormData({...formData, town: e.target.value})}} disabled={!formData.acceptMap}/>
                    <Input title="Etes-vous d'accord pour que d'autres enseignants puissent vous contacter par email? Si oui vous pourrez rentrer votre numéro de téléphone" name="card-accept" type="checkbox" onChange={() => setFormData({...formData, acceptContact: !formData.acceptContact})}/>
                    <Input title="Téléphone (facultatif)" name="téléphone" type="tel" value={formData.phone} onChange={(e)=>{setFormData({...formData, phone: e.target.value})}} disabled={!formData.acceptContact}/>
                    <Textarea title="Comment vous décririez-vous en tant qu'enseignant? (personnalité en classe, interactions avec les apprenants, philosophie de l'éducation" name="teacherBehaviour" value={formData.teacherBehaviour} onChange={(e)=>{setFormData({...formData, teacherBehaviour: e.target.value})}}/>
                    <Textarea title="Vous souhaitez ajouter quelque chose sur vous en tant qu'enseignant? Nous vous y invitons dans cette partie de commentaire libre" name="freeField" value={formData.freeField} onChange={(e)=>{setFormData({...formData, freeField: e.target.value})}}/>

                    <Button disabled={!areRequiredInputsFilled}>Sauvegarder les informations</Button>
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
     
    if (response.ok){
        setIsProfileCompleted(true);
        navigate("/application/institution");    
    } else {
        setFetchError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
    return response
}