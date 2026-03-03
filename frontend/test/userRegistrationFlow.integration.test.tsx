import { describe, expect, it, beforeEach, vi } from 'vitest';
import userEvent from '@testing-library/user-event';
import { render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { createMemoryRouter, RouterProvider } from 'react-router-dom';
import { HomePage } from '../src/pages/HomePage';
import { RegisterPage } from '../src/pages/RegisterPage';
import { LoginPage } from '../src/pages/LoginPage';
import { DescribePage } from '../src/pages/DescribePage';
import { InstitutionPage } from '../src/pages/InstitutionPage';
import { InstitutionCreationPage } from '../src/pages/InstitutionCreationPage';
import { InstitutionSelectionPage } from '../src/pages/InstitutionSelectionPage';
import { NavBar } from '../src/components/NavBar';
import { Outlet } from 'react-router-dom';
import { ThemeProvider } from '../src/hooks/useTheme';

/**
 * Test fonctionnel pour l'user story:
 * Premier enregistrement d'un utilisateur depuis l'arrivée sur le site
 * jusqu'à la création d'un établissement
 */
describe("User Story: Premier enregistrement d'un utilisateur", () => {
    let user: ReturnType<typeof userEvent.setup>;
    
    beforeEach(() => {
        user = userEvent.setup();
        
        // Mock de fetch pour simuler le backend
        global.fetch = vi.fn();
        
        // Mock de window.alert pour éviter les erreurs dans les tests
        global.alert = vi.fn();
        
        // Mock de localStorage
        const localStorageMock = {
            getItem: vi.fn(),
            setItem: vi.fn(),
            removeItem: vi.fn(),
            clear: vi.fn(),
        };
        global.localStorage = localStorageMock as any;
    });

    it("devrait permettre le parcours complet d'enregistrement d'un nouvel utilisateur", async () => {
        // Configuration du router avec toutes les routes nécessaires
        const Layout = () => (
            <>
                <NavBar>
                    <a href="/">Accueil</a>
                </NavBar>
                <Outlet />
            </>
        );

        const testRouter = createMemoryRouter([
            {
                path: "/",
                element: <Layout />,
                children: [
                    { index: true, element: <HomePage /> },
                    { path: "/register", element: <RegisterPage /> },
                    { path: "/login", element: <LoginPage /> },
                    { 
                        path: "/application/describeYou", 
                        element: <DescribePage /> 
                    },
                    { 
                        path: "/application/institution", 
                        element: <InstitutionPage /> 
                    },
                ]
            }
        ], {
            initialEntries: ["/"]
        });

        const { container } = render(
            <ThemeProvider>
                <RouterProvider router={testRouter} />
            </ThemeProvider>
        );

        // ÉTAPE 1: L'utilisateur arrive sur le site et voit "Accueil" dans la barre de navigation
        expect(screen.getByText('Accueil')).toBeInTheDocument();
        expect(screen.getByText('EVA')).toBeInTheDocument();

        // ÉTAPE 2: Il clique sur "S'inscrire/se connecter"
        const registerLink = container.querySelector("a[href='/register']");
        expect(registerLink).toBeInTheDocument();
        await user.click(registerLink!);

        // Vérification qu'on est bien sur la page d'inscription
        await waitFor(() => {
            expect(screen.getByText('Inscription')).toBeInTheDocument();
        });

        // ÉTAPE 3: L'utilisateur essaie de soumettre sans remplir tous les champs
        const submitButton = screen.getByRole('button', { name: /soumettre/i });
        
        // On remplit seulement quelques champs (pas tous)
        const emailInput = screen.getByPlaceholderText('mail');
        await user.type(emailInput, 'test@example.com');
        
        // Tentative de soumission
        await user.click(submitButton);

        // ÉTAPE 4: Un message d'erreur devrait apparaître et on reste sur la page
        await waitFor(() => {
            expect(screen.getByText('Inscription')).toBeInTheDocument();
            // Le composant Form devrait afficher "Il faut remplir ce champ" pour les champs vides
            const errorMessages = screen.getAllByText(/Il faut remplir ce champ/i);
            expect(errorMessages.length).toBeGreaterThan(0);
        });

        // ÉTAPE 5: Il remplit ensuite tous les champs correctement
        const firstnameInput = screen.getByPlaceholderText('firstname');
        const lastnameInput = screen.getByPlaceholderText('lastname');
        const passwordInput = screen.getByPlaceholderText('password');

        await user.type(firstnameInput, 'Jean');
        await user.type(lastnameInput, 'Dupont');
        await user.clear(emailInput);
        await user.type(emailInput, 'nouveau@example.com');
        await user.type(passwordInput, 'MotDePasse123!');

        // Mock de la réponse d'enregistrement réussie
        (global.fetch as any).mockResolvedValueOnce({
            ok: true,
            status: 200,
            json: async () => ({ message: 'Inscription réussie' })
        });

        // ÉTAPE 6: Il clique sur Soumettre
        await user.click(submitButton);

        // ÉTAPE 7: Le message de confirmation apparaît avec le lien pour se logger
        await waitFor(() => {
            expect(screen.getByText(/Votre inscription a bien été réalisée/i)).toBeInTheDocument();
            expect(screen.getByText(/Cliquez ici pour vous logger/i)).toBeInTheDocument();
        });

        // ÉTAPE 8: Il clique sur "Cliquez ici pour vous logger"
        const loginLink = screen.getByText(/Cliquez ici pour vous logger/i);
        await user.click(loginLink);

        // Vérification qu'on est sur la page de login
        await waitFor(() => {
            expect(screen.getByText('Se connecter')).toBeInTheDocument();
        });

        // ÉTAPE 9: Il essaie de se logger avec un mauvais mot de passe
        const loginEmailInput = screen.getByPlaceholderText('mail');
        const loginPasswordInput = screen.getByPlaceholderText('password');

        await user.type(loginEmailInput, 'nouveau@example.com');
        await user.type(loginPasswordInput, 'MauvaisMotDePasse');

        // Mock d'une réponse d'erreur (mauvais mot de passe)
        (global.fetch as any).mockResolvedValueOnce({
            ok: false,
            status: 401,
            text: async () => 'Erreur 401: Identifiants incorrects',
        });

        const loginSubmitButton = screen.getByRole('button', { name: /soumettre/i });
        await user.click(loginSubmitButton);

        // ÉTAPE 10: Un message d'erreur apparaît
        await waitFor(() => {
            // Le message d'erreur devrait contenir le texte retourné par le serveur
            expect(screen.getByText(/Erreur 401/i)).toBeInTheDocument();
        });

        // ÉTAPE 11: Il entre les bons identifiants
        await user.clear(loginPasswordInput);
        await user.type(loginPasswordInput, 'MotDePasse123!');

        // Mock d'une réponse de connexion réussie
        (global.fetch as any).mockResolvedValueOnce({
            ok: true,
            status: 200,
            text: async () => JSON.stringify({ 
                accessExpiresIn: 3600000, // 1 heure en millisecondes
                additionalData: "null" // Profil non complété
            })
        });

        await user.click(loginSubmitButton);

        // ÉTAPE 12: Il arrive sur la page "Te décrire"
        await waitFor(() => {
            expect(screen.getByText('Te décrire')).toBeInTheDocument();
            expect(screen.getByText(/Votre inscription a bien été réalisée/i)).toBeInTheDocument();
        });

        // ÉTAPE 13: Il remplit les données demandées
        // Les checkboxes n'ont pas de label for, on utilise le titre pour les localiser
        const checkboxes = container.querySelectorAll("input[type='checkbox']");
        const acceptMapCheckbox = checkboxes[0] as HTMLInputElement;
        const acceptContactCheckbox = checkboxes[1] as HTMLInputElement;
        
        await user.click(acceptMapCheckbox);
        await user.click(acceptContactCheckbox);

        // Les autres champs sont facultatifs, on peut en remplir quelques-uns
        // Il n'y a qu'un seul select sur la page DescribePage (Genre)
        const genderSelect = container.querySelector("select") as HTMLSelectElement;
        if (genderSelect) {
            await user.selectOptions(genderSelect, 'Homme');
        }

        // Mock de la réponse de sauvegarde du profil
        (global.fetch as any).mockResolvedValueOnce({
            ok: true,
            status: 200,
            json: async () => ({ message: 'Profil mis à jour' })
        });

        // ÉTAPE 14: Il clique sur "Sauvegarder les informations"
        const saveProfileButton = screen.getByRole('button', { name: /sauvegarder les informations/i });
        await user.click(saveProfileButton);

        // ÉTAPE 15: Il arrive sur la page "Tes établissements"
        await waitFor(() => {
            expect(screen.getByText('Tes établissements')).toBeInTheDocument();
            expect(screen.getByText(/terminer l'enregistrement/i)).toBeInTheDocument();
        });

        // ÉTAPE 16: Il n'y a aucun établissement dans la liste
        // Il clique sur "Créer un établissement"
        const createInstitutionButton = screen.getByRole('button', { name: /créer un établissement/i });
        await user.click(createInstitutionButton);

        // Vérification que le formulaire de création d'établissement est affiché
        await waitFor(() => {
            expect(screen.getByText(/Nom de l'établissement/i)).toBeInTheDocument();
        });

        // ÉTAPE 17: Il remplit les données du formulaire
        // Les inputs n'ont pas de labels dans le composant Input, on utilise les name attributes
        const institutionNameInput = container.querySelector("input[name='name']") as HTMLInputElement;
        const institutionTownInput = container.querySelector("input[name='town']") as HTMLInputElement;
        const institutionCategorySelect = container.querySelector("select[name='category']") as HTMLSelectElement;
        const institutionEmailInput = container.querySelector("input[name='contactMail']") as HTMLInputElement;
        const institutionSocialStatusSelect = container.querySelector("select[name='socialStatus']") as HTMLSelectElement;
        const studentsNumberInput = container.querySelector("input[name='studentsNumber']") as HTMLInputElement;

        await user.type(institutionNameInput, 'Lycée Test');
        await user.type(institutionTownInput, 'Paris');
        await user.selectOptions(institutionCategorySelect, 'Public');
        await user.type(institutionEmailInput, 'contact@lyceetest.fr');
        await user.selectOptions(institutionSocialStatusSelect, 'Moyen');
        await user.type(studentsNumberInput, '500');

        // Mock de la réponse de création d'établissement
        (global.fetch as any).mockResolvedValueOnce({
            ok: true,
            status: 200,
            json: async () => ({ message: 'Établissement créé' })
        });

        // ÉTAPE 18: Il clique sur "Sauver l'établissement"
        const saveInstitutionButton = screen.getByRole('button', { name: /sauver l'établissement/i });
        await user.click(saveInstitutionButton);

        // ÉTAPE 19: L'établissement est enregistré et il reste sur la même page
        await waitFor(() => {
            expect(global.alert).toHaveBeenCalledWith(
                expect.stringMatching(/enregistrer un établissement avec succès/i)
            );
        });

        // ÉTAPE 20: Il constate que les boutons sont apparus
        // Note: Dans le vrai composant, les boutons Accueil, Mon profil, Mes expérimentations
        // apparaissent dans la NavBar une fois isProfileCompleted = true
        // Pour ce test, on vérifie que le bouton "Quitter la page" est maintenant visible
        await waitFor(() => {
            const quitButton = screen.getByRole('button', { name: /quitter la page/i });
            expect(quitButton).toBeInTheDocument();
        });

        // ÉTAPE 21: Il clique sur "Quitter la page"
        const quitButton = screen.getByRole('button', { name: /quitter la page/i });
        await user.click(quitButton);

        // ÉTAPE 22: Il arrive sur la page d'accueil
        await waitFor(() => {
            expect(screen.getByText('EVA')).toBeInTheDocument();
            expect(screen.getByText('Accueil')).toBeInTheDocument();
        });
    });
});
