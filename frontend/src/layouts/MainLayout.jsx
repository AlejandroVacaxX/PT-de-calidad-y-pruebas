import { Sidebar } from '../components/Sidebar';
import { Topbar } from '../components/Topbar';
// Recibimos "children" como parámetro
export const MainLayout = ({ children }) => {
  return (
        <div className="flex h-screen bg-gray-50 font-sans">
          <Sidebar/>
          <div className="flex-1 flex flex-col overflow-hidden">
            <Topbar>
              {children}
            </Topbar>
            <main className="flex-1 overflow-auto bg-gray-50">
              {children}
            </main>
          </div>
        </div>
  );
};
