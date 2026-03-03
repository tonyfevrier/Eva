import { Button } from "./Button";
import styles from "./Cloud.module.css"

type CloudProps = {
    title: string,
    options: Map<string, Boolean>
    onClick: (e: React.MouseEvent<HTMLButtonElement>) => void; 
}

export function Cloud({title, options, onClick}:CloudProps){
    /*Nuage de boutons qui peut representer un nuage de mots-clés sur lesquels 
    l'utilisateur peut cliquer. options est un Map associant à chaque clé un booléen */
    return  <div className={styles.container}>
                <p>{title}</p>
                <div className={styles.buttons}>
                    { Array.from(options.keys()).map(option => {
                        return <Button key={option} 
                                       onClick={onClick}
                                       style={options.get(option)?{backgroundColor: '#b2f9a4', color: '#0a921a'}:{}}
                                       >{option}</Button>
                        })}
                </div>
            </div>
}