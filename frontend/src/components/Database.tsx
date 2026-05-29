import { useState, type Dispatch, type SetStateAction } from "react";
import type { Data } from "../pages/ExperimentationListPage"
import { apiFetch } from "../utils/apiFetch";
import { Button } from "./Button";
import styles from "./Database.module.css"
import { useNavigate } from "react-router-dom"
import { exportFile } from "../utils/request/fileExport";
import { Spinner } from "./Spinner";

export function Database({experimentations}:{experimentations: Array<Data>}){
    const navigate = useNavigate();
    const [error, setError] = useState<Error|null>(null);
    const [loading, setLoading] = useState<Boolean>(false);

    const download = (e:React.MouseEvent<HTMLButtonElement>) => {
        sendDownloadRequest(e.currentTarget.id, setError, setLoading);
        e.stopPropagation();
    }
    return <>
             <table className={styles.table}>
                <thead>
                    <tr>
                        <th className={styles.responsiveDisappear}>Expérimentation</th>
                        <th>Mots clés</th>
                        <th>Discipline</th>
                        <th className={styles.responsiveDisappear}>Année scolaire</th>
                        <th>Institution</th>
                        <th className={styles.responsiveDisappear}>Pratique pédagogique</th>
                        <th className={styles.responsiveDisappear}>L'expérimentation a fonctionné</th>
                        <th>Télécharger</th>
                    </tr>
                </thead>
                <tbody>
                    {experimentations.map(expe => (
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
                            <td className={styles.responsiveDisappear}>{expe.expeWorked}</td>
                            <td><Button id={expe.id} onClick={download}>Cliquez</Button></td>
                        </tr>
                    ))}
                </tbody>
              </table>
              {loading &&<Spinner/>}
              {error?.message && <p>error?.message</p>}
            </>;
}

async function sendDownloadRequest(id: string, setError: Dispatch<SetStateAction<Error|null>>, setLoading: Dispatch<SetStateAction<Boolean>>){
    setLoading(true);
    const response = await apiFetch(`/pdf/getPdf/${id}`, {
            headers: {
                'Content-Type': 'application/json',
            },
            method: "get",
        }).catch(error => {
            setError(error);
            throw error;
        })

        if (response.ok){
            exportFile(response, `experimentation_${id}.pdf`);
        } else {
            setError(new Error(`Erreur ${response.status}: ${response.statusText}`));
        }
        setLoading(false);
}