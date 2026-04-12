import { Sidebar } from '../components/Sidebar';
import { Topbar } from '../components/Topbar';

export const MainLayout = ({ children, titulo }) => {
  return (
    <div className="flex h-screen w-screen bg-gray-50 overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Topbar titulo={titulo} />
        {/* Contenedor relativo que servirá de ancla */}
        <div className="flex-1 relative">
          {/* Contenedor absoluto que fuerza el scroll */}
          <main className="absolute inset-0 overflow-y-auto">
            {children}
          </main>
        </div>
      </div>
    </div>
  );
};
