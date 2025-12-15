export function ProfilePage(){
    /* Faire une requête à la bonne adresse spring, récupérer tous les champs de user 
    boucler sur ces champs pour les afficher  ou simplement les indiquer
    Le bouton sauvegarder ne devra apparaître que si on a cliqué sur modifier le profil
    : faire un état pour l'affichage
    La suppression devra faire apparaître un popup de confirmation auquel cas on supprime et 
    redirige vers la page d'accueil
    */

    return <>
                <button>Modifier le profil</button>
                <div> Infos utilisateurs </div>
                <div className="profil-modify">
                    <button>Sauvegarder les modifications</button>
                    <a href="">Supprimer l'utilisateur</a>
                </div>
           </>
}