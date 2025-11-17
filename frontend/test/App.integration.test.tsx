import { describe, expect, it } from 'vitest';
import userEvent from '@testing-library/user-event';
import { render, screen} from '@testing-library/react';
import App from '../src/App';
import { createMemoryRouter, RouterProvider } from 'react-router-dom';
import { HomePage } from '../src/pages/HomePage';
import { LoginPage } from '../src/pages/LoginPage';


describe("App integration", () => {
    it("should redirect to login page", async () => {
        const user = userEvent.setup();
        // substituer le browserRouter par un router en mémoire car browserRouter ne marche pas bien dans jsdom
        const testRouter = createMemoryRouter([
            { path:"/", element: <HomePage/>},
            { path:"/login", element: <LoginPage/>},
        ], {
            initialEntries: ["/"]  // ✅ Démarrer à la racine
        })
        const {container} = render(<RouterProvider router={testRouter}/>);
        //const {container} = render(<App/>);
        const link = container.querySelector<SVGAElement>("a[id='authentification-link']");
        console.log(link)
        if (link){
            await user.click(link);
            console.log(screen)
            expect(screen.getByText('Bienvenue Untel!')).toBeInTheDocument();
        }
    })
});