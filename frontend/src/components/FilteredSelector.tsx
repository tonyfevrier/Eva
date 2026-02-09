import React, { useState } from "react"
import { Button } from "./Button";
import styles from "./FilteredSelector.module.css"

type FilterArgs = {
    title: string,
    items: Array<Record<string, any>>,
    onClick: (e:React.MouseEvent<HTMLButtonElement>) => void
}

export function FilteredSelector({items, onClick}: FilterArgs){
    /* Barre de filtre en haut et une liste de boutons cliquables.
    value est la valeur qui est mise dans l'input destiné à être récupérée*/
    const [search, setSearch] = useState("");

    return  <div className={styles.body}>
                <input type="text" value={search} onChange={e => setSearch(e.target.value)} placeholder="Vous pouvez filtrer les établissements ici"/>
                {items.map(item => item.name.toLowerCase().includes(search.toLowerCase()) && <Button className={styles.item} id={item.id} key={item.id} onClick={onClick}>{item.name}</Button>)}
            </div> 
}