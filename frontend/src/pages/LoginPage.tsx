

export function LoginPage({}){
    return <>
        <h1> Se connecter</h1>
        <form action="">
            <input type="mail" placeholder="Adresse mail"/>
            <input type="password" placeholder="Mot de passe"/>
            <a href="">Mot de passe oublié?</a>
            <input type="submit" />
            <div>
                <p>Vous n'avez pas encore de compte?</p>
                <a href="/register">Inscrivez-vous ici</a>
            </div>
        </form>

    </>
}