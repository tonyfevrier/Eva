import type { PropsWithChildren } from "react";
import styles from "./Datalist.module.css"

type DataListType = {
    title: string,
    onChange: (e:React.ChangeEvent<HTMLInputElement>) => void
}  & React.InputHTMLAttributes<HTMLInputElement>

export function Datalist({title, children, onChange, ...props}:PropsWithChildren<DataListType>){
    return  <div className={styles.formField}>
                <p>{title}</p>
                <input list="list" onChange={onChange} {...props}/>
                <datalist id="list">
                    {children}
                </datalist>
            </div>
}