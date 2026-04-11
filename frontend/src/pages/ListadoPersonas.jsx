import { useEffect, useState } from "react";
import { navigate } from "../Link";
import { API_URL } from "../consts";

export default function ListadoPersonas() {
  const [personas, setPersonas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const nombreMayuscula  = (text) =>{
    if (!text) return "";
    return text.toLowerCase().split(" ").map(palabra => palabra.charAt(0).toUpperCase() + palabra.slice(1)).join(" ");
  }

  const handleNavigation = (path) => {
    navigate(path);
  };

  // Funcion para obtener la lista de personas desde el backend
  const obtenerPersonas = async () => {
    try {
      setLoading(true);
      const response = await fetch(API_URL);

      if (!response.ok) {
        throw new Error("Error en la respuesta del servidor");
      }

      const data = await response.json();
      setPersonas(data);
    } catch (err) {
      console.error(err);
      setError("No se pudo cargar la lista de personas. Intentalo mas tarde.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    obtenerPersonas();
  }, []);

  return (
    <main className="p-8 bg-gray-100 min-h-screen">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-blue-900">Listado de Personas</h1>
          <p className="text-gray-500">Consulta y gestiona los perfiles registrados</p>
        </div>
        <button
          onClick={() => handleNavigation("/registro")}
          className="px-5 py-2 bg-blue-600 text-white rounded-lg shadow hover:bg-blue-700 transition"
        >
          + Nuevo Registro
        </button>
      </div>

      {/* Estado de carga mejorado visualmente */}
      {loading && (
        <div className="flex flex-col items-center justify-center py-20">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-900 mb-4"></div>
          <p className="text-blue-900 font-medium">Buscando registros...</p>
        </div>
      )}

      {/* Manejo de errores */}
      {error && !loading && (
        <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-6">
          <p className="text-red-700">{error}</p>
        </div>
      )}

      {/* Listado de tarjetas */}
      {!loading && !error && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {personas.length > 0 ? (
            personas.map((persona) => (
              <div
                key={persona.id}
                onClick={() => handleNavigation(`/infopersona/${persona.id}`)}
                className="bg-white p-6 rounded-2xl shadow-sm hover:shadow-md border border-transparent hover:border-blue-200 cursor-pointer transition-all flex flex-col justify-between"
              >
                <div> 
                  <div className="flex justify-between items-start mb-2">
                    <span className="px-2 py-1 text-xs font-bold uppercase tracking-wider rounded bg-blue-100 text-blue-700">
                      {persona.genero || "No especificado"}
                    </span>
                  </div>
                  <h2 className="text-xl font-bold text-blue-900 mb-1">
                    {nombreMayuscula(persona.nombre)}{" "}
                    {nombreMayuscula(persona.apellidoPaterno)}{" "}
                    {nombreMayuscula(persona.apellidoMaterno)}
                  </h2>
                  <p className="text-sm text-gray-500 mb-4">{persona.email}</p>
                </div>

                <div className="flex items-center text-blue-600 text-sm font-semibold">
                  Ver detalle completo →
                </div>
              </div>
            ))
          ) : (
            /* Estado vacio si no hay registros */
            <div className="col-span-full bg-white p-12 rounded-2xl shadow text-center">
              <p className="text-gray-500 text-lg">No se encontraron personas registradas.</p>
              <button
                onClick={() => handleNavigation("/registro")}
                className="mt-4 text-blue-600 font-bold hover:underline"
              >
                Registra la primera persona aqui
              </button>
            </div>
          )}
        </div>
      )}
    </main>
  );
}
