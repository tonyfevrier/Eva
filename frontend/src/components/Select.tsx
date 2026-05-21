import type { PropsWithChildren } from "react"
import styles from "./Select.module.css"

// Type pouvant contenir les attributs usuels de input et 3 aux autres attributs
type SelectProps = {
    title: string,
    onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void,
    className?: string
} & React.SelectHTMLAttributes<HTMLSelectElement>

export function Select({title, onChange = () => {}, className=styles.formField, children, ...props}:PropsWithChildren<SelectProps>){
    return  <div className={className}>
                <p>{title}</p>
                <select onChange={onChange as any} {...props}>
                    {children}
                </select>
            </div>
}
