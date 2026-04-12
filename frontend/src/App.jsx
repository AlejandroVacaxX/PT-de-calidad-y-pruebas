import { MainLayout } from "./layouts/MainLayout.jsx";
import Registro from "./pages/Registro.jsx";
import InfoPersona from "./pages/InfoPersona.jsx";
import { EVENTS } from "./consts.js";
import { useEffect, useState } from "react";
import ListadoPersonas from "./pages/ListadoPersonas.jsx";
import Busqueda from "./pages/Busqueda.jsx";

function App() {
  const [currentPath, setCurrentPath] = useState(window.location.pathname);

  useEffect(() => {
    const onLocationChange = () => {
      setCurrentPath(window.location.pathname);
    };
    window.addEventListener(EVENTS.PUSHSTATE, onLocationChange);
    window.addEventListener(EVENTS.POPSTATE, onLocationChange);
    return () => {
      window.removeEventListener(EVENTS.PUSHSTATE, onLocationChange);
      window.removeEventListener(EVENTS.POPSTATE, onLocationChange);
    };
  }, []);
  return (
    <>
      {(currentPath === '/' || currentPath === '/listado' || currentPath === '/dashboard') &&(
        <MainLayout titulo="Listado de Personas">
          <ListadoPersonas/>
        </MainLayout>
      )}
      {currentPath === '/registro' && (
        <MainLayout titulo="Nuevo Registro de Persona">
          <Registro/>
        </MainLayout>
      )}
      {currentPath.startsWith('/infopersona') &&(
        <MainLayout titulo="Detalles de Persona">
          <InfoPersona/>
        </MainLayout>
      )}
      {currentPath === '/busqueda' && (
          <MainLayout titulo="Búsqueda Avanzada">
            <Busqueda/>
          </MainLayout>
      )}
      {!(currentPath === '/' || currentPath === '/listado' || currentPath === '/dashboard' || currentPath === '/registro' || currentPath === '/busqueda' ||currentPath.startsWith('/infopersona')) && (
        <div className="flex flex-col items-center justify-center h-screen">
          <h1 className="text-4xl font-bold text-gray-800">404</h1>
          <p className="text-gray-500">Página no encontrada</p>
          <button onClick={() => window.location.href = '/'} className="mt-4 text-blue-600 underline">Ir al inicio</button>
        </div>
      )}
      </>
  );
}

export default App;
