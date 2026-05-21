import React, { useState } from "react"
import { Button } from "./Button";
import styles from "./FilteredInput.module.css"

type FilterArgs = {
    items: Array<Record<string, any>>,
    className?: string
}

export function FilteredInput({items, className=styles.body}: FilterArgs){
    /* Input : dont une liste d'items apparaît quand une lettre est inscrite
    qui permet de cliquer sur un des items et qui quand on clique, enlève la liste 
    et affiche l'item dans l'input. La liste disparait aussi si on clique ailleurs sur la page après avoir rempli l'input*/
    const [search, setSearch] = useState<string>("");
    const [isListApparent, setIsListApparent] = useState<boolean>(false);
    const filteredItemList = items.filter(item =>
        item.name.toLowerCase().includes(search.toLowerCase())
    )

    const handleChange = (e:React.ChangeEvent<HTMLInputElement>) => {
        const newInputValue = e.target.value;
        setSearch(e.target.value);
        setIsListApparent(newInputValue !== "");
    }

    const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
        setSearch(e.currentTarget.value);
        setIsListApparent(false);
    }

    const leaveInput = () => {
        setIsListApparent(false);
    }

    return  <div className={className}>
                <input type="text" value={search} onChange={handleChange} onBlur={leaveInput} placeholder="Vous pouvez filtrer les établissements ici"/>
                {isListApparent && <>
                    {filteredItemList.length > 0 && <p className={styles.beforeItems}>Clique sur un des choix suivants</p>}
                    {filteredItemList.map(item => (
                        <Button className={styles.item} id={item.id} key={item.id} value={item.name} onMouseDown={handleClick}>{item.name}</Button>
                    ))}
                </>}
            </div> 
}