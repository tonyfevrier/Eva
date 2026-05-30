import { forwardRef, type Dispatch, type FormEventHandler, type SetStateAction } from "react"
import type { SendingStatus } from "../types/types"
import styles from "./Form.module.css"
import { Alert } from "./Alert"

type FormProps = {
    mapping: Record<string, boolean>, 
    sendingState: SendingStatus<any>,
    setSendingState: Dispatch<SetStateAction<SendingStatus<any>>>
    onSubmit: FormEventHandler
}

export const Form = forwardRef<HTMLFormElement, FormProps>(
    ({mapping, sendingState, setSendingState, onSubmit},ref) => {
    
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
                {sendingState.error !== null && <Alert message={sendingState.error} onClose={() => {setSendingState(prev => ({...prev, error: null}))}}/> } 
           </>
    }
);