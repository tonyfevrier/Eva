import type { PropsWithChildren } from "react";

export function NavBar({children}:PropsWithChildren){
    return <nav> {children} </nav>
}