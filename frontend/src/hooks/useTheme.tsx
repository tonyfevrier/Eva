import { createContext, useContext, useState, type PropsWithChildren } from "react"


const ThemeContext = createContext({
    isAuthenticated: false,
    toggleIsAuthenticated: () => {}
});

export function useTheme(){
    const {isAuthenticated, toggleIsAuthenticated} = useContext(ThemeContext);
    return {
        isAuthenticated: isAuthenticated,
        toggleIsAuthenticated: toggleIsAuthenticated
    };
}

export function ThemeProvider({children}:PropsWithChildren){
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const toggleIsAuthenticated = () => {
        isAuthenticated ? setIsAuthenticated(false) : setIsAuthenticated(true);
    };
    return <ThemeContext.Provider value={{isAuthenticated, toggleIsAuthenticated}}>
                {children}
           </ThemeContext.Provider>
}