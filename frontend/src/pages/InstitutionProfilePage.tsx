import { Input } from "../components/Input";
import { useEffect, useReducer, type Dispatch } from "react";
import { Textarea } from "../components/Textarea";
import { Select } from "../components/Select";
import { UpdateButtons } from "../components/UpdateButtons";
import { useFetch } from "../hooks/useFetch";
import { useParams } from "react-router-dom";
import { Spinner } from "../components/Spinner";
import { apiFetch } from "../utils/apiFetch";

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

type State = {
    isEditing: boolean;
    formData: InstitutionFormData; //Valeur en temps réel des inputs
    formDataInMemory: InstitutionFormData; //Valeur des champs enregistrées actuellement dans la base de données. Utile si l'utilisateur veut annuler ses modifications
    updateError: Error|null;
};

type Action = 
    | { type: 'TOGGLE_EDITING' }
    | { type: 'SAVE_INFOS' }
    | { type: 'UPDATE_FIELD', field: string, value: string }
    | { type: 'UPDATE_FIELD_MEMORY', field: string, value: string }
    | { type: 'SET_ERROR', error: Error|null }

function reducer(state:State, action:Action){
    switch(action.type){
        case 'TOGGLE_EDITING': //si on annule, les inputs sont remis à leurs valeurs initales
            return { ...state, isEditing: !state.isEditing,
                       formData:{...state.formData, name: state.formDataInMemory.name,
                                                    town: state.formDataInMemory.town,
                                                    category: state.formDataInMemory.category,
                                                    contactMail: state.formDataInMemory.contactMail,
                                                    studentsNumber: state.formDataInMemory.studentsNumber,
                                                    socialStatus: state.formDataInMemory.socialStatus,
                                                    institutionSpecifities: state.formDataInMemory.institutionSpecifities,
                                                    studentsSpecificities: state.formDataInMemory.studentsSpecificities,
                                                    teachersSpecificities: state.formDataInMemory.teachersSpecificities}};
        case 'SAVE_INFOS': 
            return { ...state, isEditing: false, updateError: null, 
                    formDataInMemory:{...state.formDataInMemory, name: state.formData.name, 
                                                                 town: state.formData.town,
                                                                 category: state.formData.category,
                                                                 contactMail: state.formData.contactMail,
                                                                 studentsNumber: state.formData.studentsNumber,
                                                                 socialStatus: state.formData.socialStatus,
                                                                 institutionSpecifities: state.formData.institutionSpecifities,
                                                                 studentsSpecificities: state.formData.studentsSpecificities,
                                                                 teachersSpecificities: state.formData.teachersSpecificities}};
        case 'UPDATE_FIELD': //Mise à jour des champs input
            return {...state, formData: {...state.formData,
                                               [action.field]: action.value}};
        case 'UPDATE_FIELD_MEMORY': //Mise à jour de la valeur des champs input dans la base de données
            return {...state, formDataInMemory: {...state.formDataInMemory,
                                               [action.field]: action.value}};
        case 'SET_ERROR':
            return {...state, updateError: action.error};
    }
}


export function InstitutionProfilePage(){
    const initialformData = {name: "", town: "", category: "", contactMail: "", socialStatus: "",
                             institutionSpecifities: "", studentsSpecificities: "",
                             studentsNumber: "", teachersSpecificities: ""};
    const initialState = {isEditing: false, formData: initialformData, formDataInMemory: initialformData,
                          updateError: null};
    const [state, dispatch] = useReducer(reducer, initialState);

    const {id} = useParams();
    const {loading, data} = useFetch(`/institution/get/${id}`);
    
    useEffect(() => {
        if (data){
            fillInputsWithUserInfos(data, dispatch);
        } 
    }, [data]);

    const handleSaveInfos = (e:React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const data = {name: state.formData.name, town: state.formData.town, category: state.formData.category,
                      contactMail: state.formData.contactMail,
                      socialStatus: state.formData.socialStatus,
                      studentsNumber: state.formData.studentsNumber,
                      institutionSpecifities: state.formData.institutionSpecifities,
                      studentsSpecificities: state.formData.studentsSpecificities,
                      teachersSpecificities: state.formData.teachersSpecificities}
        if (id !== undefined){
            sendPutRequest(data, id, dispatch);
        }
    }

    const handleToggleEditing = () => {dispatch({type: 'TOGGLE_EDITING'});}

    if (loading){
        return <Spinner/>
    }


    return <>
                <h2 style={{'margin': '1em'}}>Profil de l'établissement</h2>

                <form onSubmit={handleSaveInfos}>
                    <Input title="Nom de l'établissement" name="name" type="text" value={state.formData.name} onChange={(e)=>{dispatch({type: 'UPDATE_FIELD', field: 'name', value: e.target.value})}} disabled={!state.isEditing} required/>
                    <Input title="Ville" name="ville" type="text" value={state.formData.town} onChange={(e)=>{dispatch({type: 'UPDATE_FIELD', field: 'town', value: e.target.value})}} disabled={!state.isEditing}/>
                    <Input title="Mail de contact" name="contactMail" type="mail" value={state.formData.contactMail} onChange={(e)=>{dispatch({type: 'UPDATE_FIELD', field: 'contactMail', value: e.target.value})}} disabled={!state.isEditing} required/>
                    <Select title="Type" value={state.formData.category} onChange={(e)=>{dispatch({type: 'UPDATE_FIELD', field: 'category', value: e.target.value})}} disabled={!state.isEditing} required>
                        <option value="">Choisissez une des options suivantes</option>
                        <option value="Public">Public</option>
                        <option value="Privé">Privé</option>
                        <option value="Privé hors contrat">Privé hors contrat</option>
                        <option value="Autre">Autre</option>
                    </Select>
                    <Select title="Niveau socio-économique moyen des apprenants" value={state.formData.socialStatus} onChange={(e)=>{dispatch({type: 'UPDATE_FIELD', field: 'socialStatus', value: e.target.value})}} disabled={!state.isEditing} required>
                        <option value="">Choisissez une des options suivantes</option>
                        <option value="Très faible">Très faible</option>
                        <option value="Faible">Faible</option>
                        <option value="Moyen">Moyen</option>
                        <option value="Elevé">Elevé</option>
                        <option value="Très élevé">Très élevé</option>
                    </Select>
                    <Input title="Nombre approximatif d'étudiants" type="text" name="studentsNumber" value={state.formData.studentsNumber} onChange={(e)=>{dispatch({type: 'UPDATE_FIELD', field: 'studentsNumber', value: e.target.value})}} disabled={!state.isEditing} required/>
                    <Textarea title="Particularités de l'établissement" name="institutionSpecifities" value={state.formData.institutionSpecifities} onChange={(e)=>{dispatch({type: 'UPDATE_FIELD', field: 'institutionSpecifities', value: e.target.value})}} disabled={!state.isEditing}/>
                    <Textarea title="Particularités des apprenants" name="studentsSpecificities" value={state.formData.studentsSpecificities} onChange={(e)=>{dispatch({type: 'UPDATE_FIELD', field: 'studentsSpecificities', value: e.target.value})}} disabled={!state.isEditing}/>
                    <Textarea title="Particularités des enseignants" name="teachersSpecificities" value={state.formData.teachersSpecificities} onChange={(e)=>{dispatch({type: 'UPDATE_FIELD', field: 'teachersSpecificities', value: e.target.value})}} disabled={!state.isEditing}/>

                    <UpdateButtons toggleButton={state.isEditing} handleToggleButton={handleToggleEditing}/>
                    {state.updateError?.message && <p>{state.updateError?.message}</p> }
                </form>
           </>
}

function fillInputsWithUserInfos(data: any, dispatch:Dispatch<Action>){
    const userInfos = Object.keys(data);
    userInfos.forEach(userInfo => {
        dispatch({type: "UPDATE_FIELD", field: userInfo, value: data[userInfo]});
        dispatch({type: "UPDATE_FIELD_MEMORY", field: userInfo, value: data[userInfo]});
    })
}

async function sendPutRequest(data: InstitutionFormData, id:string, dispatch:Dispatch<Action>){
    const response = await apiFetch(`/institution/update/${id}`, {
            method: "PUT",
            headers:{
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify(data),
            credentials: "include"})
            .catch(error => {
                dispatch({type: 'SET_ERROR', error:new Error(error?.message || String(error))});
                throw error;
        });
     
    // MaJ des états après la requête
    if (response.ok){
        dispatch({type: 'SAVE_INFOS'}); 
    } else {
        dispatch({type: 'SET_ERROR', error: new Error(`Erreur ${response.status}: ${response.statusText}`)});
    }
}