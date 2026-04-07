import { useEffect, useState } from "react";
import { navigate } from "../Link";

export default function InfoPersona() {
  const [persona, setPersona] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  // Extraemos el ID de la URL de forma segura
  // filter(Boolean) elimina strings vacíos si la URL termina en "/"
  const pathParts = window.location.pathname.split("/").filter(Boolean);
  const id = pathParts[pathParts.length - 1];

  const handleNavigation = (path) => {
    navigate(path);
  };

  const obtenerPersona = async () => {
    try {
      setLoading(true);
      const response = await fetch(
        `http://localhost:8080/personamanager/personas/${id}`
      );

      if (!response.ok) throw new Error("Persona no encontrada");

      const data = await response.json();
      setPersona(data);
      setError(false);
    } catch (err) {
      console.error("Error al obtener los datos:", err);
      setError(true);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (id && id !== "infopersona") {
      obtenerPersona();
    } else {
      setError(true);
      setLoading(false);
    }
  }, [id]);

  // Función para calcular el IMC dinámicamente
  const calcularIMC = (peso, estatura) => {
    if (!peso || !estatura) return "N/A";
    const imc = (peso / (estatura * estatura)).toFixed(1);
    return imc;
  };

  if (loading) return <div className="p-8 text-blue-600 font-bold">Cargando perfil...</div>;

  if (error || !persona) {
    return (
      <div className="p-8">
        <p className="text-red-500 font-bold">⚠️ Error: No se pudo cargar la información.</p>
        <button onClick={() => handleNavigation("/listado")} className="mt-4 text-blue-600 underline">
          Regresar al listado
        </button>
      </div>
    );
  }

  return (
    <main className="p-8 bg-gray-100 min-h-screen">
      {/* TOP ACTIONS */}
      <div className="flex justify-between items-center mb-6">
        <button
          onClick={() => handleNavigation("/")}
          className="text-blue-600 flex items-center gap-2 hover:font-bold transition-all"
        >
          ← Regresar al Listado
        </button>

        <div className="flex gap-3">
          <button className="px-4 py-2 rounded-lg bg-blue-100 text-blue-700 hover:bg-blue-200 transition">
            Editar Información
          </button>
          <button className="px-4 py-2 rounded-lg bg-red-100 text-red-600 hover:bg-red-200 transition">
            Eliminar Persona
          </button>
        </div>
      </div>

      {/* PERFIL HEADER */}
      <section className="bg-white p-6 rounded-2xl shadow mb-6 flex justify-between items-center">
        <div className="flex items-center gap-5">
          <div className="w-20 h-20 bg-blue-900 rounded-xl flex items-center justify-center text-white text-2xl font-bold">
            {persona.nombre?.charAt(0)}{persona.apellidos?.charAt(0)}
          </div>

          <div>
            <h1 className="text-2xl font-bold text-blue-900">
              {persona.nombre} {persona.apellidos}
            </h1>
            <p className="text-gray-500">ID del Registro: {persona.id}</p>
          </div>
        </div>

        <div className="text-right">
          <p className="text-xs text-gray-400 uppercase">Estado del Sistema</p>
          <p className="text-sm text-green-600 font-bold">● Activo</p>
        </div>
      </section>

      {/* GRID DE INFORMACIÓN */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

        {/* COLUMNA IZQUIERDA: DATOS */}
        <section className="lg:col-span-2 bg-white p-6 rounded-2xl shadow">
          <h2 className="text-lg font-semibold text-blue-900 mb-6 border-b pb-2">
            Datos personales
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-y-6 gap-x-4">
            <div>
              <p className="text-xs text-gray-400 uppercase">Fecha de nacimiento</p>
              <p className="text-blue-900 font-medium">{persona.fechaDeNacimiento || "No registrada"}</p>
            </div>

            <div>
              <p className="text-xs text-gray-400 uppercase">Género</p>
              <p className="text-blue-900 font-medium">{persona.genero}</p>
            </div>

            <div>
              <p className="text-xs text-gray-400 uppercase">Estatus migratorio</p>
              <span className="inline-block px-3 py-1 mt-1 text-xs rounded-full bg-purple-100 text-purple-700 font-semibold">
                {persona.estatusMigratorio || "PENDIENTE"}
              </span>
            </div>

            <div>
              <p className="text-xs text-gray-400 uppercase">Altura / Peso</p>
              <p className="text-blue-900 font-medium">
                {persona.estatura} m / {persona.peso} kg
              </p>
            </div>

            <div>
              <p className="text-xs text-gray-400 uppercase">Teléfono</p>
              <p className="text-blue-900 font-medium">{persona.telefono}</p>
            </div>

            <div>
              <p className="text-xs text-gray-400 uppercase">Correo electrónico</p>
              <p className="text-blue-900 font-medium underline">{persona.email}</p>
            </div>
          </div>
        </section>

        {/* COLUMNA DERECHA: INDICADORES */}
        <aside className="space-y-6">
          <section className="bg-blue-900 p-6 rounded-2xl text-white shadow-lg">
            <h2 className="text-lg font-semibold mb-4 text-blue-100">Indicadores Salud</h2>

            <div className="bg-white/10 p-4 rounded-xl backdrop-blur-sm">
              <p className="text-xs text-blue-200 uppercase">IMC (Calculado)</p>
              <p className="text-3xl font-bold mt-1">
                {calcularIMC(persona.peso, persona.estatura)}
              </p>
              <span className="text-[10px] bg-green-500 text-white px-2 py-0.5 rounded-full mt-2 inline-block">
                RANGO NORMAL
              </span>
            </div>
          </section>

          <section className="bg-white p-6 rounded-2xl shadow">
            <h2 className="text-sm font-bold text-gray-400 uppercase mb-4">Documentos</h2>
            <div className="space-y-3">
               <div className="p-3 bg-gray-50 rounded-lg border border-dashed border-gray-300">
                  <p className="text-xs text-gray-400">CURP</p>
                  <p className="text-sm font-mono font-bold text-blue-900">VER-EN-BASE-DE-DATOS</p>
               </div>
               <div className="p-3 bg-gray-50 rounded-lg border border-dashed border-gray-300">
                  <p className="text-xs text-gray-400">RFC</p>
                  <p className="text-sm font-mono font-bold text-blue-900">PENDIENTE-VALIDAR</p>
               </div>
            </div>
          </section>
        </aside>

      </div>
    </main>
  );
}
