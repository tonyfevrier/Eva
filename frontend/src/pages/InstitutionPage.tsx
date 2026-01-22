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
    const {isProfileCompleted, setIsProfileCompleted} = useTheme();
    const initialformData = {name: "", town: "", category: "", contactMail: "", socialStatus: "",
                             institutionSpecifities: "", studentsSpecificities: "",
                             studentsNumber: "", teachersSpecificities: ""};
    const [formData, setFormData] = useState<InstitutionFormData>(initialformData);
    const [fetchError, setFetchError] = useState<Error|null>(null);
    const navigate = useNavigate();

    const areRequiredInputsFilled = formData.name !== "" && formData.contactMail !== "jj/mm/aaaa" &&
                                    formData.category !== "" && formData.socialStatus &&
                                    formData.studentsNumber !== "";

    const handleSubmit = (e:React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault();
        const data = {name: formData.name, town: formData.town, category: formData.category,
                      contactMail: formData.contactMail,
                      socialStatus: formData.socialStatus,
                      studentsNumber: formData.studentsNumber,
                      institutionSpecifities: formData.institutionSpecifities,
                      studentsSpecificities: formData.studentsSpecificities,
                      teachersSpecificities: formData.teachersSpecificities}
    console.log(isProfileCompleted)
        sendPostRequest(data, setFetchError, navigate, setIsProfileCompleted);
        if (e.currentTarget.name === "saveQuit"){
            navigate("/");
        } 
    }


    return <>
                <h1>Tes établissements</h1>
                {isProfileCompleted? <p> Vous avez enregistré un établissement avec succès, vous pouvez en entrer un autre</p>:
                                     <p>Pour terminer l'enregistrement, vous allez maintenant rentrer les détails sur votre ou vos établissements d'exercice.</p> }

                <form>
                    <Input title="Nom de l'établissement" name="name" type="text" value={formData.name} onChange={(e)=>{setFormData({...formData, name: e.target.value})}} required/>
                    <Input title="Ville" name="ville" type="text" value={formData.town} onChange={(e)=>{setFormData({...formData, town: e.target.value})}}/>
                    <Input title="Mail de contact" name="contactMail" type="mail" value={formData.contactMail} onChange={(e)=>{setFormData({...formData, contactMail: e.target.value})}} required/>
                    <Select title="Type" value={formData.category} onChange={(e)=>{setFormData({...formData, category: e.target.value})}} required>
                        <option value="">Choisissez une des options suivantes</option>
                        <option value="Public">Public</option>
                        <option value="Privé">Privé</option>
                        <option value="Privé hors contrat">Privé hors contrat</option>
                        <option value="Autre">Autre</option>
                    </Select>
                    <Select title="Niveau socio-économique moyen des apprenants" value={formData.socialStatus} onChange={(e)=>{setFormData({...formData, socialStatus: e.target.value})}} required>
                        <option value="">Choisissez une des options suivantes</option>
                        <option value="Très faible">Très faible</option>
                        <option value="Faible">Faible</option>
                        <option value="Moyen">Moyen</option>
                        <option value="Elevé">Elevé</option>
                        <option value="Très élevé">Très élevé</option>
                    </Select>
                    <Input title="Nombre approximatif d'étudiants" type="text" name="studentsNumber" value={formData.studentsNumber} onChange={(e)=>{setFormData({...formData, studentsNumber: e.target.value})}} required/>
                    <Textarea title="Particularités de l'établissement" name="institutionSpecifities" value={formData.institutionSpecifities} onChange={(e)=>{setFormData({...formData, institutionSpecifities: e.target.value})}}/>
                    <Textarea title="Particularités des apprenants" name="studentsSpecificities" value={formData.studentsSpecificities} onChange={(e)=>{setFormData({...formData, studentsSpecificities: e.target.value})}}/>
                    <Textarea title="Particularités des enseignants" name="teachersSpecificities" value={formData.teachersSpecificities} onChange={(e)=>{setFormData({...formData, teachersSpecificities: e.target.value})}}/>

                    {isProfileCompleted && <Button onClick={()=>{navigate("/")}}>Quitter la page</Button> }
                    <Button disabled={!areRequiredInputsFilled} name="saveStay" onClick={handleSubmit}>Sauver et entrer un autre établissement</Button>
                    <Button disabled={!areRequiredInputsFilled} name="saveQuit" onClick={handleSubmit}>Sauver et quitter la page</Button>
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
                setFetchError(new Error(error?.message || String(error)))
                throw error;
        });
     
    if (response.ok){
        setIsProfileCompleted(true);
    } else {
        console.log(12)
        setFetchError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
    return response
}