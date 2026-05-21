import React, { useRef, useState } from "react"
import { Button } from "./Button";
import styles from "./FilteredInput.module.css"

type FilterArgs = {
    items: Array<string>,
    listTitle?: string,
    onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void,
    className?: string
} & React.InputHTMLAttributes<HTMLInputElement>

export function FilteredInput({items, listTitle="Cliquez sur un des choix suivantes", onChange = () => {}, className=styles.body, ...props}: FilterArgs){
    /* Input : dont une liste d'items apparaît quand une lettre est inscrite
    qui permet de cliquer sur un des items et qui quand on clique, enlève la liste 
    et affiche l'item dans l'input. La liste disparait aussi si on clique ailleurs sur la page après avoir rempli l'input*/
    const [search, setSearch] = useState<string>("");
    const [isListApparent, setIsListApparent] = useState<boolean>(false);
    const inputRef = useRef<HTMLInputElement>(null);
    const filteredItemList = items.filter(item =>
        item.toLowerCase().includes(search.toLowerCase())
    )

    const applyInputValue = (newInputValue: string, shouldShowList: boolean) => {
        setSearch(newInputValue);
        setIsListApparent(shouldShowList);
    }

    const handleChange = (e:React.ChangeEvent<HTMLInputElement>) => {
        const newInputValue = e.target.value;
        applyInputValue(newInputValue, newInputValue !== "");
        onChange(e);
    }

    const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
        const newInputValue = e.currentTarget.value;
        applyInputValue(newInputValue, false);

        if (inputRef.current) {
            inputRef.current.value = newInputValue;
            onChange({
                target: inputRef.current,
                currentTarget: inputRef.current
            } as React.ChangeEvent<HTMLInputElement>);
        }
    }

    const leaveInput = () => {
        setIsListApparent(false);
    }

    return  <div className={className}>
                <input ref={inputRef} type="text" value={search} onChange={handleChange} onBlur={leaveInput} {...props}/>
                {isListApparent && <>
                    <div className={styles.list}>
                        {filteredItemList.length > 0 && <p className={styles.beforeItems}>{listTitle}</p>}
                        {filteredItemList.map(item => (
                            <Button className={styles.item} key={item} value={item} onMouseDown={handleClick}>{item}</Button>
                        ))}
                    </div>
                </>}
            </div> 
}