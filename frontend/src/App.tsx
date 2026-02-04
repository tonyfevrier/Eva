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
import { ExperimentationPage } from './pages/ExperimentationPage';
import { InstitutionPage } from './pages/InstitutionPage';
import { InstitutionProfilePage } from './pages/InstitutionProfilePage';
import { ExperimentationSummaryPage } from './pages/ExperimentationSummaryPage';
import { ExperimentationProfilePage } from './pages/ExperimentationProfilePage';

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
            },
            {
              path: "institution",
              element : <InstitutionPage/>, 
            },
            {
              path: "institutionProfile/:id",
              element : <InstitutionProfilePage/>, 
            },
            {
              path: "expe",
              element : <ExperimentationPage/>
            },
            {
              path: "experimentationSummary/:id",
              element : <ExperimentationSummaryPage/>, 
            },
            {
              path: "modifyExpe/:id",
              element : <ExperimentationProfilePage/>, 
            },
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
  const {isAuthenticated, logout, isProfileCompleted} = useTheme();
  const handleClick = async () => {logout()}
  const isAnAuthenticatedCompletedProfileUser = isAuthenticated && isProfileCompleted;
  return <>
          <NavBar>
              { (!isAuthenticated || isAnAuthenticatedCompletedProfileUser) && <a href="/">Accueil</a>}
              {isAnAuthenticatedCompletedProfileUser && <>
                                                          <a href="/application/profile">Profil</a>
                                                          <a href="/application/expe">Mes expérimentations</a>
                                                        </>}
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
