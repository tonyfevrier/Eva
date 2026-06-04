import styles from './Spinner.module.css'

export function Spinner(){
    return <div className="modal show" tabIndex={-1} style={{display: "block"}}>
                <div className="modal-dialog modal-dialog-centered">
                    <div className={"modal-content"}>
                        <div className={styles.body}>
                            <div className={`spinner-border ${styles.spinner}`} role="status">
                                <span className="visually-hidden"></span>
                            </div>
                            <p className={styles.label}>Chargement en cours</p>
                        </div>
                    </div>
                </div>
            </div>
}