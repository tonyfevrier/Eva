import { createContext, useContext, useEffect, useState, type Dispatch, type PropsWithChildren, type SetStateAction } from "react"

type ThemeContextType = {
    isAuthenticated: boolean,
    toggleIsAuthenticated: () => void,
    expirationTime: number,
    setExpirationTime: Dispatch<SetStateAction<number>>
}

const ThemeContext = createContext<ThemeContextType>({
    isAuthenticated: false,
    toggleIsAuthenticated: () => {},
    expirationTime: 0,
    setExpirationTime: () => {}
});

export function useTheme(){
    const {isAuthenticated, toggleIsAuthenticated, expirationTime, setExpirationTime} = useContext(ThemeContext);
    return {
        isAuthenticated: isAuthenticated,
        toggleIsAuthenticated: toggleIsAuthenticated,
        expirationTime: expirationTime,
        setExpirationTime: setExpirationTime
    };
}

export function ThemeProvider({children}:PropsWithChildren){
    const lastIsAuthenticated = localStorage.getItem("isAuthenticated");
    const lastExpirationTime = localStorage.getItem("expirationTime");
    const [isAuthenticated, setIsAuthenticated] = useState(lastIsAuthenticated? Boolean(lastIsAuthenticated):false);
    const [expirationTime, setExpirationTime] = useState(lastExpirationTime? Number(lastExpirationTime): 0);
    const toggleIsAuthenticated = () => {
        isAuthenticated ? setIsAuthenticated(false) : setIsAuthenticated(true);
    };

    useEffect(() => {
        localStorage.setItem("isAuthenticated", String(isAuthenticated));
        localStorage.setItem("expirationTime", String(expirationTime));
    }, [isAuthenticated]);

    return <ThemeContext.Provider value={{isAuthenticated, toggleIsAuthenticated, expirationTime, setExpirationTime}}>
                {children}
           </ThemeContext.Provider>
}