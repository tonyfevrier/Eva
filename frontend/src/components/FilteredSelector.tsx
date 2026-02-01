import React, { useState, type Dispatch, type SetStateAction } from "react"
import { Button } from "./Button";
import styles from "./FilteredSelector.module.css"

type FilterArgs = {
    title: string,
    items: Array<Record<string, any>>,
    onClick: (e:React.MouseEvent<HTMLButtonElement>) => void
    setIsModalOpen: Dispatch<SetStateAction<boolean>>
}

export function FilteredSelector({title, items, onClick, setIsModalOpen}: FilterArgs){
    /* Modal avec une barre de filtre en haut et une liste de boutons cliquables.
    value est la valeur qui est mise dans l'input destiné à être récupérée*/
    const [search, setSearch] = useState("");

    return  <>
                <div className={styles.modalOverlay} onClick={() => setIsModalOpen(false)}>
                    <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
                        <div className={styles.modalHeader}>
                            <h2>{title}</h2>
                            <button className={styles.closeButton} onClick={() => setIsModalOpen(false)}>×</button>
                        </div>
                        <div className={styles.modalBody}>
                            <input type="text" value={search} onChange={e => setSearch(e.target.value)} placeholder="Vous pouvez filtrer les établissements ici"/>
                            {items.map(item => item.name.toLowerCase().includes(search.toLowerCase()) && <Button className={styles.item} id={item.id} key={item.id} onClick={onClick}>{item.name}</Button>)}
                        </div> 
                    </div>
                </div>
            </>
}