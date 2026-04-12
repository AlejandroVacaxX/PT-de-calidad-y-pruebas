# Contexto: Refactorización de Arquitectura (CURP y RFC) con Patrón Repository

## Estado Actual
El proyecto en Java presenta un problema de acoplamiento: la lógica de negocio (generación de CURP/RFC y resolución de homonimias) residía junto con la lógica de persistencia de datos. Al intentar mover la lógica al `Service`, los métodos de persistencia se rompieron. 

## Objetivo
Refactorizar el código aplicando **Clean Architecture** y el principio de **Inversión de Dependencias (DIP)**:
1. **Repository (Interfaz):** Definirá el contrato de las operaciones de base de datos.
2. **RepositoryImpl (Implementación):** Contendrá el código duro (JDBC/SQL, etc.) para comunicarse con la base de datos, implementando la interfaz. No contendrá lógica de negocio.
3. **Service:** Orquestará las reglas de negocio, generará las claves, resolverá las colisiones (homónimos) interactuando con el repositorio, y ensamblará el objeto final antes de mandarlo a persistir.

---

## Tareas a Ejecutar en el Código

### 1. Crear el Contrato (Interfaz Repository)
El servicio dependerá de esta interfaz, desconociendo los detalles de la base de datos.

**Referencia de implementación esperada:**
```java
public interface PersonaRepository {
    void guardar(PersonaModel persona);
    void actualizar(PersonaModel persona);
    PersonaModel buscarPorId(Long id);
    boolean existePorCurp(String curp);
}