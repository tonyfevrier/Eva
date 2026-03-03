import React, { useState, type Dispatch, type SetStateAction } from "react"
import { Button } from "./Button";
import { FilteredSelector } from "./FilteredSelector";
import styles from "./ModalFilteredSelector.module.css"

type FilterArgs = {
    title: string,
    items: Array<Record<string, any>>,
    onClick: (e:React.MouseEvent<HTMLButtonElement>) => void
    setIsModalOpen: Dispatch<SetStateAction<boolean>>
}

export function ModalFilteredSelector({title, items, onClick, setIsModalOpen}: FilterArgs){
    /* Modal avec une barre de filtre en haut et une liste de boutons cliquables.
    value est la valeur qui est mise dans l'input destiné à être récupérée*/

    return  <>
                <div className={styles.modalOverlay} onClick={() => setIsModalOpen(false)}>
                    <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
                        <div className={styles.modalHeader}>
                            <h2>{title}</h2>
                            <button className={styles.closeButton} onClick={() => setIsModalOpen(false)}>×</button>
                        </div>
                        <FilteredSelector items={items} onClick={onClick} className={styles.modalBody}/>
                    </div>
                </div>
            </>
}