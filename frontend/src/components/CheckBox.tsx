import type { PropsWithChildren } from "react"
import styles from "./Input.module.css"

// Type pouvant contenir les attributs usuels de input et 3 aux autres attributs
type CheckboxProps = {
    title: string,
    onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void,
    options: Array<string>
} & React.HTMLAttributes<HTMLInputElement>

export function Checkbox({title, options, onChange = () => {}, ...props}:PropsWithChildren<CheckboxProps>){
    return  <div className={styles.formField}>
                <p>{title}</p>
                {options.map((option) => (
                    <label key={option} style={{display: 'block'}}>
                        <input type="checkbox"name="keywords" value={option} onChange={onChange} {...props}/>
                        {option}
                    </label>))}
            </div>
}
