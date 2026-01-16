import { Button } from "./Button";
import styles from "./Cloud.module.css"

type CloudProps = {
    title: string,
    options: Array<string>,
    buttonClicked?: Array<string>
}

export function Cloud({title, options, buttonClicked=[""]}:CloudProps){
    return  <div className={styles.container}>
                <p>{title}</p>
                <div className={styles.buttons}>
                    { options.map(option => {
                        if (buttonClicked.includes(option)){
                            return <Button style={{backgroundColor: '#ffebee', color: '#c62828'}}>{option}</Button>
                        } else {
                            return <Button>{option}</Button>
                        }})}
                </div>
            </div>
}