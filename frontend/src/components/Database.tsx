import type { Data } from "../pages/ExperimentationListPage"
import styles from "./Database.module.css"
import { useNavigate } from "react-router-dom"

export function Database({experimentations}:{experimentations: Array<Data|undefined>}){
    const navigate = useNavigate();

    return <table className={styles.table}>
                <thead>
                    <tr>
                        <th>Numéro de l'expérimentation</th>
                        <th>Mots clés</th>
                        <th>Discipline</th>
                        <th>Année scolaire</th>
                        <th>Institution</th>
                        <th>Pratique pédagogique</th>
                        <th>En cours/Terminée</th>
                        <th>L'expérimentation a fonctionné</th>
                    </tr>
                </thead>
                <tbody>
                    {experimentations.map(expe => (expe !== undefined &&
                        <tr className={styles.tableLines} key={expe.id} onClick={() => navigate(`/experimentationSummary/${expe.id}`)} style={{cursor: 'pointer'}}>
                            <td>{expe.id}</td>
                            <td>{expe.keywords.join(", ") + ", " + expe.personalKeywords}</td>
                            <td>{expe.studyField}</td>
                            <td>{expe.yearOfStudy}</td>
                            <td>{expe.institutionName}</td>
                            <td>{expe.newPedagogy}</td>
                            <td>{expe.inProgress ? "En cours" : "Terminée"}</td>
                            <td>{expe.expeWorked}</td>
                        </tr>
                    ))}
                </tbody>
            </table>;
}