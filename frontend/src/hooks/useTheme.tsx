import { createContext, useContext, useState, type Dispatch, type PropsWithChildren, type SetStateAction } from "react"

type ThemeContextType = {
    isAuthenticated: boolean,
    setIsAuthenticated: Dispatch<SetStateAction<boolean>>,
    expirationTime: number,
    setExpirationTime: Dispatch<SetStateAction<number>>
    isProfileCompleted: boolean,
    setIsProfileCompleted: Dispatch<SetStateAction<boolean>>,
}

const ThemeContext = createContext<ThemeContextType>({
    isAuthenticated: false,
    setIsAuthenticated: () => false,
    expirationTime: 0,
    setExpirationTime: () => {},
    isProfileCompleted: false,
    setIsProfileCompleted: () => false
});

export function useTheme(){
    const {isAuthenticated, setIsAuthenticated, 
           expirationTime, setExpirationTime,
           isProfileCompleted, setIsProfileCompleted} = useContext(ThemeContext);
    
    const toggleIsAuthenticated = () => {
        isAuthenticated ? setIsAuthenticated(false) : setIsAuthenticated(true);
    };

    const logout = async () => {
        try{
            await fetch("http://localhost:9000/auth/logout", {
                headers: {
                            'Content-Type': 'application/json',
                            'Accept': 'application/json',
                },
                credentials: "include" // envoyer les cookies
            });
            setIsAuthenticated(false);
            setExpirationTime(0);
        } catch(error){
            console.log(error);
        };        
    }

    const refresh = async () => {
        try{
            // Réception de l'éventuel nouveau access token et update de la date d'expiration
            const response = await fetch("http://localhost:9000/auth/refresh", {
                headers: {
                            'Content-Type': 'application/json',
                            'Accept': 'application/json',
                },
                credentials: "include"
            });
            const text = await response.text();

            if (response.ok){
                const data = JSON.parse(text);
                setIsAuthenticated(true);
                setExpirationTime(Date.now() + data.accessExpiresIn);    
            } else {
                setIsAuthenticated(false);
                setExpirationTime(0);
            }
        } catch(error){
            console.log(error);
        };        
    }

    return {
        isAuthenticated: isAuthenticated,
        setIsAuthenticated: setIsAuthenticated, 
        toggleIsAuthenticated: toggleIsAuthenticated,
        logout: logout,
        refresh: refresh,
        expirationTime: expirationTime,
        setExpirationTime: setExpirationTime,
        isProfileCompleted: isProfileCompleted,
        setIsProfileCompleted: setIsProfileCompleted
    };
}

export function ThemeProvider({children}:PropsWithChildren){
    const lastIsAuthenticated = localStorage.getItem("isAuthenticated");
    const lastExpirationTime = localStorage.getItem("expirationTime");
    const lastIsProfileCompleted = localStorage.getItem("isProfileCompleted");
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(lastIsAuthenticated? lastIsAuthenticated === "true":false);
    const [expirationTime, setExpirationTime] = useState<number>(lastExpirationTime? Number(lastExpirationTime): 0);
    const [isProfileCompleted, setIsProfileCompleted] = useState<boolean>(lastIsProfileCompleted? lastIsProfileCompleted === "true" : false);

    return <ThemeContext.Provider value={{isAuthenticated, setIsAuthenticated, expirationTime, setExpirationTime, isProfileCompleted, setIsProfileCompleted}}>
                {children}
           </ThemeContext.Provider>
}
 