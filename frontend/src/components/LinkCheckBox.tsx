import type { PropsWithChildren } from "react"
import styles from "./LinkCheckbox.module.css"
import { Button } from "./Button"

// Type pouvant contenir les attributs usuels de input et 3 aux autres attributs
type CheckboxProps = {
    title: string,
    onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void,
    onButtonClick?: (event: React.MouseEvent<HTMLButtonElement>) => void,
    options: Array<string>,
} & React.HTMLAttributes<HTMLInputElement>

export function LinkCheckbox({title, options, onChange = () => {}, onButtonClick = () => {}, ...props}:PropsWithChildren<CheckboxProps>){
    return  <div className={styles.checkbox}>
                <p>{title}</p>
                {options.map((option) => (
                    <label key={option}>
                        <Button onClick={onButtonClick} value={option}> {option}</Button>
                        <input type="checkbox"name="keywords" value={option} onChange={onChange} {...props}/>
                    </label>))}
            </div>
}
