import { Goto } from "../components/Goto"

export function SeekMailPage(){
    return <Goto label="Un courriel vous a été envoyé, veuillez cliquer sur le lien présent dans ce courriel." href="/login" buttonLabel="Revenir à la page de login"/>
}