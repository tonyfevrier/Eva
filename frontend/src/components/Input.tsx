import type { PropsWithChildren } from "react"
import styles from "./Input.module.css"

// Type pouvant contenir les attributs usuels de input et 3 aux autres attributs
type InputProps = {
    title: string,
    onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void,
    variant?: string,
} & React.InputHTMLAttributes<HTMLInputElement>

export function Input({title, onChange = () => {}, variant="noErrorMsg", ...props}:PropsWithChildren<InputProps>){
    if (variant === "noErrorMsg"){
        return  <div className={styles.formField}>
                    <p>{title}</p>
                    <input onChange={onChange} {...props} />
                </div>
    } else {
        return  <div className={styles.container}>
                    <div className={styles.formField}>
                        <p>{title}</p>
                        <input onChange={onChange} {...props}/>
                    </div>
                    {props?.value === "" && <p> Ce champ doit être rempli </p>}
                </div>
    }   
}
