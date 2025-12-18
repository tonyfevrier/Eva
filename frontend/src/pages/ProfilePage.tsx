import { useEffect, useReducer, type Dispatch, type SetStateAction} from "react";
import { useFetch } from "../hooks/useFetch"
import { Spinner } from "../components/Spinner";
import type { UpdateFormString } from "../types/types";
import { UpdateButtons } from "../components/UpdateButtons";
import { Input } from "../components/Input";
import { Modal } from "../components/Modal";
import { useNavigate, type NavigateFunction } from "react-router-dom";
import { useTheme } from "../hooks/useTheme";

type State = {
    formDataInMemory: UpdateFormString; //Valeur des champs enregistrées actuellement dans la base de données
    isEditing: boolean;
    isChangingPassword: boolean;
    formData: UpdateFormString; //Valeur en temps réel des inputs
    updateError: Error|null;
    printModal: boolean;
};

type Action = 
    | { type: 'TOGGLE_EDITING' }
    | { type: 'TOGGLE_PASSWORD_CHANGE' }
    | { type: 'SAVE_INFOS' }
    | { type: 'SAVE_PWD' }
    | { type: 'UPDATE_FIELD', field: string, value: string }
    | { type: 'UPDATE_FIELD_MEMORY', field: string, value: string }
    | { type: 'SET_ERROR', error: Error|null }
    | { type: 'TOGGLE_MODAL' };

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
        case 'TOGGLE_MODAL':
            return {...state, printModal: !state.printModal};
    }
}

export function ProfilePage(){    
    const initialformData = {firstname: "", lastname: "",mail: "", password:"pass", passwordCopy:"pass"};
    const initialState = {isEditing: false, isChangingPassword: false, formData: initialformData, formDataInMemory: initialformData, updateError: null, printModal: false};
    const [state, dispatch] = useReducer(reducer, initialState);
    const navigate = useNavigate();
    const {toggleIsAuthenticated} = useTheme();

    // Chargement des données utilisateurs
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
    
    const handleToggleEditing = () => {dispatch({type: 'TOGGLE_EDITING'});}
    const handleTogglePassword = () => {dispatch({type: 'TOGGLE_PASSWORD_CHANGE'});}
    const handleToggleModal = () => {dispatch({type: 'TOGGLE_MODAL'});}

    const handleDeleteConfirm = async () => {
        sendDeleteRequest(dispatch, toggleIsAuthenticated, navigate);
    }

    const handleSaveInfos = async (event: React.FormEvent<HTMLFormElement>) => {
        /*Gère la sauvegarde des données utilisateur modifiées */
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
        /*Gère la modification des mots de passe */
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
    
    if (error){
        return <>{error}</>
    }
    
    if (loading){
        return <Spinner/>
    }

    return <>
                <h1> Infos utilisateurs</h1>
                <form onSubmit={handleSaveInfos}>
                    <Input title="Mail" name="mail" value={state.formData.mail} onChange={handleFormChange}/>
                    <Input title="Prénom" name="firstname" value={state.formData.firstname} onChange={handleFormChange} disabled={!state.isEditing} variant="withErrorMsg"/>
                    <Input title="Nom" name="lastname" value={state.formData.lastname} onChange={handleFormChange} disabled={!state.isEditing} variant="withErrorMsg"/>
                    <UpdateButtons toggleButton={state.isEditing} handleToggleButton={handleToggleEditing}/>
                </form>
                <form onSubmit={handleSavePassword}>
                    <Input title="Veuillez entrer un nouveau mot de passe" type="password" name="password" value={state.formData.password} onChange={handleFormChange} disabled={!state.isChangingPassword}/>
                    <Input title="Veuillez entrer une seconde fois le mot de passe" type="password" name="passwordCopy" value={state.formData.passwordCopy} onChange={handleFormChange} disabled={!state.isChangingPassword}/>
                    <UpdateButtons toggleButton={state.isChangingPassword} handleToggleButton={handleTogglePassword} text="Modifier le mot de passe"/>
                </form>
                {state.updateError?.message && <p>{state.updateError?.message}</p> }
                <button onClick={handleToggleModal}>Supprimer l'utilisateur</button>
                {state.printModal && <Modal postTitle="Confirmation de fermeture" postContent="Confirmez-vous la suppression de votre compte utilisateur?" onClose={handleToggleModal} onSave={handleDeleteConfirm}/>}
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

async function sendDeleteRequest(dispatch:Dispatch<Action>, toggleIsAuthenticated:() => void, navigate: NavigateFunction){
    const response = await fetch("http://localhost:9000/auth/delete", {
            method: "delete",
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            credentials: "include"  
        })
        .catch(requestError => {
            dispatch({type: 'SET_ERROR', error: requestError});
            throw requestError;
        });

    // Redirection si la requête est acceptée 
    if (response.ok){
         navigate("/");
         toggleIsAuthenticated();
    } else {
        dispatch({type: 'SET_ERROR', error: new Error(`Erreur ${response.status}: ${response.statusText}`)});
    }
}
