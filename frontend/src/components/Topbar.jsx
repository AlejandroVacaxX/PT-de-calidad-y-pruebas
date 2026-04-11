import { FiSearch, FiBell, FiChevronDown } from 'react-icons/fi';

// Recibimos la "prop" titulo. Si no nos mandan nada, por defecto dirá "Dashboard"
export const Topbar = ({ titulo = "Dashboard" }) => {
  return (
    <div className="h-20 bg-white border-b border-gray-200 flex justify-between items-center px-8 w-full">

      {/* TÍTULO DINÁMICO */}
      <h2 className="text-2xl font-bold text-gray-800">{titulo}</h2>

      {/* CONTROLES DERECHOS */}
      <div className="flex items-center gap-6">
      </div>
    </div>
  );
};
