import { forwardRef, type FormEventHandler } from "react"
import type { SendingStatus } from "../types/types"

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
                            <div key={name}>
                                {name !== "password" ? <input type="text" placeholder={name} name={name} /> : <input type={name} placeholder={name} name={name}/>}
                                {mapping[name] && <p>Il faut remplir ce champ</p>}
                            </div>)}
                    <input type="submit" />
                </form>
                {sendingState.error !== null && <p> {sendingState.error} </p>}
           </>
    }
);