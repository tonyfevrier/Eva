import { forwardRef, type FormEventHandler } from "react"
import type { SendingStatus } from "../types/types"
import styles from "./Form.module.css"

type FormProps = {
    mapping: Record<string, boolean>, 
    sendingState: SendingStatus<any>,
    onSubmit: FormEventHandler
}

export const Form = forwardRef<HTMLFormElement, FormProps>(
    ({mapping, sendingState, onSubmit},ref) => {
    return <>
                <form ref={ref} onSubmit={onSubmit}>
                    {Object.keys(mapping).map(name => 
                            <div key={name} className={styles.formField}>
                                {!name.startsWith("password") && <input type="text" placeholder={name} name={name} />}
                                {name.startsWith("password") &&  <input type="password" placeholder={name} name={name}/>}
                                {mapping[name] && <p>Il faut remplir ce champ</p>}
                            </div>)}
                    <button type="submit">Soumettre</button>
                </form>
                {sendingState.error !== null && <p> {sendingState.error} </p>}
           </>
    }
);