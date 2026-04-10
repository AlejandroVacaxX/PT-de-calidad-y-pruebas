# Problema de Conexión: React Frontend a Spring Boot Backend

## Descripción del Problema
El frontend en React no puede comunicarse con el backend en Spring Boot. Al intentar hacer un `fetch` a la API, el navegador rechaza la conexión de forma inmediata.

**Error exacto en la consola del navegador:**
`Failed to load resource: No se ha podido establecer conexión con el servidor.`
`TypeError: Load failed` en `ListadoPersonas.jsx:26`

## Contexto del Proyecto
* **Frontend:** React
* **Backend:** Java con Spring Boot
* **Puerto Backend:** `8080`
* **Ruta de la API:** `http://localhost:8080/personas/personas-api` (también se intentó con `127.0.0.1`)

## Intentos de Solución Previos (Sin éxito)
1. **Configuración CORS:** Se agregó `@CrossOrigin(origins = "*")` en el controlador de Spring Boot para descartar bloqueos por política de mismo origen.
2. **Resolución de DNS:** Se cambió la URL del fetch en React de `localhost` a `127.0.0.1` para evitar conflictos entre IPv4 e IPv6.
3. **Sintaxis de Rutas:** Se corrigió el `@RequestMapping` del controlador para incluir la barra inicial (`/personas/personas-api`).
4. **Verificación de ejecución:** El servidor de Spring Boot compila y arroja el mensaje de inicialización en consola.

## Archivos Relevantes

### 1. `ListadoPersonas.jsx` (Fragmento del Fetch)
```javascript
  // Funcion para obtener la lista de personas desde el backend
  const obtenerPersonas = async () => {
    try {
      setLoading(true);
      // Nota: Actualmente fallando en esta línea
      const response = await fetch("[http://127.0.0.1:8080/personas/personas-api](http://127.0.0.1:8080/personas/personas-api)");
      
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