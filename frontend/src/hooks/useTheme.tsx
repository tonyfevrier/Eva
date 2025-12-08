import { createContext, useContext, useState, type Dispatch, type PropsWithChildren, type SetStateAction } from "react"

type ThemeContextType = {
    isAuthenticated: boolean,
    toggleIsAuthenticated: () => void,
    expirationTimestamp: number,
    setExpirationTimestamp: Dispatch<SetStateAction<number>>
}

const ThemeContext = createContext<ThemeContextType>({
    isAuthenticated: false,
    toggleIsAuthenticated: () => {},
    expirationTimestamp: 0,
    setExpirationTimestamp: () => {}
});

export function useTheme(){
    const {isAuthenticated, toggleIsAuthenticated, expirationTimestamp, setExpirationTimestamp} = useContext(ThemeContext);
    return {
        isAuthenticated: isAuthenticated,
        toggleIsAuthenticated: toggleIsAuthenticated,
        expirationTimestamp: expirationTimestamp,
        setExpirationTimestamp: setExpirationTimestamp
    };
}

export function ThemeProvider({children}:PropsWithChildren){
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [expirationTimestamp, setExpirationTimestamp] = useState(0);
    const toggleIsAuthenticated = () => {
        isAuthenticated ? setIsAuthenticated(false) : setIsAuthenticated(true);
    };

    return <ThemeContext.Provider value={{isAuthenticated, toggleIsAuthenticated, expirationTimestamp, setExpirationTimestamp}}>
                {children}
           </ThemeContext.Provider>
}