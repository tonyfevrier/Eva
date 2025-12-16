import { useEffect, useReducer, type Dispatch} from "react";
import { useFetch } from "../hooks/useFetch"
import { Spinner } from "../components/Spinner";
import type { UpdateFormString } from "../types/types";

type State = {
    formDataInMemory: UpdateFormString; //Valeur des champs enregistrées actuellement dans la base de données
    isEditing: boolean;
    isChangingPassword: boolean;
    formData: UpdateFormString; //Valeur en temps réel des inputs
    updateError: Error|null;
};

type Action = 
    | { type: 'TOGGLE_EDITING' }
    | { type: 'TOGGLE_PASSWORD_CHANGE' }
    | { type: 'SAVE_INFOS' }
    | { type: 'SAVE_PWD' }
    | { type: 'UPDATE_FIELD', field: string, value: string }
    | { type: 'UPDATE_FIELD_MEMORY', field: string, value: string }
    | { type: 'SET_ERROR', error: Error|null };

function reducer(state:State, action:Action){
    switch(action.type){
        case 'TOGGLE_EDITING': //si on annule, les inputs sont remis à leurs valeurs initales
            return { ...state, isEditing: !state.isEditing,
                       formData:{...state.formData, firstname: state.formDataInMemory.firstname,
                                                    lastname: state.formDataInMemory.lastname}};
        case 'TOGGLE_PASSWORD_CHANGE':
            return { ...state, isChangingPassword: !state.isChangingPassword, updateError: null,
                        formData:{...state.formData, password: "pass", passwordCopy:"pass"}};
        case 'SAVE_INFOS': 
            return { ...state, isEditing: false, updateError: null, 
                    formDataInMemory:{...state.formDataInMemory, firstname: state.formData.firstname, 
                                                                 lastname: state.formData.lastname}};
        case 'SAVE_PWD':
            return { ...state, isChangingPassword: false, updateError: null,
                    formDataInMemory:{...state.formDataInMemory, password: "pass", passwordCopy:"pass"}};
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

export function ProfilePage(){    
    const initialformData = {firstname: "", lastname: "",mail: "", password:"pass", passwordCopy:"pass"};
    const initialState = {isEditing: false, isChangingPassword: false, formData: initialformData, formDataInMemory: initialformData, updateError: null};
    const [state, dispatch] = useReducer(reducer, initialState);

    const {loading, data, error} = useFetch<any>("http://localhost:9000/auth/profile");
    
    useEffect(() => {
        if (data){
            fillInputsWithUserInfos(data, dispatch);
        }
    }, [data]);

    const handleFormChange = (e:React.ChangeEvent<HTMLInputElement>) => {
        /*Prend en charge les changements de valeur de l'input qu'on modifie*/
        const {name, value} = e.target;
        dispatch({type: "UPDATE_FIELD", field: name , value: value});        
    }
    
    const handleToggleEditing = () => {
        dispatch({type: 'TOGGLE_EDITING'});
    }

    const handleTogglePassword = () => {
        dispatch({type: 'TOGGLE_PASSWORD_CHANGE'});
    }
    
    const handleSaveInfos = async (event: React.FormEvent<HTMLFormElement>) => {
        /*on regarde si aucun des éléments n'est vide, si oui on lance une requête */
        event.preventDefault();

        if (state.formData.firstname === ""){
            dispatch({type: 'SET_ERROR', error: new Error("Le champ firstname doit être rempli")});
            return;
        }
        if (state.formData.lastname === ""){
            dispatch({type: 'SET_ERROR', error: new Error("Le champ lastname doit être rempli")});
            return;
        } 
        const updatedData = JSON.stringify({
                    firstname: state.formData.firstname,
                    lastname: state.formData.lastname,
                });
        
        sendPutRequest(updatedData, dispatch, "SAVE_INFOS");
    }

    const handleSavePassword = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        const passWordsAreIdentical = (state.formData.password === state.formData.passwordCopy);
        const passwordInputsAreCongruent = (state.formData.password.length >= 8) && passWordsAreIdentical;
        
        if (!passwordInputsAreCongruent){
            dispatch({type: 'SET_ERROR', error: new Error("Les mots de passe doivent être identiques et contenir au moins 8 caractères.")});
            return;
        }
         
        const updatedData = JSON.stringify({password: state.formData.password});
        sendPutRequest(updatedData, dispatch, "SAVE_PWD");
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
                <form onSubmit={handleSaveInfos}>
                    <div>
                        <p>Mail</p>
                        <input type="text" value={state.formData.mail} name="mail" onChange={handleFormChange} disabled={true}/>
                        {state.formData.mail === "" && <p> Ce champ doit être rempli </p>}
                    </div>
                    <div>
                        <p>Prénom</p>
                        <input type="text" value={state.formData.firstname} name="firstname" onChange={handleFormChange} disabled={!state.isEditing}/>
                        {state.formData.firstname === "" && <p> Ce champ doit être rempli </p>}
                    </div>
                    <div>
                        <p>Nom</p>
                        <input type="text" value={state.formData.lastname} name="lastname" onChange={handleFormChange} disabled={!state.isEditing}/>
                        {state.formData.lastname === "" && <p> Ce champ doit être rempli </p>}
                    </div>
                    <div className="profile-modify">
                        {!state.isEditing && <button type="button" onClick={handleToggleEditing}>Modifier les informations</button>}
                        { state.isEditing && <button type="button" onClick={handleToggleEditing}>Annuler les modifications</button>}
                        <button disabled={!state.isEditing}>Sauvegarder</button>
                    </div>
                </form>
                <form onSubmit={handleSavePassword}>
                    <div>
                        <p>Veuillez entrer un nouveau mot de passe</p>
                        <input type="password" name="password" disabled = {!state.isChangingPassword} value={state.formData.password} onChange={handleFormChange}/>
                        <p>Veuillez entrer une seconde fois le mot de passe</p>
                        <input type="password" name="passwordCopy" disabled = {!state.isChangingPassword} value={state.formData.passwordCopy} onChange={handleFormChange}/>
                    </div>
                    <div className="profile-modify">
                        {!state.isChangingPassword && <button type="button" onClick={handleTogglePassword}> Changer le mot de passe</button>}
                        {state.isChangingPassword && <button type="button" onClick={handleTogglePassword}> Annuler le changement</button>}
                        <button disabled={!state.isChangingPassword}>Sauvegarder</button>
                        <a href="" onClick={handleDeleteClick}>Supprimer l'utilisateur</a>
                    </div>
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

async function sendPutRequest(updatedData:string, dispatch:Dispatch<Action>, saveAction: 'SAVE_INFOS'|'SAVE_PWD'){
    const response = await fetch("http://localhost:9000/auth/update", {
                method: "put",
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: updatedData,
                credentials: "include"  
            })
            .catch(requestError => {
                dispatch({type: 'SET_ERROR', error: requestError});
                throw requestError;
            });

    // MaJ des états après la requête
    if (response.ok){
        dispatch({type: saveAction}); 
    } else {
        dispatch({type: 'SET_ERROR', error: new Error(`Erreur ${response.status}: ${response.statusText}`)});
    }
}
