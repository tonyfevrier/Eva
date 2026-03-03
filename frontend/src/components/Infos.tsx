import styles from "./Infos.module.css"

type InfoData = {
    title: string,
    info: string
}

export function Infos({title, info}:InfoData){
    return <div className={styles.container}>
                <p>{title}</p>
                <p>{info}</p>
           </div>
}