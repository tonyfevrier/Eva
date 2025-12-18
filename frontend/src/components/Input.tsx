import styles from "./Input.module.css"

type InputProps = {
    title: string,
    name: string,
    value: string,
    onChange: (event: React.ChangeEvent<HTMLInputElement>) => void,
    type?: string,
    disabled?: boolean,
    variant?: string,
}

export function Input({title, name, value, onChange, type="text", disabled= true, variant="noErrorMsg"}:InputProps){
    if (variant === "noErrorMsg"){
        return  <div className={styles.formField}>
                    <p>{title}</p>
                    <input type={type} value={value} name={name}  disabled={disabled} onChange={onChange}/>
                </div>
    } else {
        return  <div className={styles.container}>
                    <div className={styles.formField}>
                        <p>{title}</p>
                        <input type={type} value={value} name={name}  disabled={disabled} onChange={onChange}/>
                    </div>
                    {value === "" && <p> Ce champ doit être rempli </p>}
                </div>
    }   
}