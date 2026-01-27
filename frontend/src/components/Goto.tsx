import { Button } from "./Button"
import styles from "./Goto.module.css"

type GotoType = {
    label: string,
    href: string,
    buttonLabel?: string,
    className?: string
}

export function Goto({label, href, buttonLabel="Cliquez ici", className=""}:GotoType){
    return  <div className={className===""? styles.container:className}>
                <p>{label}</p>
                <Button href={href}> {buttonLabel}</Button>
            </div>  
}