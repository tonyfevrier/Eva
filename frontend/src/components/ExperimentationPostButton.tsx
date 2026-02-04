import { Button } from "./Button";
import styles from "./ExperimentationPostButton.module.css";

export type Data = {
    id: string,
    institutionName: string,
    yearOfStudy: string,
    teachingTitle: string,
    keywords: Array<string>,
    personalKeywords: string
}

type ExpeData = {
    data: Data
}

export function ExperimentationPostButton({data}:ExpeData){
    return  <Button href={`/application/experimentationSummary/${data.id}`} className={styles.container}>
                <h5>Expérimentation</h5>
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