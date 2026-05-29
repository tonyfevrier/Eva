import { useEffect, useState, type Dispatch, type SetStateAction } from "react";
import { Button } from "../components/Button";
import { Input } from "../components/Input";
import { Goto } from "../components/Goto";
import { apiFetch } from "../utils/apiFetch";


type DataType = {
    password: string,
    token: string
}

export function PasswordChangePage(){
    const [pwdSent, setPwdSent] = useState<Boolean>(false);
    const [passwords, setPasswords] = useState({password:"", passwordCopy:""})
    const [fetchError, setFetchError] = useState<Error|null>(null);
    const [token, setToken] = useState("");

    /*Extraire le token de l'url au premier chargement de la page */
    useEffect(() => {
        const searchParam = new URLSearchParams(window.location.search);
        const tokenFromUrl = searchParam.get("token")
        if (tokenFromUrl !== null){
            setToken(tokenFromUrl);
        }
    }, [])

    const handleFormChange = (e: React.ChangeEvent<HTMLInputElement>) => {
            const {name, value} = e.target;
            setPasswords({...passwords, [name] : value});
        }

    const handleSavePassword = (e:React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const passWordsAreIdentical = (passwords.password === passwords.passwordCopy);
        const passwordInputsAreCongruent = (passwords.password.length >= 8) && passWordsAreIdentical;
        
        if (!passwordInputsAreCongruent){
            setFetchError(new Error("Les mots de passe doivent être identiques et contenir au moins 8 caractères."));
            return;
        }

        const data = {password: passwords.password, token: token};          
        sendPostRequest(data, setFetchError, setPwdSent);
    }

    if (!pwdSent){
        return <>
                <h1> Changer de mot de passe </h1>
                <form onSubmit={handleSavePassword}>
                    <Input title="Veuillez entrer un nouveau mot de passe" type="password" name="password" value={passwords.password} onChange={handleFormChange}/>
                    <Input title="Veuillez entrer une seconde fois le mot de passe" type="password" name="passwordCopy" value={passwords.passwordCopy} onChange={handleFormChange}/>
                    <Button style={{"margin": "1em"}}>Enregistrer le mot de passe</Button>
                </form>
                {fetchError?.message && <p>{fetchError?.message}</p> }
           </>
    }
    return <Goto href="/login" label="Votre mot de passe a bien été modifié" buttonLabel="Revenir à la page de login"/>
}


async function sendPostRequest(data: DataType, setFetchError:Dispatch<SetStateAction<Error|null>>, setPwdSent:Dispatch<SetStateAction<Boolean>>){
    const response = await apiFetch("/auth/recoverPwd", {
            method: "POST",
            headers:{
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify(data)})
            .catch(error => {
                setFetchError(new Error(error.getMessage()))
                throw error;
        });
    
    if (response.ok){
        setPwdSent(true);    
    } else {
        setFetchError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
    return response
}