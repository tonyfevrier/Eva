import './App.css'
import { createBrowserRouter, Outlet, RouterContextProvider, RouterProvider, type RouteObject } from 'react-router-dom';
import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { PrivateRoute } from './components/PrivateRoute';

const routes: RouteObject[] = [
  {
    path : "/",
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
    element : <Layout/>,
    children : [
        {
          index : true,
          element : "à remplacer",
        },
        {
          path : "à remplacer",
          element : "à remplacer",
        }
    ]
    
  }
]

const browserRouter = createBrowserRouter(routes);

function App() {
  return <RouterProvider router={browserRouter}/>
}

function Layout(){
  return <PrivateRoute>
            <Outlet/>
         </PrivateRoute>
}

export default App
