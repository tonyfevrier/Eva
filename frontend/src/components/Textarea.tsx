import type { PropsWithChildren } from "react"
import styles from "./Textarea.module.css"

// Type pouvant contenir les attributs usuels de input et 3 aux autres attributs
type TextProps = {
    title: string,
    onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void,
    variant?: string,
} & React.TextareaHTMLAttributes<HTMLTextAreaElement>

export function Textarea({title, onChange = () => {}, variant="noErrorMsg", ...props}:PropsWithChildren<TextProps>){
    if (variant === "noErrorMsg"){
        return  <div className={styles.formField}>
                    <p>{title}</p>
                    <textarea onChange={onChange} {...props} />
                </div>
    } else {
        return  <div className={styles.container}>
                    <div className={styles.formField}>
                        <p>{title}</p>
                        <textarea onChange={onChange} {...props}/>
                    </div>
                    {props?.value === "" && <p> Ce champ doit être rempli </p>}
                </div>
    }   
}
