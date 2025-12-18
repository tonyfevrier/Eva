/**
 * Composant générique: au travers de props, on peut passer href, onClick,
 * ce qui permet de tenir compte de la variété des attributs possibles des boutons.
 * En particulier, children est automatiquement inclus dans props.
 * @param {} param0 
 * @returns 
 */

export function Button({...props}){
    const newProps = {
        ...props,
    };

    if (props.href){
        return <a className="btn-primary" {...newProps}></a>   
    }

    return <button {...newProps}></button>
}
