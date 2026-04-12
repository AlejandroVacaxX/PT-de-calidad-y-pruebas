import { useState } from "react";
import { API_URL } from "../consts";

export default function Registro() {

  const [form, setForm] = useState({
    nombre: "",
    apellidoPaterno: "",
    apellidoMaterno: "",
    fechaDeNacimiento: "",
    genero: "",
    estatusMigratorio: "",
    email: "",
    telefono: "",
    estatura: "",
    peso: ""
  });

  const [mensaje, setMensaje] = useState("");

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const formatearFecha = (fecha) => {
    if (!fecha) return "";
    const [year, month, day] = fecha.split("-");
    return `${day}/${month}/${year}`;
  };

  const guardarPersona = async () => {
    try {
      // Mapeo de valores del formulario a los valores esperados por el backend (regex)
      const mapeoGenero = {
        "Masculino": "masculino",
        "Femenino": "femenino",
        "No binario": "nobinario"
      };

      const mapeoEstatus = {
        "Mexicano": "mexicano",
        "Ciudadano": "ciudadano",
        "Nacionalizado": "nacionalizado",
        "Residente Temporal": "residentetemporal",
        "Residente Permanente": "residentepermanente"
      };

      const persona = {
        ...form,
        genero: mapeoGenero[form.genero] || form.genero.toLowerCase(),
        estatusMigratorio: mapeoEstatus[form.estatusMigratorio] || form.estatusMigratorio.toLowerCase().replace(/\s+/g, ''),
        fechaDeNacimiento: formatearFecha(form.fechaDeNacimiento),
        estatura: parseFloat(form.estatura), // Convertir cm a metros, esta mamada esta fallando porque ya se pasa en metros
        peso: parseFloat(form.peso),
        // Aseguramos que el telefono solo tenga numeros para el regex ^(55\d{8})$
        telefono: form.telefono.replace(/\D/g, '')
      };

      // Validacion previa del telefono antes de enviar
      if (!/^55\d{8}$/.test(persona.telefono)) {
        setMensaje("❌ El teléfono debe tener 10 dígitos y empezar con 55");
        return;
      }

      const response = await fetch(API_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(persona)
      });

      if (!response.ok) {
        throw new Error("Error al guardar");
      }

      setMensaje("✅ Persona registrada correctamente");

      // Reset formulario
      setForm({
        nombre: "",
        apellidoPaterno: "",
        apellidoMaterno: "",
        fechaDeNacimiento: "",
        genero: "",
        estatusMigratorio: "",
        email: "",
        telefono: "",
        estatura: "",
        peso: ""
      });

    } catch (error) {
      console.error(error);
      setMensaje("❌ Error al registrar persona");
    }
  };

  return (
    <div className="p-8 bg-gray-100 min-h-full">

      {/* HEADER */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-blue-900">
            Registro de Persona
          </h1>
          <p className="text-gray-500">
            Complete los campos requeridos para dar de alta un nuevo perfil
          </p>
        </div>

        <div className="flex gap-3">
          <button
            onClick={() => setForm({
              nombre: "",
              apellidoPaterno: "",
              apellidoMaterno: "",
              fechaDeNacimiento: "",
              genero: "",
              estatusMigratorio: "",
              email: "",
              telefono: "",
              estatura: "",
              peso: ""
            })}
            className="px-4 py-2 rounded-lg bg-blue-100 text-blue-700"
          >
            Cancelar
          </button>

          <button
            onClick={guardarPersona}
            className="px-4 py-2 rounded-lg bg-blue-600 text-white shadow"
          >
            Guardar Registro
          </button>
        </div>
      </div>

      {/* MENSAJE */}
      {mensaje && (
        <div className="mb-4 text-center font-semibold">
          {mensaje}
        </div>
      )}

      {/* GRID */}
      <div className="grid grid-cols-3 gap-6">

        {/* INFORMACIÓN PERSONAL */}
        <section className="col-span-2 bg-white p-6 rounded-2xl shadow">
          <h2 className="text-lg font-semibold text-blue-900 mb-4">
            Información Personal
          </h2>

          <div className="grid grid-cols-2 gap-4">

            <div>
              <label className="text-sm text-gray-500">Nombre</label>
              <input
                className="input"
                type="text"
                name="nombre"
                value={form.nombre}
                onChange={handleChange}
              />
            </div>

            <div className="grid grid-cols-2 gap-2">
              <div>
                <label className="text-sm text-gray-500">Apellido Paterno</label>
                <input
                  className="input"
                  type="text"
                  name="apellidoPaterno"
                  value={form.apellidoPaterno}
                  onChange={handleChange}
                />
              </div>
              <div>
                <label className="text-sm text-gray-500">Apellido Materno</label>
                <input
                  className="input"
                  type="text"
                  name="apellidoMaterno"
                  value={form.apellidoMaterno}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div>
              <label className="text-sm text-gray-500">
                Fecha de nacimiento
              </label>
              <input
                className="input"
                type="date"
                name="fechaDeNacimiento"
                value={form.fechaDeNacimiento}
                max={new Date().toISOString().split("T")[0]}
                onChange={handleChange}
              />
            </div>

            <div>
              <label className="text-sm text-gray-500">Género</label>
              <select
                className="input"
                name="genero"
                value={form.genero}
                onChange={handleChange}
              >
                <option value="">Seleccione una opción</option>
                <option>Masculino</option>
                <option>Femenino</option>
                <option>No binario</option>
              </select>
            </div>
            <div>
              <label className="text-sm text-gray-500">Estado Migratorio</label>
              <select
                className="input"
                name="estatusMigratorio"
                value={form.estatusMigratorio}
                onChange={handleChange}
              >
                <option value="">Seleccione una opción</option>
                <option>Mexicano</option>
                <option>Ciudadano</option>
                <option>Nacionalizado</option>
                <option>Residente Temporal</option>
                <option>Residente Permanente</option>
              </select>
            </div>

          </div>
        </section>

        {/* CONTACTO */}
        <section className="bg-white p-6 rounded-2xl shadow">
          <h2 className="text-lg font-semibold text-blue-900 mb-4">
            Contacto
          </h2>

          <div className="space-y-4">

            <div>
              <label className="text-sm text-gray-500">Email</label>
              <input
                className="input"
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
              />
            </div>

            <div>
              <label className="text-sm text-gray-500">Teléfono</label>
              <input
                className="input"
                type="text"
                name="telefono"
                value={form.telefono}
                onChange={handleChange}
              />
            </div>

          </div>
        </section>

        {/* DATOS FÍSICOS */}
        <section className="bg-white p-6 rounded-2xl shadow">
          <h2 className="text-lg font-semibold text-blue-900 mb-4">
            Datos físicos
          </h2>

          <div className="grid grid-cols-2 gap-4">

            <div>
              <label className="text-sm text-gray-500">Estatura (cm)</label>
              <input
                className="input"
                type="number"
                name="estatura"
                value={form.estatura}
                onChange={handleChange}
              />
            </div>

            <div>
              <label className="text-sm text-gray-500">Peso (kg)</label>
              <input
                className="input"
                type="number"
                name="peso"
                value={form.peso}
                onChange={handleChange}
              />
            </div>

          </div>
        </section>

      </div>
    </div>
  );
}
