import { useEffect, useState, type Dispatch, type SetStateAction } from "react";
import { Button } from "../components/Button";
import { Input } from "../components/Input";
import { useNavigate, type NavigateFunction } from "react-router-dom";


type DataType = {
    password: string,
    token: string
}

export function PasswordChangePage(){
    const [passwords, setPasswords] = useState({password:"", passwordCopy:""})
    const [fetchError, setFetchError] = useState<Error|null>(null);
    const navigate = useNavigate();
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
        sendPostRequest(data, setFetchError, navigate);
    }

    return <>
                <h1> Changer de mot de passe </h1>
                <form onSubmit={handleSavePassword}>
                    <Input title="Veuillez entrer un nouveau mot de passe" type="password" name="password" value={passwords.password} onChange={handleFormChange}/>
                    <Input title="Veuillez entrer une seconde fois le mot de passe" type="password" name="passwordCopy" value={passwords.passwordCopy} onChange={handleFormChange}/>
                    <Button>Enregistrer le mot de passe</Button>
                </form>
                {fetchError?.message && <p>{fetchError?.message}</p> }
           </>
}


async function sendPostRequest(data: DataType, setFetchError:Dispatch<SetStateAction<Error|null>>, navigate: NavigateFunction){
    const response = await fetch("http://localhost:9000/auth/recoverPwd", {
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
        navigate("/pwdUpdated");    
    } else {
        setFetchError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
    return response
}