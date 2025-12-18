import { Button } from "./Button"
import styles from "./Goto.module.css"

type GotoType = {
    label: string,
    href: string,
    buttonLabel?: string
}

export function Goto({label, href, buttonLabel="Cliquez ici"}:GotoType){
    return  <div className={styles.container}>
                <p>{label}</p>
                <Button href={href}> {buttonLabel}</Button>
            </div>  
}