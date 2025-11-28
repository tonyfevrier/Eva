import type { FormBoolean } from "../types/types"

type FormProps = {
    formState: FormBoolean<any>, 
    names: Array<string>
}

export function Form({InputsToStateMapping}){
    return <>
                <form ref={registerForm} onSubmit={handleClick}>
                    {names.map(name => <div key={name}>
                                            <input type="text" placeholder={name} name={name}/>
                                            {InputsToStateMapping[name] && <p>Il faut remplir ce champ</p>}
                                        </div>)}
                    <input type="text" placeholder="firstname" name="firstname"/>
                    {formState.isFirstnameEmpty && <p>Il faut remplir ce champ</p>}
                    <input type="text" placeholder="lastname" name="lastname"/>
                    {formState.isLastnameEmpty && <p>Il faut remplir ce champ</p>}
                    <input type="text" placeholder="mail" name="mail"/>
                    {formState.isUsernameEmpty && <p>Il faut remplir ce champ</p>}
                    <input type="password" placeholder="mot de passe" name="password"/>
                    {formState.isPasswordEmpty && <p>Il faut remplir ce champ</p>}
                    <input type="submit" />
                </form>
                {formState.error !== null && <p> {formState.error} </p>}
           </>
}