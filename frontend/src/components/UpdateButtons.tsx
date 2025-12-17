type ButtonDisabler = {
    toggleButton: boolean,
    handleToggleButton: () => void,
    text?: string
}

export function UpdateButtons({toggleButton, handleToggleButton, text="Modifier"}:ButtonDisabler){
    return <div className="profile-modify">
                {!toggleButton && <button type="button" onClick={handleToggleButton}> {text}</button>}
                {toggleButton && <button type="button" onClick={handleToggleButton}> Annuler les modifications</button>}
                <button disabled={!toggleButton}>Sauvegarder</button>
            </div>
}