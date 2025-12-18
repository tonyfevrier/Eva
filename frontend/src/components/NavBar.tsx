import type { PropsWithChildren } from "react";
import styles from "./NavBar.module.css";

export function NavBar({children}:PropsWithChildren){
    return <nav className={styles.navbar}> {children} </nav>
}