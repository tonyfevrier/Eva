import { Button } from "./Button";
import styles from "./Alert.module.css";

type AlertItems = {
    title: string,
    message: string,
    variant?: string,
    onClose: () => void 
}

export function Alert({title, message, variant="error", onClose}: AlertItems){
    const variantClass = variant === "error" ? styles.error : styles.success;

    return (
        <div className={styles.overlay} role="alertdialog" aria-modal="true" aria-labelledby="alert-title" aria-describedby="alert-message">
            <div className={`${styles.alertCard} ${variantClass}`}>
                <h2 id="alert-title" className={styles.title}>{title}</h2>
                <p id="alert-message" className={styles.message}>{message}</p>
                <div className={styles.actions}>
                    <Button onClick={onClose}>OK</Button>
                </div>
            </div>
        </div>
    );
}