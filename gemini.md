# Contexto del Proyecto: Sistema de Gestión de Personas

## 🏗 Arquitectura General (Monorepo)
Este proyecto es una aplicación Full-Stack dividida en dos motores independientes:
1. **Backend (Raíz del proyecto):** API REST construida con Java y Spring Boot.
2. **Frontend (`/frontend`):** Aplicación cliente construida con React y Vite.

## 💻 Stack Tecnológico
* **Backend:** Java 21+, Spring Boot 3.x, Google Cloud Firestore (Firebase Admin SDK).
* **Frontend:** React.js, Vite, Tailwind CSS, Node.js (Gestor de paquetes: `pnpm`).

## 🎯 Estado Actual y Objetivo Inmediato
* El CRUD del backend (Crear, Leer, Actualizar, Borrar) y la lógica de negocio (cálculo de CURP, RFC e IMC) ya están finalizados y conectados exitosamente a Firestore.
* Las vistas del frontend (UI) ya están maquetadas por el equipo.
* **OBJETIVO ACTUAL:** Realizar la integración HTTP entre el Frontend y el Backend. El frontend debe consumir los endpoints REST del backend.

## 🔌 Detalles de Conexión (Desarrollo Local)
* **Backend URL:** `http://localhost:8080/personas/personas-api`
* **Frontend URL:** `http://localhost:5173`

## 📜 Reglas Estrictas para el Asistente de IA

### Para el Backend (Spring Boot):
1. **Modelos:** Se utilizan `records` de Java para los modelos (ej. `PersonaModel`). No sugerir clases tradicionales con getters/setters.
2. **Base de Datos:** Es Firestore (NoSQL). El mapeo se hace de forma manual extrayendo datos del `DocumentSnapshot` (ej. `document.getString("nombre")`) para evitar errores de serialización con los `records`. 
3. **Cálculos en el Backend:** La creación del CURP, RFC y el cálculo del IMC ocurren estrictamente en el Repositorio de Spring Boot antes de guardar en Firestore. El frontend solo los recibe, no los calcula.
4. **CORS:** Es imperativo configurar CORS en Spring Boot para permitir el tráfico desde `http://localhost:5173`.

### Para el Frontend (React + Vite):
1. **Gestor de paquetes:** Usar exclusivamente comandos de `pnpm` (no `npm` ni `yarn`).
2. **Peticiones HTTP:** Sugerir implementaciones usando `fetch` nativo o `axios`, manejando correctamente los estados asíncronos (Loading, Error, Success).
3. **Manejo de Errores:** El frontend debe atrapar y mostrar amigablemente los errores que arroje el backend (ej. validaciones fallidas, IDs no encontrados).
4. **Git:** La carpeta `node_modules` está estrictamente prohibida en los commits.

### Estructura de Datos (PersonaModel)
Al enviar o recibir JSON, la estructura esperada es:
- `id` (String, generado por Firestore)
- `nombre`, `apellidoPaterno`, `apellidoMaterno` (String)
- `fechaDeNacimiento` (String, formato dd/MM/yyyy)
- `genero`, `estatusMigratorio` (String)
- `estatura`, `peso` (Double)
- `telefono`, `email` (String)
- `curp`, `rfc` (String, autogenerados)
- `imc` (Double, autogenerado)