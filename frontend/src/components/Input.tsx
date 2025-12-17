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
        return  <div>
                <p>{title}</p>
                <input type={type} value={value} name={name}  disabled={disabled} onChange={onChange}/>
            </div>
    } else {
        return  <div>
                    <p>{title}</p>
                    <input type={type} value={value} name={name}  disabled={disabled} onChange={onChange}/>
                    {value === "" && <p> Ce champ doit être rempli </p>}
                </div>
    }   
}