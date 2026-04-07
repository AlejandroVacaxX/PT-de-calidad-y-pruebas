import { MainLayout } from "./layouts/MainLayout.jsx";
import Registro from "./pages/Registro.jsx";
import InfoPersona from "./pages/InfoPersona.jsx";
import { EVENTS } from "./consts.js";
import { useEffect, useState } from "react";
import ListadoPersonas from "./pages/ListadoPersonas.jsx";

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
      {(currentPath === '/listado' || currentPath === '/dashboard') &&(
        <MainLayout>
          <ListadoPersonas/>
        </MainLayout>
      )}
      {currentPath === '/registro' && (
        <MainLayout>
          <Registro/>
        </MainLayout>
      )}
      {currentPath === '/infopersona' &&(
        <MainLayout>
          <InfoPersona/>
        </MainLayout>
      )}
      </>
  );
}

export default App;
