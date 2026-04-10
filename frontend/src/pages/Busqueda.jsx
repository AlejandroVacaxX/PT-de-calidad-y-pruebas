import { useState } from "react";
import { navigate } from "../Link";
import {
    FiSearch,
    FiExternalLink,
    FiAlertCircle,
    FiHelpCircle,
    FiLock,
} from "react-icons/fi";

const PRIMARY = "#003366";
const LIGHT_BLUE = "#E6F0FF";

export default function Busqueda() {
    const [modo, setModo] = useState("curp"); // "curp" | "rfc"
    const [valor, setValor] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [persona, setPersona] = useState(null);
    const [mostrarResultado, setMostrarResultado] = useState(false);

    const placeholder =
        modo === "curp"
            ? "Ej. VAMR850101HDFRLN09"
            : "Ej. VAMR850101ABC";

    const validarCurp = async (curp) => {
        try {
            setLoading(true);
            const response = await fetch(
                `http://localhost:8080/personas/personas-api/curp/${encodeURIComponent(curp)}`
            );

            if (!response.ok) throw new Error("Persona no encontrada");
            const data = await response.json();
            setPersona(data);
            setMostrarResultado(true);
            setError("");
        } catch (err) {
            console.error("Error al obtener los datos:", err);
            setPersona(null);
            setMostrarResultado(false);
            setError("No se encontró una persona con ese CURP.");
        } finally {
            setLoading(false);
        }
    };

    const validarRfc = async (rfc) => {
        try {
            setLoading(true);
            const response = await fetch(
                `http://localhost:8080/personas/personas-api/rfc/${encodeURIComponent(rfc)}`
            );

            if (!response.ok) throw new Error("Persona no encontrada");
            const data = await response.json();
            setPersona(data);
            setMostrarResultado(true);
            setError("");
        } catch (err) {
            console.error("Error al obtener los datos:", err);
            setPersona(null);
            setMostrarResultado(false);
            setError("No se encontró una persona con ese RFC.");
        } finally {
            setLoading(false);
        }
    };

    const handleConsultar = async (e) => {
        e.preventDefault();
        const valorNormalizado = valor.trim().toUpperCase();

        if (!valorNormalizado) {
            setError("Ingresa un valor para buscar.");
            setMostrarResultado(false);
            setPersona(null);
            return;
        }

        if (modo === "curp") {
            await validarCurp(valorNormalizado);
        } else {
            await validarRfc(valorNormalizado);
        }
    };

    return (
        <div className="p-8 max-w-6xl mx-auto">
            <header className="mb-8">
                <h1
                    className="text-3xl font-bold tracking-tight mb-2"
                    style={{ color: PRIMARY }}
                >
                    Consulta por Identificador
                </h1>
                <p className="text-slate-600 max-w-2xl text-[15px] leading-relaxed">
                    Ingrese un CURP o RFC válido para localizar el registro en el sistema
                    y visualizar un resumen del perfil asociado.
                </p>
            </header>

            <div className="grid grid-cols-1 lg:grid-cols-[minmax(0,1fr)_minmax(280px,0.42fr)] gap-6 items-start">
                {/* Columna izquierda: formulario */}
                <section
                    className="bg-white rounded-2xl shadow-md border border-slate-100/80 p-6 md:p-8"
                    style={{ boxShadow: "0 4px 6px -1px rgb(0 0 0 / 0.08)" }}
                >
                    <div
                        className="inline-flex rounded-full p-1 mb-6"
                        style={{ backgroundColor: LIGHT_BLUE }}
                    >
                        <button
                            type="button"
                            onClick={() => {
                                setModo("curp");
                                setError("");
                                setMostrarResultado(false);
                                setPersona(null);
                            }}
                            className={`px-5 py-2 rounded-full text-sm font-semibold transition-colors ${
                                modo === "curp"
                                    ? "bg-white text-blue-900 shadow-sm"
                                    : "text-slate-600 hover:text-slate-800"
                            }`}
                        >
                            Búsqueda por CURP
                        </button>
                        <button
                            type="button"
                            onClick={() => {
                                setModo("rfc");
                                setError("");
                                setMostrarResultado(false);
                                setPersona(null);
                            }}
                            className={`px-5 py-2 rounded-full text-sm font-semibold transition-colors ${
                                modo === "rfc"
                                    ? "bg-white text-blue-900 shadow-sm"
                                    : "text-slate-600 hover:text-slate-800"
                            }`}
                        >
                            Búsqueda por RFC
                        </button>
                    </div>

                    <form onSubmit={handleConsultar} className="space-y-4">
                        <div>
                            <label
                                className="block text-xs font-bold uppercase tracking-wider mb-2"
                                style={{ color: PRIMARY }}
                                htmlFor="identificador"
                            >
                                {modo === "curp"
                                    ? "Clave Única de Registro de Población"
                                    : "Registro Federal de Contribuyentes"}
                            </label>
                            <div className="relative">
                                <input
                                    id="identificador"
                                    type="text"
                                    value={valor}
                                    onChange={(e) => {
                                        setValor(e.target.value.toUpperCase());
                                        if (error) setError("");
                                    }}
                                    placeholder={placeholder}
                                    className="w-full rounded-xl bg-slate-100 border-0 px-4 py-3.5 pr-36 text-slate-800 placeholder:text-slate-400 focus:ring-2 focus:ring-blue-900/20 focus:outline-none font-mono uppercase"
                                    autoComplete="off"
                                />
                                <span className="absolute right-3 top-1/2 -translate-y-1/2 flex items-center gap-1.5 text-[10px] font-semibold uppercase tracking-tight text-slate-400">
                  {modo === "curp" ? "18 chars requeridos" : "12–13 chars"}
                                    <FiSearch className="w-3.5 h-3.5 opacity-60" aria-hidden />
                </span>
                            </div>
                            {error && (
                                <div
                                    className="mt-3 inline-flex items-center gap-2 rounded-full px-3 py-1.5 text-xs font-medium text-red-700 bg-red-50 border border-red-100"
                                    role="alert"
                                >
                                    <FiAlertCircle className="w-4 h-4 shrink-0" />
                                    {error}
                                </div>
                            )}
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full sm:w-auto inline-flex items-center justify-center gap-2 rounded-xl px-8 py-3.5 text-sm font-bold uppercase tracking-wide text-white transition-colors hover:opacity-95"
                            style={{ backgroundColor: PRIMARY }}
                        >
                            <FiSearch className="w-5 h-5" />
                            {loading ? "Consultando..." : "Consultar"}
                        </button>
                    </form>
                </section>

                {/* Columna derecha: ayuda */}
                <aside
                    className="relative rounded-2xl p-6 md:p-7 overflow-hidden min-h-[280px]"
                    style={{ backgroundColor: LIGHT_BLUE }}
                >
                    <h3
                        className="text-sm font-bold uppercase tracking-wider mb-3"
                        style={{ color: PRIMARY }}
                    >
                        Ayuda técnica
                    </h3>
                    <p className="text-sm text-slate-700 leading-relaxed mb-4">
                        El CURP consta de 18 caracteres alfanuméricos. El RFC para persona
                        física suele tener 13 caracteres (homoclave de 3). Revise mayúsculas
                        y evite espacios al copiar y pegar.
                    </p>
                    <a
                        href="#"
                        className="inline-flex items-center gap-1 text-sm font-semibold text-blue-800 hover:underline"
                        onClick={(e) => e.preventDefault()}
                    >
                        Manual de formatos →
                    </a>
                    <FiHelpCircle
                        className="absolute -bottom-2 -right-2 w-40 h-40 text-blue-900/5 pointer-events-none"
                        aria-hidden
                    />
                </aside>
            </div>

            {/* Resultados */}
            {mostrarResultado && (
                <div className="mt-12">
                    <div className="flex items-center gap-4 mb-8">
                        <div className="h-px flex-1 bg-slate-200" />
                        <span className="text-xs font-semibold uppercase tracking-widest text-slate-400">
              Resultado encontrado
            </span>
                        <div className="h-px flex-1 bg-slate-200" />
                    </div>

                    <article
                        className="bg-white rounded-2xl shadow-md border border-slate-100/80 p-6 flex flex-col sm:flex-row sm:items-stretch gap-6"
                        style={{ boxShadow: "0 4px 6px -1px rgb(0 0 0 / 0.08)" }}
                    >
                        <div
                            className="shrink-0 w-full sm:w-28 h-28 rounded-xl flex items-center justify-center"
                            style={{ background: "linear-gradient(145deg, #0d9488, #0369a1)" }}
                        >
                            <FiLock className="w-10 h-10 text-white/90" aria-hidden />
                        </div>

                        <div className="flex-1 min-w-0 space-y-3">
              <span
                  className="inline-block text-xs font-bold uppercase tracking-wide px-2.5 py-1 rounded-full"
                  style={{ backgroundColor: LIGHT_BLUE, color: PRIMARY }}
              >
                Activo en sistema
              </span>
                            <h2
                                className="text-xl font-bold truncate"
                                style={{ color: PRIMARY }}
                            >
                                {`${persona?.nombre || ""} ${persona?.apellidoPaterno || ""} ${
                                    persona?.apellidoMaterno || ""
                                }`.trim() || "Sin nombre"}
                            </h2>
                            <p className="text-sm text-slate-500 font-mono">
                                ID: {persona?.id ?? "N/A"}
                            </p>

                            <dl className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-2">
                                <div>
                                    <dt className="text-[10px] font-bold uppercase tracking-wider text-slate-500">
                                        Nacionalidad
                                    </dt>
                                    <dd className="text-sm font-semibold text-slate-800 mt-0.5">
                                        {persona?.estatusMigratorio || "No disponible"}
                                    </dd>
                                </div>
                                <div>
                                    <dt className="text-[10px] font-bold uppercase tracking-wider text-slate-500">
                                        Estado
                                    </dt>
                                    <dd className="text-sm font-semibold text-slate-800 mt-0.5">
                                        Perfil activo
                                    </dd>
                                </div>
                                <div>
                                    <dt className="text-[10px] font-bold uppercase tracking-wider text-slate-500">
                                        Registrado desde
                                    </dt>
                                    <dd className="text-sm font-semibold text-slate-800 mt-0.5">
                                        {persona?.fechaDeNacimiento || "No disponible"}
                                    </dd>
                                </div>
                            </dl>
                        </div>

                        <div className="flex sm:items-center shrink-0">
                            <button
                                type="button"
                                onClick={() => persona?.id && navigate(`/infopersona/${persona.id}`)}
                                className="w-full sm:w-auto inline-flex items-center justify-center gap-2 rounded-xl px-5 py-3 text-sm font-bold whitespace-nowrap transition-colors hover:bg-blue-100/80"
                                style={{ backgroundColor: LIGHT_BLUE, color: PRIMARY }}
                            >
                                Ver perfil completo
                                <FiExternalLink className="w-4 h-4" />
                            </button>
                        </div>
                    </article>
                </div>
            )}
        </div>
    );
}
