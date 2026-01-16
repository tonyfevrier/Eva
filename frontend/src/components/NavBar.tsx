import type { PropsWithChildren } from "react";
import styles from "./NavBar.module.css";

interface NavBarProps extends PropsWithChildren{
    variant?: "default" | "primary" | "secondary";
};

export function NavBar({children, variant="default"}:NavBarProps){
    return <nav className={`${styles.navbar} ${styles[variant]}`}> {children} </nav>

}