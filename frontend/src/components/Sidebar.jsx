import { navigate } from '../Link.jsx';
import DashboardIcon from './icons/DashboardIcon.jsx';
import PersonsIcon from './icons/PersonsIcon.jsx';
export const Sidebar = () => {
  const handleNavigation = (path) =>{
    navigate(path);
  };
  return (
    <div className="w-64 h-screen bg-white border-r border-gray-200 flex flex-col justify-between">

      <div>
        {/* SECCIÓN DE LOGOS (Reemplazo del texto anterior por los PNG) */}
        <div className="p-6 flex items-center gap-3">

        </div>

        {/* NAVEGACIÓN PRINCIPAL */}
        <nav className="flex flex-col gap-2 px-4 mt-4">

          <button
            className="group flex items-center gap-2 px-4 py-2 rounded text-black hover:bg-primary/20 hover:text-primary/80 transition"
          >
            <DashboardIcon
              className="w-5 h-5 text-black group-hover:text-primary/80"
              onClick={()=>handleNavigation('/dashboard')}
            /> Dashboard
          </button>
          <button
            className="group flex items-center gap-2 px-4 py-2 rounded text-black hover:bg-primary/20 hover:text-primary/80 transition"
            onClick={()=> handleNavigation('/listado')}
          >
            <PersonsIcon
              className="w-5 h-5 text-black group-hover:text-primary/80"
            />
            Personas
          </button>
          <button
            className="group flex items-center gap-2 px-4 py-2 rounded text-black hover:bg-primary/20 hover:text-primary/80 transition"
            onClick={()=> handleNavigation('/registro')}
          >
            <PersonsIcon
              className="w-5 h-5 text-black group-hover:text-primary/80"
            />
            Registro
          </button>
        </nav>
      </div>
    </div>
  );
};
