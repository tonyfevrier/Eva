import { Button } from "../components/Button";
import { useState, type Dispatch, type SetStateAction } from "react";
import { useNavigate } from "react-router-dom";
import { useTheme } from "../hooks/useTheme";
import { InstitutionCreationPage} from "./InstitutionCreationPage";
import { InstitutionSelectionPage } from "./InstitutionSelectionPage";

export type InstitutionFormData = InstitutionCreationData | InstitutionSelectionData | null;

export type InstitutionCreationData = {
    name: string,
    town: string,
    category: string,
    contactMail: string,
    studentsNumber: string,
    socialStatus: string,
    institutionSpecifities: string,
    studentsSpecificities: string,
    teachersSpecificities: string,
};

export type InstitutionSelectionData = { affiliationId: string };

export function InstitutionPage(){
    const {isProfileCompleted, setIsProfileCompleted} = useTheme();
    const [userCreatesInstitution, setUserCreatesInstitution] = useState<boolean>(false);
    const [selectionFormData, setSelectionFormData] = useState<InstitutionSelectionData>({affiliationId: ""});
    const initialformData = {name: "", town: "", category: "", contactMail: "", socialStatus: "",
                             institutionSpecifities: "", studentsSpecificities: "",
                             studentsNumber: "", teachersSpecificities: ""};
    const [creationFormData, setCreationFormData] = useState<InstitutionCreationData>(initialformData);
    
    const [error, setError] = useState<Error|null>(null);
    const navigate = useNavigate();

    const areRequiredInputsFilled = userCreatesInstitution 
    ? creationFormData.name !== "" && creationFormData.town && creationFormData.category !== "" && creationFormData.contactMail !== "" && creationFormData.socialStatus !== "" && creationFormData.studentsNumber !== ""
    : selectionFormData.affiliationId !== "";
  
    const handleSubmit = (e:React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault();
        const data = userCreatesInstitution?creationFormData:selectionFormData;
        sendPostRequest(data, setError, setIsProfileCompleted, setCreationFormData, setSelectionFormData);
        if (e.currentTarget.name === "saveQuit"){
            navigate("/");
        } 
    }

    return <>
                <h1>Tes établissements</h1>
                {isProfileCompleted? <p> Vous avez enregistré un établissement avec succès, vous pouvez en enregistrer un autre</p>:
                                     <p>Pour terminer l'enregistrement, vous allez maintenant rentrer les détails sur votre ou vos établissements d'exercice.</p> }
                
                {!userCreatesInstitution && <InstitutionSelectionPage setData={setSelectionFormData}/>}
                {userCreatesInstitution && <InstitutionCreationPage formData={creationFormData} setData={setCreationFormData}/>}
                

                <Button onClick={() => {setUserCreatesInstitution(!userCreatesInstitution)}}>{userCreatesInstitution?"Choisir un établissement existant":"Créer un établissement"}</Button>
                <Button disabled={!areRequiredInputsFilled} name="save" onClick={handleSubmit}>Sauver l'établissement</Button>
                {isProfileCompleted && <Button onClick={()=>{navigate("/")}}>Quitter la page</Button> }
                {error?.message && <p>{error?.message}</p>}
           </>
}


async function sendPostRequest(
    data: InstitutionFormData, 
    setFetchError: Dispatch<SetStateAction<Error|null>>, 
    setIsProfileCompleted: Dispatch<SetStateAction<boolean>>,
    setCreationFormData: Dispatch<SetStateAction<InstitutionCreationData>>,
    setSelectionFormData: Dispatch<SetStateAction<InstitutionSelectionData>>,
){
    const isDataAnInstitutionCreationPage = data != null && "name" in data;
    const endpoint = isDataAnInstitutionCreationPage?"create":"associate"; 
    const response = await fetch(`http://localhost:9000/institution/${endpoint}`, {
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
        alert("Vous venez d'enregistrer un établissement avec succès!")
        setFetchError(null);
        setCreationFormData({name: "", town: "", category: "", contactMail: "", socialStatus: "",
                             institutionSpecifities: "", studentsSpecificities: "",
                             studentsNumber: "", teachersSpecificities: ""});
        setSelectionFormData({affiliationId: ""});
    } else {
        setFetchError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
    return response
}


