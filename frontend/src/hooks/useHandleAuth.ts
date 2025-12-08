import { useEffect, useState } from "react";

export function useHandleAuth(){
    const lastIsAuthenticated = localStorage.getItem("isAuthenticated");
    const lastExpirationTime = localStorage.getItem("expirationTime");
    const [isAuthenticated, setIsAuthenticated] = useState(lastIsAuthenticated? Boolean(lastIsAuthenticated):false);
    const [expirationTime, setExpirationTime] = useState(lastExpirationTime? Number(lastExpirationTime): 0);
    const toggleIsAuthenticated = () => {
        isAuthenticated ? setIsAuthenticated(false) : setIsAuthenticated(true);
    };

    // Enregistrement des états en mémoire en cas de changement
    useEffect(() => {
        localStorage.setItem("isAuthenticated", String(isAuthenticated));
        localStorage.setItem("expirationTime", String(expirationTime));
    }, [isAuthenticated]);

    // Remise de l'état à non authentifié après expiration du token
    useEffect(() => {
        if (expirationTime !== 0 && Date.now() > expirationTime) {
            toggleIsAuthenticated();
        }
    }, []);

    return {isAuthenticated, toggleIsAuthenticated, expirationTime, setExpirationTime};
}