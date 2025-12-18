import { useEffect } from "react";
import { useTheme } from "./useTheme";

export function useHandleAuth(){
    /* Gère l'envoi d'une requête de rafraichissement de token pour */

    const {isAuthenticated, expirationTime, refresh} = useTheme();
    const checkIfExpiredTimeInMin = 10; 
    
    const refreshIfExpired = async () => {
            if (expirationTime !== 0 && Date.now() > expirationTime) {
                await refresh();
            }
    }

    // Enregistrement des états en mémoire en cas de changement
    useEffect(() => {
        localStorage.setItem("isAuthenticated", String(isAuthenticated));
        localStorage.setItem("expirationTime", String(expirationTime));    
    }, [isAuthenticated, expirationTime]);

    // Vérification de l'expiration du token au montage
    useEffect(() => {refreshIfExpired()}, []);

    // Vérification de l'expiration du token à intervalles réguliers
    useEffect(() => {
        if (expirationTime !== 0){
            const intervalId = setInterval(refreshIfExpired, checkIfExpiredTimeInMin * 60 * 1000);
            return () => clearInterval(intervalId);
        }
        
        return;
    }, [expirationTime]);
}
