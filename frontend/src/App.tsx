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
              {isAuthenticated && <a href="/application/profile">Profil</a> }
              {isAuthenticated && <button onClick={handleClick}>Se déconnecter</button>}
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
