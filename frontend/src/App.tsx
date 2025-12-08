import './App.css'
import { createBrowserRouter, Outlet, RouterProvider, type RouteObject } from 'react-router-dom';
import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { PrivateRoute } from './components/PrivateRoute';
import { TestPage } from './pages/TestPage';
import { NavBar } from './components/NavBar';
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
  const {isAuthenticated, toggleIsAuthenticated, setExpirationTime} = useTheme();

  const handleClick = async () => {
    toggleIsAuthenticated();
    setExpirationTime(0);
    const response = await fetch("http://localhost:9000/api/logout", {
      headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
      },
      credentials:'include'
    });
  console.log(response);

  }
 
  return <>
          <NavBar>
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
