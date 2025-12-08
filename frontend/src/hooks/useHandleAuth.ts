import { useEffect } from "react";
import { useTheme } from "./useTheme";

export function useHandleAuth(){
    const {isAuthenticated, expirationTime, logout} = useTheme();

    // Enregistrement des états en mémoire en cas de changement
    useEffect(() => {
        localStorage.setItem("isAuthenticated", String(isAuthenticated));
        localStorage.setItem("expirationTime", String(expirationTime));    
    }, [isAuthenticated]);

    // Remise de l'état à non authentifié après expiration du token
    useEffect(() => {
        if (expirationTime !== 0 && Date.now() > expirationTime) {
            logout();
        }
    }, []);

    useEffect(() => {
        
    }, [expirationTime])
}