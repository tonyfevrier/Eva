import { Button } from "./Button";
import styles from "./ExperimentationPostButton.module.css";
import { type Data} from "../pages/ExperimentationListPage";


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
                    <p>{data.studyField}</p>
                </div>
                <div className={styles.footer}>
                    {data.keywords.map(word => <p key={word}>{word}</p>)}
                    {data.personalKeywords !== "" && <p className={styles.personalKeywords}>{data.personalKeywords}</p>}
                </div>
            </Button>
}