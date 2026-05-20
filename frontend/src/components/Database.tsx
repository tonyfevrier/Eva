import type { Data } from "../pages/ExperimentationListPage"
import styles from "./Database.module.css"
import { useNavigate } from "react-router-dom"

export function Database({experimentations}:{experimentations: Array<Data>}){
    const navigate = useNavigate();

    return <table className={styles.table}>
                <thead>
                    <tr>
                        <th>Expérimentation id</th>
                        <th>Mots clés</th>
                        <th>Discipline</th>
                        <th>Année scolaire</th>
                        <th>Institution</th>
                        <th>Pratique pédagogique</th>
                        <th>En cours/Terminée</th>
                        <th>A fonctionné</th>
                    </tr>
                </thead>
                <tbody>
                    {experimentations.map(expe => (
                        <tr className={styles.tableLines} key={expe.id} onClick={() => navigate(`/experimentationSummary/${expe.id}`)} style={{cursor: 'pointer'}}>
                            <td>{expe.id}</td>
                            <td>Mots clés à gérer</td>
                            <td>{expe.teachingTitle}</td>
                            <td>{expe.yearOfStudy}</td>
                            <td>{expe.newPedagogy}</td>
                            <td>{expe.inProgress ? "En cours" : "Terminée"}</td>
                            <td>{expe.expeWorked}</td>
                        </tr>
                    ))}
                </tbody>
            </table>;
}