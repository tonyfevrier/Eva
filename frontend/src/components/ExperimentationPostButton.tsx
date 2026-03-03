import { Button } from "./Button";
import styles from "./ExperimentationPostButton.module.css";

export type Data = {
    id: string,
    institutionName: string,
    yearOfStudy: string,
    teachingTitle: string,
    keywords: Array<string>,
    personalKeywords: string,
    inProgress: boolean,
    studyField: string
}

type ExpeData = {
    data: Data
}

export function ExperimentationPostButton({data}:ExpeData){
    return  <Button href={`/experimentationSummary/${data.id}`} className={styles.container}>
                <div className={styles.title}>
                    <h5>Expérimentation</h5>
                    {data.inProgress ? <p className={styles.inProgress}>En cours</p>: <p className={styles.finished}>Terminée</p>}
                </div>
                <div className={styles.header}>
                    <p>{data.institutionName}</p>
                    <p>{data.yearOfStudy}</p>
                </div>
                <p>{data.teachingTitle}</p>
                <div className={styles.footer}>
                    {data.keywords.map(word => <p key={word}>{word}</p>)}
                    {data.personalKeywords !== "" && <p className={styles.personalKeywords}>{data.personalKeywords}</p>}
                </div>
            </Button>
}