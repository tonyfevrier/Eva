import { useFetch } from "../hooks/useFetch";
import { useNavigate, useParams, type NavigateFunction } from "react-router-dom";
import { Spinner } from "../components/Spinner";
import { Button } from "../components/Button";
import { Infos } from "../components/Infos";
import styles from "./ExperimentationSummaryPage.module.css"
import { useState, type Dispatch, type SetStateAction } from "react";
import { Modal } from "../components/Modal";
import { ModalList } from "../components/ModalList";
import { Goto } from "../components/Goto";
import { exportFile } from "../utils/request/fileExport";
import { generatePdf } from "./EndExperimentationPage";
import { apiFetch } from "../utils/apiFetch";

export function ExperimentationSummaryPage(){
    const {id} = useParams();
    const credentials = undefined;  
    const {loading, data, error} = useFetch<Record<string, any>>(`/expe/get/${id}`, credentials);
    const [sendError, setSendError] = useState<Error|null>(null);
    const [printModal, setPrintModal] = useState<boolean>(false);
    const [printExportModal, setPrintExportModal] = useState<boolean>(false);
    const [loadingPdf, setLoading] = useState<boolean>(false);
    const navigate = useNavigate();

    if (loading){
        return <Spinner/>
    }

    if (error){
        return <p>{error?.message}</p>
    }

    if (data){

        const authenticatedUserOwnsExpe = data.userOwnsExpe;
        const ownerAcceptsContact = data.contactMail !== "";
        const oldAccountedEvaluationExists = data.pedagogicalContext.oldPedagogyEvaluations.accountedEvaluation !== null;
        const newAccountedEvaluationExists = data.pedagogicalContext.newPedagogyEvaluations.accountedEvaluation !== null;

        const keywords = Array.from(data.keywords).join(", ");

        const handleToggleModal = () => {
            setPrintModal(!printModal);
        }

        const handleDeleteConfirm = async () => {
            sendDeleteRequest(id, setSendError, navigate);
        }

        const handleExport = (format: string) => {
            sendExportRequest(format, setSendError, setPrintExportModal);
        }

        const handlePdf = () => {
            setLoading(true);
            sendDownloadRequest(id, setSendError, setLoading);
        }

        return <>
                    <h2 className={styles.h2}>Récapitulatif de l'expérimentation</h2>
                    {ownerAcceptsContact && 
                    <>
                        <h4>Contact</h4>
                        <Infos title="Pour plus d'informations, vous pouvez écrire au courriel suivant" info={data.contactMail}></Infos>
                    </>}
                    {!data.inProgress && <Goto variant="export" label="L'expérimentation est terminée, vous pouvez récupérer son contexte et ses résultats au format pdf" buttonLabel="Générez le pdf" onClick={handlePdf}/>}
                    <h4 className={styles.h4}>Mots clés</h4>
                    {keywords !== "" && <Infos title="Mots-clés" info={keywords}/>}
                    {data.personalKeywords !== "" && <Infos title="Mots-clés personnalisés" info={data.personalKeywords}/>}
                    <h4 className={styles.h4}>Contexte pédagogique</h4>
                    <Infos title="Nom de l'institution" info={data?.affiliation?.name}/> 
                    <Infos title="Titre de l'enseignement" info={data.pedagogicalContext.teachingTitle}/> 
                    <Infos title="Domaine d'étude" info={data.pedagogicalContext.studyField}/> 
                    <Infos title="Année d'étude" info={data.pedagogicalContext.yearOfStudy}/> 
                    <Infos title="Difficulté d'apprentissage" info={data.pedagogicalContext.learningDifficulty}/> 
                    <Infos title="Origine de la difficulté" info={data.pedagogicalContext.learningDifficultyOrigin}/> 
                    <Infos title="Prérequis" info={data.pedagogicalContext.prerequisite}/> 
                    <Infos title="Connaissances" info={data.pedagogicalContext.knowledges}/>
                    <Infos title="Ancienne pédagogie" info={data.pedagogicalContext.oldPedagogy}/> 
                    <Infos title="Nouvelle pédagogie" info={data.pedagogicalContext.newPedagogy}/> 
                    <Infos title="Nombre d'étudiants" info={data.pedagogicalContext.studentsNumber}/> 
                    <Infos title="Spécificités des étudiants" info={data.pedagogicalContext.studentsSpecificities}/>
                    <Infos title="Particularités d'organisation" info={data.pedagogicalContext.organisationParticularities}/> 
                    <Infos title="Fréquence des cours" info={data.pedagogicalContext.classesFrequencies}/> 
                    <Infos title="Dates des cours" info={data.pedagogicalContext.classesDates}/> 
                    <div>
                        <h4 className={styles.h4}>Données d'évaluations</h4>
                        <Infos title="Protocole" info={data.protocol}/>
                        <Infos title="Accepte le partage de données de l'expérimentation" info={data.isSharingData?"oui":"non"}/>
                        {authenticatedUserOwnsExpe && 
                        <>
                            <div className={styles.btnContainer} >
                                <Button onClick={()=> setPrintExportModal(true)}>Exporter le modèle de tableur</Button>
                                <Button href={`/application/endExpe/${id}`}>Ajouter les données de l'expérimentation</Button>
                            </div>
                        </>}
                        <div>
                            <h5>Ancienne pratique</h5>
                            <Infos title="Évaluation initiale" info={data.pedagogicalContext.oldPedagogyEvaluations.initialEvaluation}/> 
                            <Infos title="Évaluation immédiate" info={data.pedagogicalContext.oldPedagogyEvaluations.immediateEvaluation}/> 
                            <Infos title="Évaluation différée" info={data.pedagogicalContext.oldPedagogyEvaluations.delayedEvaluation}/> 
                            {oldAccountedEvaluationExists && <Infos title="Évaluation comptabilisée" info={data.pedagogicalContext.oldPedagogyEvaluations.accountedEvaluation}/>}  
                        </div>
                        <div>
                            <h5>Nouvelle pratique</h5>
                            <Infos title="Évaluation initiale" info={data.pedagogicalContext.newPedagogyEvaluations.initialEvaluation}/> 
                            <Infos title="Évaluation immédiate" info={data.pedagogicalContext.newPedagogyEvaluations.immediateEvaluation}/> 
                            <Infos title="Évaluation différée" info={data.pedagogicalContext.newPedagogyEvaluations.delayedEvaluation}/> 
                            {newAccountedEvaluationExists && <Infos title="Évaluation comptabilisée" info={data.pedagogicalContext.newPedagogyEvaluations.accountedEvaluation}/>}      
                        </div>
                    </div>
                     
                    {authenticatedUserOwnsExpe && 
                    <>
                        <div className={styles.btnContainer} >
                            <Button href={`/application/modifyExpe/${id}`}>Modifier l'expérimentation</Button>
                            <Button href="/application/expe">Voir la liste des expérimentations</Button>
                        </div>
                        <Button className={styles.deleteBtn} onClick={handleToggleModal}>Supprimer l'expérimentation</Button>
                    </>}
                    {printExportModal && 
                        <ModalList title="Format du fichier souhaité" onClose={()=>setPrintExportModal(false)}>
                            <Goto label="Fichier xlsx (Excel 2007)" buttonLabel="Exporter" variant="export" onClick={() => handleExport("xlsx")}/>
                            <Goto label="Fichier xls (Excel 97-2003)" buttonLabel="Exporter" variant="export" onClick={() => handleExport("xls")}/>
                            <Goto label="Fichier ods (Libre office calc)" buttonLabel="Exporter" variant="export" onClick={() => handleExport("ods")}/>
                        </ModalList>}
                    {printModal && <Modal title="Suppression de l'expérimentation" postTitle="Confirmation de fermeture" postContent="Confirmez-vous la suppression de votre expérimentation?" onClose={handleToggleModal} onSave={handleDeleteConfirm}/>}
                    {sendError?.message && <p>{sendError?.message}</p>}
                    {loadingPdf && <Spinner/>}
               </>
    }
} 


async function sendDeleteRequest(id: string|undefined, setSendError: Dispatch<SetStateAction<Error|null>>, navigate: NavigateFunction){
    const response = await apiFetch(`/expe/delete/${id}`, {
            method: "delete",
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            credentials: "include"  
        })
        .catch(requestError => {
            setSendError(requestError);
            throw requestError;
        });

    // Redirection si la requête est acceptée 
    if (response.ok){
         navigate("/application/expe");
    } else {
        setSendError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
}

async function sendExportRequest(format: string, setSendError: Dispatch<SetStateAction<Error|null>>, setPrintExportModal: Dispatch<SetStateAction<boolean>>){
    const formData = new FormData();
    formData.append("entry", format);
    formData.append("exportType", "format");
    
    const response = await apiFetch(`/file/export`, {
            method: "post",
            headers: {
                'Accept': 'application/json',
            },
            body: formData,
            credentials: "include"  
        })
        .catch(requestError => {
            setSendError(requestError);
            throw requestError;
        });

    if (response.ok){
        setPrintExportModal(false);
    } else {
        setSendError(new Error(`Erreur ${response.status}: ${response.statusText}`));
        return;
    }
    
    exportFile(response, "ResultatsEVA_v2." + format);
}
 
async function sendDownloadRequest(id: string|undefined, setError: Dispatch<SetStateAction<Error|null>>, setLoading: Dispatch<SetStateAction<boolean>>){
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