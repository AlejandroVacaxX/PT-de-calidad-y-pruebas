import { useEffect, useState } from "react";
import { navigate } from "../Link";

export default function InfoPersona() {
  const [persona, setPersona] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [editando, setEditando] = useState(false);
  const [formData, setFormData] = useState({});

  // Extraemos el ID de la URL
  const pathParts = window.location.pathname.split("/").filter(Boolean);
  const id = pathParts[pathParts.length - 1];

  const handleNavigation = (path) => {
    navigate(path);
  };

  // Obtener los datos de la persona desde el backend
  const obtenerPersona = async () => {
    try {
      setLoading(true);
      const response = await fetch(
        `http://localhost:8080/personas/personas-api/id/${id}`
      );

      if (!response.ok) throw new Error("Persona no encontrada");

      const data = await response.json();
      setPersona(data);
      setFormData(data); // Inicializamos el formulario con los datos actuales
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

  // Manejar cambios en el formulario de edicion
  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  // Guardar los cambios realizados
  const handleGuardarCambios = async () => {
    try {
      // Ajuste de estatura si se cambio (convertir de cm a metros si es necesario)
      // Nota: Si el usuario ve metros, lo dejamos igual. Si ve cm, dividimos.
      // Aqui asumiremos que el input muestra metros para mantener consistencia con la vista.
      
      const response = await fetch(`http://localhost:8080/personas/personas-api/id/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData)
      });

      if (!response.ok) throw new Error("Error al actualizar");

      const dataActualizada = await response.json();
      setPersona(dataActualizada);
      setEditando(false);
      alert("Registro actualizado con exito");
    } catch (err) {
      console.error(err);
      alert("Hubo un error al intentar guardar los cambios");
    }
  };

  // Eliminar el registro
  const handleEliminar = async () => {
    if (!window.confirm("¿Estas seguro de que deseas eliminar este registro? Esta accion no se puede deshacer.")) {
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/personas/personas-api/id/${id}`, {
        method: "DELETE"
      });

      if (!response.ok) throw new Error("Error al eliminar");
      
      alert("Persona eliminada correctamente");
      handleNavigation("/");
    } catch (err) {
      console.error(err);
      alert("No se pudo eliminar el registro");
    }
  };

  if (loading) return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-900"></div>
      <p className="ml-4 text-blue-900 font-bold">Cargando perfil...</p>
    </div>
  );

  if (error || !persona) {
    return (
      <div className="p-8 text-center bg-gray-100 min-h-screen">
        <div className="bg-white p-10 rounded-2xl shadow-lg inline-block">
          <p className="text-red-500 font-bold text-xl mb-4">⚠️ No se encontro la informacion.</p>
          <button 
            onClick={() => handleNavigation("/")} 
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
          >
            Regresar al listado
          </button>
        </div>
      </div>
    );
  }

  return (
    <main className="p-8 bg-gray-100 min-h-screen">
      {/* ACCIONES SUPERIORES */}
      <div className="flex justify-between items-center mb-6">
        <button
          onClick={() => handleNavigation("/")}
          className="text-blue-600 flex items-center gap-2 hover:font-bold transition-all"
        >
          ← Volver al Listado
        </button>

        <div className="flex gap-3">
          {!editando ? (
            <>
              <button 
                onClick={() => setEditando(true)}
                className="px-4 py-2 rounded-lg bg-blue-100 text-blue-700 hover:bg-blue-200 transition font-medium"
              >
                Editar Perfil
              </button>
              <button 
                onClick={handleEliminar}
                className="px-4 py-2 rounded-lg bg-red-100 text-red-600 hover:bg-red-200 transition font-medium"
              >
                Eliminar
              </button>
            </>
          ) : (
            <>
              <button 
                onClick={() => setEditando(false)}
                className="px-4 py-2 rounded-lg bg-gray-200 text-gray-700 hover:bg-gray-300 transition font-medium"
              >
                Cancelar
              </button>
              <button 
                onClick={handleGuardarCambios}
                className="px-4 py-2 rounded-lg bg-green-600 text-white hover:bg-green-700 transition font-medium shadow-md"
              >
                Guardar Cambios
              </button>
            </>
          )}
        </div>
      </div>

      {/* CABECERA DEL PERFIL */}
      <section className="bg-white p-8 rounded-2xl shadow-sm mb-6 flex flex-col md:flex-row justify-between items-center gap-6">
        <div className="flex items-center gap-6">
          <div className="w-24 h-24 bg-blue-900 rounded-2xl flex items-center justify-center text-white text-3xl font-bold shadow-inner">
            {persona.nombre?.charAt(0)}{persona.apellidoPaterno?.charAt(0)}
          </div>

          <div>
            {!editando ? (
              <h1 className="text-3xl font-bold text-blue-900 leading-tight">
                {persona.nombre} {persona.apellidoPaterno} {persona.apellidoMaterno}
              </h1>
            ) : (
              <div className="flex gap-2">
                <input 
                  className="p-1 border rounded text-lg font-bold text-blue-900 w-32" 
                  name="nombre" 
                  value={formData.nombre} 
                  onChange={handleChange} 
                />
                <input 
                  className="p-1 border rounded text-lg font-bold text-blue-900 w-32" 
                  name="apellidoPaterno" 
                  value={formData.apellidoPaterno} 
                  onChange={handleChange} 
                />
                <input 
                  className="p-1 border rounded text-lg font-bold text-blue-900 w-32" 
                  name="apellidoMaterno" 
                  value={formData.apellidoMaterno} 
                  onChange={handleChange} 
                />
              </div>
            )}
            <div className="flex items-center gap-2 mt-1">
              <span className="text-xs font-bold text-gray-400 tracking-widest uppercase">ID:</span>
              <span className="text-sm font-mono text-gray-500">{persona.id}</span>
            </div>
          </div>
        </div>

        {!editando && (
          <div className="bg-green-50 px-4 py-2 rounded-full border border-green-100 flex items-center gap-2">
            <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
            <p className="text-xs text-green-700 font-bold uppercase tracking-wider">Perfil Activo</p>
          </div>
        )}
      </section>

      {/* CONTENIDO PRINCIPAL */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">

        {/* SECCION DE DATOS PERSONALES */}
        <section className="lg:col-span-2 bg-white p-8 rounded-2xl shadow-sm border border-gray-50">
          <h2 className="text-xl font-bold text-blue-900 mb-8 flex items-center gap-2">
            <span className="w-1.5 h-6 bg-blue-600 rounded-full"></span>
            Informacion Personal
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-y-8 gap-x-6">
            <div className="space-y-1">
              <p className="text-xs text-gray-400 font-bold uppercase tracking-wider">Fecha de nacimiento</p>
              {!editando ? (
                <p className="text-blue-900 font-semibold text-lg">{persona.fechaDeNacimiento || "No registrada"}</p>
              ) : (
                <input 
                  className="w-full p-2 border rounded-lg" 
                  type="text" 
                  name="fechaDeNacimiento" 
                  placeholder="dd/MM/yyyy"
                  value={formData.fechaDeNacimiento} 
                  onChange={handleChange} 
                />
              )}
            </div>

            <div className="space-y-1">
              <p className="text-xs text-gray-400 font-bold uppercase tracking-wider">Genero</p>
              {!editando ? (
                <p className="text-blue-900 font-semibold text-lg">{persona.genero}</p>
              ) : (
                <select 
                  className="w-full p-2 border rounded-lg" 
                  name="genero" 
                  value={formData.genero} 
                  onChange={handleChange}
                >
                  <option>Masculino</option>
                  <option>Femenino</option>
                  <option>No binario</option>
                </select>
              )}
            </div>

            <div className="space-y-1">
              <p className="text-xs text-gray-400 font-bold uppercase tracking-wider">Estatus migratorio</p>
              {!editando ? (
                <div>
                  <span className="inline-block px-4 py-1.5 text-xs rounded-lg bg-blue-50 text-blue-700 font-bold border border-blue-100">
                    {persona.estatusMigratorio?.toUpperCase() || "PENDIENTE"}
                  </span>
                </div>
              ) : (
                <select 
                  className="w-full p-2 border rounded-lg" 
                  name="estatusMigratorio" 
                  value={formData.estatusMigratorio} 
                  onChange={handleChange}
                >
                  <option>Mexicano</option>
                  <option>Ciudadano</option>
                  <option>Nacionalizado</option>
                  <option>Residente Temporal</option>
                  <option>Residente Permanente</option>
                </select>
              )}
            </div>

            <div className="space-y-1">
              <p className="text-xs text-gray-400 font-bold uppercase tracking-wider">Medidas Fisicas (m / kg)</p>
              {!editando ? (
                <p className="text-blue-900 font-semibold text-lg">
                  {persona.estatura} m <span className="text-gray-300 mx-2">|</span> {persona.peso} kg
                </p>
              ) : (
                <div className="flex gap-2">
                  <input 
                    className="w-1/2 p-2 border rounded-lg" 
                    type="number" 
                    step="0.01" 
                    name="estatura" 
                    value={formData.estatura} 
                    onChange={handleChange} 
                  />
                  <input 
                    className="w-1/2 p-2 border rounded-lg" 
                    type="number" 
                    name="peso" 
                    value={formData.peso} 
                    onChange={handleChange} 
                  />
                </div>
              )}
            </div>

            <div className="space-y-1">
              <p className="text-xs text-gray-400 font-bold uppercase tracking-wider">Telefono</p>
              {!editando ? (
                <p className="text-blue-900 font-semibold text-lg">{persona.telefono || "Sin telefono"}</p>
              ) : (
                <input 
                  className="w-full p-2 border rounded-lg" 
                  type="text" 
                  name="telefono" 
                  value={formData.telefono} 
                  onChange={handleChange} 
                />
              )}
            </div>

            <div className="space-y-1">
              <p className="text-xs text-gray-400 font-bold uppercase tracking-wider">Correo electronico</p>
              {!editando ? (
                <p className="text-blue-600 font-semibold text-lg hover:underline cursor-pointer">{persona.email}</p>
              ) : (
                <input 
                  className="w-full p-2 border rounded-lg" 
                  type="email" 
                  name="email" 
                  value={formData.email} 
                  onChange={handleChange} 
                />
              )}
            </div>
          </div>
        </section>

        {/* COLUMNA DE INDICADORES Y DOCUMENTOS */}
        <aside className="space-y-8">
          {/* TARJETA DE SALUD (IMC) */}
          <section className="bg-gradient-to-br from-blue-900 to-indigo-900 p-8 rounded-2xl text-white shadow-xl transform transition-transform hover:scale-[1.02]">
            <h2 className="text-lg font-bold mb-6 text-blue-100 flex items-center gap-2">
              Indicadores de Salud
            </h2>

            <div className="flex flex-col items-center">
              <p className="text-xs text-blue-200 font-bold uppercase tracking-widest mb-1">Indice de Masa Corporal (IMC)</p>
              <p className="text-5xl font-black mb-3 leading-none">
                {persona.imc || "0.0"}
              </p>
              <div className="bg-white/20 backdrop-blur-md px-4 py-1.5 rounded-full border border-white/10 text-center">
                <span className="text-[10px] font-bold uppercase tracking-tighter">
                  {editando ? "Se recalculara al guardar" : "Calculado por el sistema"}
                </span>
              </div>
            </div>
          </section>

          {/* TARJETA DE DOCUMENTOS OFICIALES */}
          <section className="bg-white p-8 rounded-2xl shadow-sm border border-gray-50">
            <h2 className="text-sm font-black text-gray-400 uppercase mb-6 tracking-widest">Documentos Oficiales</h2>
            <div className="space-y-4">
               <div className="p-4 bg-gray-50 rounded-xl border border-gray-100 group hover:border-blue-200 transition-colors">
                  <p className="text-[10px] text-gray-400 font-bold uppercase mb-1">CURP</p>
                  <p className="text-sm font-mono font-bold text-blue-900 tracking-wider">
                    {persona.curp || "GENERANDO..."}
                  </p>
               </div>
               <div className="p-4 bg-gray-50 rounded-xl border border-gray-100 group hover:border-blue-200 transition-colors">
                  <p className="text-[10px] text-gray-400 font-bold uppercase mb-1">RFC</p>
                  <p className="text-sm font-mono font-bold text-blue-900 tracking-wider">
                    {persona.rfc || "GENERANDO..."}
                  </p>
               </div>
               {editando && (
                 <p className="text-[10px] text-orange-500 font-bold text-center">
                   * CURP y RFC se actualizan automaticamente si cambian los datos base.
                 </p>
               )}
            </div>
          </section>
        </aside>

      </div>
    </main>
  );
}
