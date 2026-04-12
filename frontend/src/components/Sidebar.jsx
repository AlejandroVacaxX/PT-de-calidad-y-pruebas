import { navigate } from '../Link.jsx';
import AddPersonIcon from './icons/AddPersonIcon.jsx';
import PersonsIcon from './icons/PersonsIcon.jsx';
import SearchIcon from './icons/SearchIcon.jsx';
export const Sidebar = () => {
  const handleNavigation = (path) =>{
    navigate(path);
  };
  return (
      <div className="w-64 bg-white border-r border-gray-200 flex flex-col justify-between overflow-hidden">

        <div>
          {/* SECCIÓN DE LOGOS (Reemplazo del texto anterior por los PNG) */}
          <div className="p-6 flex items-center gap-3">

          </div>

          {/* NAVEGACIÓN PRINCIPAL */}
          <nav className="flex flex-col gap-2 px-4 mt-4">
            <button
                className="group flex items-center gap-2 px-4 py-2 rounded-lg 
                text-slate-900 hover:bg-slate-100 
                transition-all duration-200"
                onClick={()=> handleNavigation('/listado')}
            >
              <PersonsIcon
                  className="w-5 h-5 text-slate-400 group-hover:text-slate-900"
              />
              Personas
            </button>
            <button
                className="group flex items-center gap-2 px-4 py-2 rounded-lg 
                text-slate-900 hover:bg-slate-100 
                transition-all duration-200"
                onClick={()=> handleNavigation('/registro')}
            >
              <AddPersonIcon
                  className="w-5 h-5 text-slate-400 group-hover:text-slate-900"
              />
              Registro
            </button>
            <button
                className="group flex items-center gap-2 px-4 py-2 rounded-lg 
                text-slate-900 hover:bg-slate-100 
                transition-all duration-200"
                onClick={()=> handleNavigation('/busqueda')}
            >
              <SearchIcon
                  className="w-5 h-5 text-slate-400 group-hover:text-slate-900"
              />
              Busqueda
            </button>
          </nav>
        </div>
      </div>
  );
};