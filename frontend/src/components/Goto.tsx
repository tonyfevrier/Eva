import { Button } from "./Button"
import styles from "./Goto.module.css"

type GotoType = {
    label: string,
    href?: string,
    buttonLabel?: string,
    variant?: "default" | "export",
    className?: string
} & React.ButtonHTMLAttributes<HTMLButtonElement>

export function Goto({label, href="", buttonLabel="Cliquez ici", variant="default", className="", ...props}:GotoType){
    const variantClassName = variant === "export" ? styles.export : styles.container;
    const resolvedClassName = [variantClassName, className].filter(Boolean).join(" "); // utile pour combiner plusieurs classes de style

    if (href !== ""){
        return  <div className={resolvedClassName}>
                <p>{label}</p>
                <Button href={href} {...props}> {buttonLabel}</Button>
            </div>  
    } 
    return  <div className={resolvedClassName}>
                <p>{label}</p>
                <Button {...props}> {buttonLabel}</Button>
            </div>  
    
}