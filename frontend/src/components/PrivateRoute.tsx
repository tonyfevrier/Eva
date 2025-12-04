import { useState, type ReactNode } from "react"



export function PrivateRoute({children}:PrivateRouteProps){ 
    /* il faudra récupérer les états du useContext */
    if (userConnected && !tokenExpired){
        return <>{children}</>
    } else {
        return <> 
                    <h2>Cette page est réservée aux personnes loggées</h2>
                    <a href="/login">Vous pouvez vous connecter ici</a>
               </>
    }
}