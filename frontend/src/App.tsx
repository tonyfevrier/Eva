import './App.css'
import { createBrowserRouter, Outlet, RouterProvider, type RouteObject } from 'react-router-dom';
import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { PrivateRoute } from './components/PrivateRoute';
import { TestPage } from './pages/TestPage';
import { NavBar } from './components/NavBar';
import { useTheme } from './hooks/useTheme';
import { useHandleAuth } from './hooks/useHandleAuth';
import { ProfilePage } from './pages/ProfilePage';
import { Button } from './components/Button';
import { RecoveryPage } from './pages/RecoveryPage';
import { Goto } from './components/Goto';
import { PasswordChangePage } from './pages/PasswordChangePage';
import { DescribePage } from './pages/DescribePage';

const routes: RouteObject[] = [
  {
    path : "/",
    element : <Layout/>,
    children : [
      {
        index : true,
        element : <HomePage/>,
      },
      {
        path : "/login",
        element : <LoginPage/>,
      },
      {
        path : "/register",
        element: <RegisterPage/>
      },
      {
        path : "/pwdForget",
        element: <RecoveryPage/>
      },
      {
        path: "/pwdChange",
        element: <PasswordChangePage/>
      },
      {
        path : "/seeMail",
        element: <Goto href="/login" label="Un courriel vous a été envoyé, veuillez cliquer sur le lien présent dans ce courriel." buttonLabel="Revenir à la page de login"/>
      },
      {
        path : "/pwdUpdated",
        element: <Goto href="/login" label="Votre mot de passe a bien été modifié" buttonLabel="Revenir à la page de login"/>
      },
      {
        path : "/application",
        element : <AuthenticatedLayout/>,
        children : [
            {
              index : true,
              element : <TestPage/>,
            },
            {
              path : "profile",
              element : <ProfilePage/>,
            },
            {
              path: "describeYou",
              element : <DescribePage/>, 
            }
        ]
        
      }
    ]
  }, 
]

const browserRouter = createBrowserRouter(routes);

function App() {
  useHandleAuth();    
  return <RouterProvider router={browserRouter}/>
}

function Layout(){
  const {isAuthenticated, logout} = useTheme();
  const handleClick = async () => {logout()}
  return <>
          <NavBar>
              <a href="/">Accueil</a>
              {isAuthenticated && <a href="/application/profile">Profil</a> }
              {isAuthenticated && <Button onClick={handleClick}>Se déconnecter</Button>}
          </NavBar>
          <Outlet/>
       </>
  }

function AuthenticatedLayout(){
  return <PrivateRoute>
            <Outlet/>
         </PrivateRoute>
}

export default App
