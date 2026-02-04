import { useFetch } from "../hooks/useFetch";
import { useParams } from "react-router-dom";
import { Spinner } from "../components/Spinner";
import { Button } from "../components/Button";
import { Infos } from "../components/Infos";
import styles from "./ExperimentationSummaryPage.module.css"
 
export function ExperimentationSummaryPage(){
    const {id} = useParams();
    const {loading, data, error} = useFetch<Record<string, any>>(`http://localhost:9000/expe/get/${id}`);

    if (loading){
        return <Spinner/>
    }

    if (error){
        return <p>{error?.message}</p>
    }

    if (data){
        
        let keywords = "";
        for (const word of Array.from(data.keywords)){
            keywords += word + ", "; 
        }

        return <>
                    <h1>Récapitulatif de l'expérimentation</h1>
                    <h4>Mots clés</h4>
                    <Infos title="Mots-clés" info={keywords}/>
                    <Infos title="Mots-clés personnalisés" info={data.personalKeywords}/>
                    <h4>Contexte pédagogique</h4>
                    <Infos title="Nom de l'institution" info={data.institutionName}/> 
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
                            <Infos title="Évaluation comptabilisée" info={data.pedagogicalContext.oldPedagogyEvaluations.accountedEvaluation}/>   
                        </div>
                        <div>
                            <h5>Nouvelle pratique</h5>
                            <Infos title="Évaluation initiale" info={data.pedagogicalContext.newPedagogyEvaluations.initialEvaluation}/> 
                            <Infos title="Évaluation immédiate" info={data.pedagogicalContext.newPedagogyEvaluations.immediateEvaluation}/> 
                            <Infos title="Évaluation différée" info={data.pedagogicalContext.newPedagogyEvaluations.delayedEvaluation}/> 
                            <Infos title="Évaluation comptabilisée" info={data.pedagogicalContext.newPedagogyEvaluations.accountedEvaluation}/>      
                        </div>
                    </div>
                     
                    <div className={styles.btnContainer} >
                        <Button href={`/application/modifyExpe/${id}`}>Modifier l'expérimentation</Button>
                        <Button href="/application/expe">Confirmer l'expérimentation</Button>
                    </div>
               </>
    }
} 