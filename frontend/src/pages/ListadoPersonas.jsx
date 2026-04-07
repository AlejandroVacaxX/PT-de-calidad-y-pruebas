import { useEffect, useState } from "react";
import { navigate } from "../Link";

export default function ListadoPersonas() {
  const [personas, setPersonas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const handleNavigation = (path) => {
    navigate(path);
  };

  const obtenerPersonas = async () => {
    try {
      const response = await fetch("http://localhost:8080/personamanager/personas");
      if (!response.ok) throw new Error();
      const data = await response.json();
      setPersonas(data);
    } catch {
      setError("❌ Error al cargar datos");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    obtenerPersonas();
  }, []);

  return (
    <main className="p-8 bg-gray-100 min-h-screen">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-blue-900">Listado de Personas</h1>
        <p className="text-gray-500">Selecciona un registro para ver o editar</p>
      </div>

      {loading && <p>Cargando...</p>}
      {error && <p className="text-red-500">{error}</p>}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {personas.map((persona) => (
          <div
            key={persona.id}
            onClick={() => handleNavigation('/infopersona')}
            className="bg-white p-5 rounded-2xl shadow hover:shadow-lg cursor-pointer transition"
          >
            <h2 className="text-lg font-bold text-blue-900">
              {persona.nombre} {persona.apellidos}
            </h2>
            <p className="text-sm text-gray-500">{persona.genero}</p>
            <p className="text-sm text-gray-500">{persona.email}</p>
          </div>
        ))}
      </div>
    </main>
  );
}
