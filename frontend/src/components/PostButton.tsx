import { useState } from "react"
import { Button } from "./Button"
import styles from "./PostButton.module.css"

type PostButtonType = {
    title: string,
    text: string,
    notices: Array<string>,
}

export function PostButton({title, text, notices}:PostButtonType){
    const [isModalOpen, setIsModalOpen] = useState(false);
    
    return  <>
                <Button className={styles.container} onClick={() => setIsModalOpen(true)}>
                    <p>{title}</p>
                    <p>{text}</p>
                </Button>
                
                {isModalOpen && (
                    <div className={styles.modalOverlay} onClick={() => setIsModalOpen(false)}>
                        <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
                            <div className={styles.modalHeader}>
                                <h2>{title}</h2>
                                <button className={styles.closeButton} onClick={() => setIsModalOpen(false)}>×</button>
                            </div>
                            <div className={styles.modalBody}>
                                <p>{text}</p>
                                {notices.map(notice => <p key={notice} className={styles.notice}> {notice}</p>)}
                            </div>
                            {/* <div className={styles.modalFooter}>
                                <Button onClick={(e:React.MouseEvent<HTMLButtonElement>) => {  setIsModalOpen(false) }}>Choisir ce protocole</Button>
                            </div> */}
                        </div>
                    </div>
                )}
            </>
}