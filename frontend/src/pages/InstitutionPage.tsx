import { Input } from "../components/Input";
import { Button } from "../components/Button";
import { useState, type Dispatch, type SetStateAction } from "react";
import { useNavigate, type NavigateFunction } from "react-router-dom";
import { useTheme } from "../hooks/useTheme";
import { Textarea } from "../components/Textarea";
import { Select } from "../components/Select";

type InstitutionFormData = {
    name: string,
    town: string,
    category: string,
    contactMail: string,
    studentsNumber: string,
    socialStatus: string,
    institutionSpecifities: string,
    studentsSpecificities: string,
    teachersSpecificities: string,
}


export function InstitutionPage(){
    const {setIsProfileCompleted} = useTheme();
    const initialformData = {name: "", town: "", category: "", contactMail: "", socialStatus: "",
                             institutionSpecifities: "", studentsSpecificities: "",
                             studentsNumber: "", teachersSpecificities: ""};
    const [formData, setFormData] = useState<InstitutionFormData>(initialformData);
    const [fetchError, setFetchError] = useState<Error|null>(null);
    const navigate = useNavigate();

    /*const areRequiredInputsFilled = formData.gender !== "" && formData.birthday !== "jj/mm/aaaa" &&
                                    formData.job !== "" && formData.specializedTopics &&
                                    formData.teacherBehaviour !== "";*/

    const handleSubmit = (e:React.FormEvent<HTMLFormElement>) => {
        e.preventDefault(); 
        const data = {name: formData.name, town: formData.town, category: formData.category,
                      contactMail: formData.contactMail,
                      socialStatus: formData.socialStatus,
                      studentsNumber: formData.studentsNumber,
                      institutionSpecifities: formData.institutionSpecifities,
                      studentsSpecificities: formData.studentsSpecificities,
                      teachersSpecificities: formData.teachersSpecificities}
        sendPostRequest(data, setFetchError, navigate, setIsProfileCompleted);
    }


    return <>
                <h1>Tes établissements</h1>
                <p>
                    Pour terminer l'enregistrement, vous allez maintenant rentrer les détails sur votre ou vos établissements d'exercice.
                </p>
                <form onSubmit={handleSubmit}>
                    <Input title="Nom de l'établissement" name="name" type="text" value={formData.name} onChange={(e)=>{setFormData({...formData, name: e.target.value})}}/>
                    <Input title="Ville" name="ville" type="text" value={formData.town} onChange={(e)=>{setFormData({...formData, town: e.target.value})}}/>
                    <Input title="Mail de contact" name="contactMail" type="mail" value={formData.contactMail} onChange={(e)=>{setFormData({...formData, contactMail: e.target.value})}}/>
                    <Select title="Type" value={formData.category} onChange={(e)=>{setFormData({...formData, category: e.target.value})}}>
                        <option value="">Choisissez une des options suivantes</option>
                        <option value="Public">Public</option>
                        <option value="Privé">Privé</option>
                        <option value="Privé hors contrat">Privé hors contrat</option>
                        <option value="Autre">Autre</option>
                    </Select>
                    <Select title="Niveau socio-économique moyen des apprenants" value={formData.socialStatus} onChange={(e)=>{setFormData({...formData, socialStatus: e.target.value})}}>
                        <option value="">Choisissez une des options suivantes</option>
                        <option value="Très faible">Très faible</option>
                        <option value="Faible">Faible</option>
                        <option value="Moyen">Moyen</option>
                        <option value="Elevé">Elevé</option>
                        <option value="Très élevé">Très élevé</option>
                    </Select>
                    <Input title="Nombre approximatif d'étudiants" type="text" name="studentsNumber" value={formData.studentsNumber} onChange={(e)=>{setFormData({...formData, studentsNumber: e.target.value})}}/>
                    <Textarea title="Particularités de l'établissement" name="institutionSpecifities" value={formData.institutionSpecifities} onChange={(e)=>{setFormData({...formData, institutionSpecifities: e.target.value})}}/>
                    <Textarea title="Particularités des apprenants" name="studentsSpecificities" value={formData.studentsSpecificities} onChange={(e)=>{setFormData({...formData, studentsSpecificities: e.target.value})}}/>
                    <Textarea title="Particularités des enseignants" name="teachersSpecificities" value={formData.teachersSpecificities} onChange={(e)=>{setFormData({...formData, teachersSpecificities: e.target.value})}}/>

                    <Button disabled={!areRequiredInputsFilled}>Sauvegarder un établissement</Button>
                    <h1>IL Faudra mettre ICI un second bouton Sauvegarder et quitter la page qui n'apparait une fois qu'un établissement au moins a été complété</h1>
                    {fetchError?.message && <p>{fetchError?.message}</p>}
                </form>
           </>
}


async function sendPostRequest(data: InstitutionFormData, setFetchError:Dispatch<SetStateAction<Error|null>>, navigate: NavigateFunction, setIsProfileCompleted:Dispatch<SetStateAction<boolean>>){
    const response = await fetch("http://localhost:9000/institution/create", {
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
        navigate("/");    
    } else {
        setFetchError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
    return response
}