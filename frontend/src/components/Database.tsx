import type { Data } from "../pages/ExperimentationListPage"
import styles from "./Database.module.css"
import { useNavigate } from "react-router-dom"

export function Database({experimentations}:{experimentations: Array<Data|undefined>}){
    const navigate = useNavigate();

    return <table className={styles.table}>
                <thead>
                    <tr>
                        <th className={styles.responsiveDisappear}>Numéro de l'expérimentation</th>
                        <th>Mots clés</th>
                        <th>Discipline</th>
                        <th className={styles.responsiveDisappear}>Année scolaire</th>
                        <th>Institution</th>
                        <th className={styles.responsiveDisappear}>Pratique pédagogique</th>
                        <th className={styles.responsiveDisappear}>En cours/Terminée</th>
                        <th className={styles.responsiveDisappear}>L'expérimentation a fonctionné</th>
                    </tr>
                </thead>
                <tbody>
                    {experimentations.map(expe => (expe !== undefined &&
                        <tr className={styles.tableLines} key={expe.id} onClick={() => navigate(`/experimentationSummary/${expe.id}`)} style={{cursor: 'pointer'}}>
                            <td className={styles.responsiveDisappear}>{expe.id}</td>
                            {
                                expe.personalKeywords !== ""? 
                                <td>{expe.keywords.concat(expe.personalKeywords).join(", ")}</td> :
                                <td>{expe.keywords.join(", ")}</td>
                            }
                            <td>{expe.studyField}</td>
                            <td className={styles.responsiveDisappear}>{expe.yearOfStudy}</td>
                            <td>{expe.institutionName}</td>
                            <td className={styles.responsiveDisappear}>{expe.newPedagogy}</td>
                            <td className={styles.responsiveDisappear}>{expe.inProgress ? "En cours" : "Terminée"}</td>
                            <td className={styles.responsiveDisappear}>{expe.expeWorked}</td>
                        </tr>
                    ))}
                </tbody>
            </table>;
}