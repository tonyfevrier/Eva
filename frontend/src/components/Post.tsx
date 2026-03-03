import styles from "./Post.module.css"

type PostType = {
    title: string,
    text: string,
    notices: Array<string>,
}

export function Post({title, text, notices}:PostType){
    return  <div className={styles.container}>
                <p>{title}</p>
                <p>{text}</p>
                {notices.map(notice => <p key={notice} className={styles.notice}>{notice}</p>)}
            </div>
}