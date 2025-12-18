import { useTheme } from "../hooks/useTheme"
import type { PrivateRouteProps } from "../types/types";

export function PrivateRoute({children}:PrivateRouteProps){
    const {isAuthenticated, expirationTime} = useTheme();
    const tokenExpired = Date.now() > expirationTime;
    if (isAuthenticated && !tokenExpired){
        return <>{children}</>
    } else {
        return <> 
                    <h2>Cette page est réservée aux personnes loggées</h2>
                    <a href="/login">Vous pouvez vous connecter ici</a>
               </>
    }
 
}

