import './App.css'
import { createBrowserRouter, Outlet, RouterContextProvider, RouterProvider, type RouteObject } from 'react-router-dom';
import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { PrivateRoute } from './components/PrivateRoute';
import { TestPage } from './pages/TestPage';
import { NavBar } from './components/NavBar';
import userEvent from '@testing-library/user-event';
import { useTheme } from './hooks/useTheme';

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
              path : "à remplacer",
              element : "à remplacer",
            }
        ]
        
      }
    ]
  },

  
]

const browserRouter = createBrowserRouter(routes);

function App() {
  return <RouterProvider router={browserRouter}/>
}

function Layout(){
  const {isAuthenticated, toggleIsAuthenticated} = useTheme();
 
  return <>
          <NavBar>
              {isAuthenticated && <button onClick={toggleIsAuthenticated}>Se déconnecter</button>}
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
