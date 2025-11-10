import './App.css'
import { createBrowserRouter, RouterContextProvider, RouterProvider, type RouteObject } from 'react-router-dom';
import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';


const routes: RouteObject[] = [
  {
    path : "/",
    element : <HomePage/>,
  },
  {
    path : "/login",
    element : <LoginPage/>,
  },
]

const browserRouter = createBrowserRouter(routes);

function App() {
  return <RouterProvider router={browserRouter}/>
}

export default App
