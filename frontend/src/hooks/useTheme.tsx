import { createContext, useContext, useState, type Dispatch, type PropsWithChildren, type SetStateAction } from "react"

type ThemeContextType = {
    isAuthenticated: boolean,
    setIsAuthenticated: Dispatch<SetStateAction<boolean>>,
    expirationTime: number,
    setExpirationTime: Dispatch<SetStateAction<number>>
}

const ThemeContext = createContext<ThemeContextType>({
    isAuthenticated: false,
    setIsAuthenticated: () => false,
    expirationTime: 0,
    setExpirationTime: () => {}
});

export function useTheme(){
    const {isAuthenticated, setIsAuthenticated, expirationTime, setExpirationTime} = useContext(ThemeContext);
    
    const toggleIsAuthenticated = () => {
        isAuthenticated ? setIsAuthenticated(false) : setIsAuthenticated(true);
    };

    const logout = async () => {
        try{
            await fetch("http://localhost:9000/api/logout", {
                headers: {
                            'Content-Type': 'application/json',
                            'Accept': 'application/json',
                },
                credentials:'include'
                });

            setIsAuthenticated(false);
            setExpirationTime(0);
        } catch(error){
            console.log(error);
        };        
    }

    return {
        isAuthenticated: isAuthenticated,
        setIsAuthenticated: setIsAuthenticated, 
        toggleIsAuthenticated: toggleIsAuthenticated,
        logout: logout,
        expirationTime: expirationTime,
        setExpirationTime: setExpirationTime
    };
}

export function ThemeProvider({children}:PropsWithChildren){
    const lastIsAuthenticated = localStorage.getItem("isAuthenticated");
    const lastExpirationTime = localStorage.getItem("expirationTime");
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(lastIsAuthenticated? lastIsAuthenticated === "true":false);
    const [expirationTime, setExpirationTime] = useState<number>(lastExpirationTime? Number(lastExpirationTime): 0);
    
    return <ThemeContext.Provider value={{isAuthenticated, setIsAuthenticated, expirationTime, setExpirationTime}}>
                {children}
           </ThemeContext.Provider>
}
 