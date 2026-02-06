import { useFetch } from "../hooks/useFetch";
import { useNavigate, useParams, type NavigateFunction } from "react-router-dom";
import { Spinner } from "../components/Spinner";
import { Button } from "../components/Button";
import { Infos } from "../components/Infos";
import styles from "./ExperimentationSummaryPage.module.css"
import { useState, type Dispatch, type SetStateAction } from "react";
import { Modal } from "../components/Modal";
 
export function ExperimentationSummaryPage(){
    const {id} = useParams();
    const credentials = undefined;  
    const {loading, data, error} = useFetch<Record<string, any>>(`http://localhost:9000/expe/get/${id}`, credentials);
    const [deleteError, setDeleteError] = useState<Error|null>(null);
    const [printModal, setPrintModal] = useState<boolean>(false);
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
            sendDeleteRequest(id, setDeleteError, navigate);
        }

        return <>
                    <h1>Récapitulatif de l'expérimentation</h1>
                    {ownerAcceptsContact && 
                    <>
                        <h4>Contact</h4>
                        <Infos title="Pour plus d'informations, vous pouvez écrire au courriel suivant" info={data.contactMail}></Infos>
                    </>}
                    <h4>Mots clés</h4>
                    {keywords !== "" && <Infos title="Mots-clés" info={keywords}/>}
                    {data.personalKeywords !== "" && <Infos title="Mots-clés personnalisés" info={data.personalKeywords}/>}
                    <h4>Contexte pédagogique</h4>
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
                        <h4>Données d'évaluations</h4>
                        <Infos title="Protocole" info={data.protocol}/>
                        <Infos title="Accepte le partage de données de l'expérimentation" info={data.isSharingData?"oui":"non"}/>
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
                    {printModal && <Modal title="Suppression de l'expérimentation" postTitle="Confirmation de fermeture" postContent="Confirmez-vous la suppression de votre expérimentation?" onClose={handleToggleModal} onSave={handleDeleteConfirm}/>}
                    {deleteError?.message && <p>{deleteError?.message}</p>}
               </>
    }
} 


async function sendDeleteRequest(id: string|undefined, setDeleteError: Dispatch<SetStateAction<Error|null>>, navigate: NavigateFunction){
    const response = await fetch(`http://localhost:9000/expe/delete/${id}`, {
            method: "delete",
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            credentials: "include"  
        })
        .catch(requestError => {
            setDeleteError(requestError);
            throw requestError;
        });

    // Redirection si la requête est acceptée 
    if (response.ok){
         navigate("/application/expe");
    } else {
        setDeleteError(new Error(`Erreur ${response.status}: ${response.statusText}`));
    }
}