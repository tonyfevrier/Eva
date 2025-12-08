import { createContext, useContext, useEffect, useState, type Dispatch, type PropsWithChildren, type SetStateAction } from "react"
import { useHandleAuth } from "./useHandleAuth";

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
    const {isAuthenticated, toggleIsAuthenticated, expirationTime, setExpirationTime} = useHandleAuth();    

    return <ThemeContext.Provider value={{isAuthenticated, toggleIsAuthenticated, expirationTime, setExpirationTime}}>
                {children}
           </ThemeContext.Provider>
}