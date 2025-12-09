import { useEffect } from "react";
import { useTheme } from "./useTheme";

export function useHandleAuth(){
    const {isAuthenticated, expirationTime, refresh} = useTheme();
    const checkIfExpiredTimeInMin = 0.1;

    // Enregistrement des états en mémoire en cas de changement
    useEffect(() => {
        localStorage.setItem("isAuthenticated", String(isAuthenticated));
        localStorage.setItem("expirationTime", String(expirationTime));    
    }, [isAuthenticated]);


    // Tentative de rafraîchissement après expiration du token
    useEffect(() => {
        const refreshIfExpired = async () => {
            if (expirationTime !== 0 && Date.now() > expirationTime) {
                await refresh();
            }
        }

        if (expirationTime !== 0){
            const intervalId = setInterval(refreshIfExpired, checkIfExpiredTimeInMin * 60 * 1000);
            return () => clearInterval(intervalId);
        }
        
        return;
    }, [expirationTime]);
}