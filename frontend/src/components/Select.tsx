import type { PropsWithChildren } from "react"
import styles from "./Input.module.css"

// Type pouvant contenir les attributs usuels de input et 3 aux autres attributs
type SelectProps = {
    title: string,
    onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void,
} & React.SelectHTMLAttributes<HTMLSelectElement>

export function Select({title, onChange = () => {}, children, ...props}:PropsWithChildren<SelectProps>){
    return  <div className={styles.formField}>
                <p>{title}</p>
                <select onChange={onChange as any} {...props}>
                    {children}
                </select>
            </div>
}
