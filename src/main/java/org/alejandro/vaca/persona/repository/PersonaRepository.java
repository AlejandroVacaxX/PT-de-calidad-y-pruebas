package org.alejandro.vaca.persona.repository;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.alejandro.vaca.persona.Persona;
import org.alejandro.vaca.persona.model.PersonaModel;
import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

@Repository
public class PersonaRepository {
    // private final FirebaseConfig firebaseConfig;
    private static final String COLLECTION = "personas";
    public final Firestore firestore;
    public PersonaModel personaModel;

    public PersonaRepository(Firestore fire) {
        this.firestore = fire;
    }

    /*
     * Dentro de la lógica de negocios del programa, se calculará el IMC de la
     * persona, su RFC sin homoclave y su CURP, en el caso de este último, podrás
     * crear valores
     * aleatorios en
     * los dígitos 18 y 19 para completar la estructura.
     */

    public List<PersonaModel> obtenerTodos(){

        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION).get();
            QuerySnapshot querySnapshot = future.get();
            List<PersonaModel> personas = new ArrayList<>();

            for (DocumentSnapshot document : querySnapshot.getDocuments()){
                PersonaModel persona = document.toObject(PersonaModel.class);
                if (persona != null){
                    personas.add(new PersonaModel(
                            document.getId(),
                            persona.nombre(),
                            persona.apellidoPaterno(),
                            persona.apellidoMaterno(),
                            persona.fechaDeNacimiento(),
                            persona.genero(),
                            persona.estatusMigratorio(),
                            persona.estatura(),
                            persona.peso(),
                            persona.telefono(),
                            persona.email(),
                            persona.rfc(),
                            persona.curp(),
                            persona.imc()
                    ));
                }else{
                    System.out.println("No se encontró ninguna persona");
                }
            }
            return personas;
        }catch (Exception e){
            throw new RuntimeException("No fue posible obtener las personas por el error" + e);
        }
    }


    // Inician metodos de IMC
    public Double calcularIMC(Double peso, Double estatura) {

        return peso * (estatura * estatura);
    }

    public Optional<PersonaModel> obtenerPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();

        }
        try {
            DocumentReference documentReference = firestore.collection(COLLECTION).document(id);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                PersonaModel persona = document.toObject(PersonaModel.class);

                if (persona != null) {
                    return Optional.of(new PersonaModel(
                            document.getId(),
                            persona.nombre(),
                            persona.apellidoPaterno(),
                            persona.apellidoMaterno(),
                            persona.fechaDeNacimiento(),
                            persona.genero(),
                            persona.estatusMigratorio(),
                            persona.estatura(),
                            persona.peso(),
                            persona.telefono(),
                            persona.email(),
                            document.getString("curp"),
                            document.getString("rfc"),
                            document.getDouble("imc")

                    ));

                } else {
                    System.out.println("No se encontro la persona con el id: " + id + " .");
                }
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("No fue posible obtener a la persona por el error: " + e);
        }

    }

    public void getRfcPorId() {

    }

    public void getRfcPorCurp(String curp) {

    }

    public void updateRfcPorCurp() {

    }

    public void updateRfcPorNombreYFechaNac(String nombreCompleto, String fechaNac) {

    }

    public boolean esMayorDe16Anios(String fechaDeNacimiento) {
        try {

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            LocalDate fechaNac = LocalDate.parse(fechaDeNacimiento, formato);

            LocalDate hoy = LocalDate.now();

            Period edad = Period.between(fechaNac, hoy);

            return edad.getYears() >= 16;

        } catch (DateTimeParseException e) {

            System.err.println("Formato de fecha inválido: " + e.getMessage());
            return false;
        }
    }

    private void validarPersona(PersonaModel persona) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
    }

    private void validarDatoPorNullYVacio(String dato) {
        if (dato == null || dato.isBlank())
            throw new IllegalArgumentException("El valor no puede ser null");
    }

    private String generarRaizCurpRfc(PersonaModel persona) {

        String paterno = persona.apellidoPaterno().toUpperCase();
        String materno = persona.apellidoMaterno().toUpperCase();
        String nombre = persona.nombre().toUpperCase();
        String fecha = persona.fechaDeNacimiento();

        String letras = paterno.substring(0, 2) + materno.substring(0, 1) + nombre.substring(0, 1);

        String dia = fecha.substring(0, 2);
        String mes = fecha.substring(3, 5);
        String anio = fecha.substring(8, 10);

        return letras + anio + mes + dia;
    }

    public String generarCurp(PersonaModel persona) {
        if (persona.estatusMigratorio() == null || persona.estatusMigratorio().isBlank()) {
            throw new IllegalArgumentException("El estatus migratorio no puede ser nulo o estar vacío");
        }

        String raiz = generarRaizCurpRfc(persona);
        String inicialGenero = persona.genero() != null ? String.valueOf(persona.genero().charAt(0)).toUpperCase()
                : "X";

        return raiz + inicialGenero + "X" + "XX" + "00";
    }

    public String generarRfc(PersonaModel persona) {
        if (!esMayorDe16Anios(persona.fechaDeNacimiento())) {
            throw new IllegalArgumentException("La persona debe ser mayor de 16 años para generar el RFC");
        }

        String raiz = generarRaizCurpRfc(persona);

        return raiz;
    }

    public String crearCurp(PersonaModel persona) {
        validarPersona(persona);
        try {
            String curpGenerada = generarCurp(persona);
            System.out.println("CURP Generada: " + curpGenerada);
            return curpGenerada;
        } catch (Exception e) {
            throw new RuntimeException("No fue posible generar la CURP por el error: " + e.getMessage(), e);
        }
    }

    public String  crearRFC(PersonaModel persona) {
        validarPersona(persona);
        try {
            String rfcGenerado = generarRfc(persona);
            System.out.println("RFC Generado: " + rfcGenerado);
            return rfcGenerado;
        } catch (Exception e) {
            throw new RuntimeException("No fue posible generar el RFC por el error: " + e.getMessage(), e);
        }
    }

    public Optional<PersonaModel> getPersonaPorCurp(String curp) {
        if (curp == null || curp.isBlank()) {
            return Optional.empty();
        }
        try {
            DocumentReference documentReference = firestore.collection(COLLECTION).document(curp);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                PersonaModel persona = document.toObject(PersonaModel.class);
                if (persona != null) {
                    return Optional.of(new PersonaModel(
                            document.getId(),
                            persona.nombre(),
                            persona.apellidoPaterno(),
                            persona.apellidoMaterno(),
                            persona.fechaDeNacimiento(),
                            persona.genero(),
                            persona.estatusMigratorio(),
                            persona.estatura(),
                            persona.peso(),
                            persona.telefono(),
                            persona.email(),
                            persona.curp(),
                            persona.rfc(),
                            persona.imc()
                            ));

                } else {
                    System.out.println("No se encontro persona con el curp: " + curp);
                }

            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("No fue posible obtener a la persona con el curp: " + curp);
        }

    }

    public Optional<PersonaModel> getPersonaPorRFC(String rfc) {
        if (rfc == null || rfc.isBlank()) {
            return Optional.empty();
        }
        try {
            DocumentReference documentReference = firestore.collection(COLLECTION).document(rfc);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                PersonaModel persona = document.toObject(PersonaModel.class);
                if (persona != null) {
                    return Optional.of(new PersonaModel(
                            document.getId(),
                            persona.nombre(),
                            persona.apellidoPaterno(),
                            persona.apellidoMaterno(),
                            persona.fechaDeNacimiento(),
                            persona.genero(),
                            persona.estatusMigratorio(),
                            persona.estatura(),
                            persona.peso(),
                            persona.telefono(),
                            persona.email(),
                            persona.curp(),
                            persona.rfc(),
                            persona.imc()));

                } else {
                    System.out.println("No se encontro persona con el rfc: " + rfc);
                }

            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("No fue posible obtener a la persona con el rfc: " + rfc);
        }

    }

    public boolean deletePersonaPorId(String id) {
        validarDatoPorNullYVacio(id);
        try {
            DocumentReference documentReference = firestore.collection(COLLECTION).document(id);
            ApiFuture<WriteResult> future = documentReference.delete();
            future.get();
            System.out.println("Los datos de la persona con el ID: " + id + "Fueron eliminados exitosamente");
            return true;
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error al intentar borrar los datos de la persona con ID:" + id);
            return false;
        }

    }

    // MetodoGenericoDelete
    private boolean deletePersonaGenerico(String identificador, String tipoBusqueda) {

        if (identificador == null || identificador.isBlank()) {
            throw new IllegalArgumentException(
                    "El " + tipoBusqueda + " no puede ser nulo");
        }
        try {
            DocumentReference documentReference = firestore.collection(COLLECTION).document(identificador);
            ApiFuture<WriteResult> future = documentReference.delete();
            future.get();
            System.out.println("Los datos de la persona con el " + tipoBusqueda + ": " + identificador + " fueron eliminados exitosamente");
            return true;

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error al intentar borrar los datos con " + tipoBusqueda + ": " + identificador);
            return false;
        }
    }

    public void deletePersonaPorRFC(String rfc) {
        deletePersonaGenerico(rfc, "RFC");
    }

    public void deletePersonaPorCurp(String curp) {
        deletePersonaGenerico(curp, "CURP");
    }


    // Metodo Generico para Update
    private PersonaModel ejecutarActualizacionBase(String identificador, PersonaModel personaActualizada,
            String tipoBusqueda) {
        if (identificador == null || identificador.isBlank() || personaActualizada == null) {
            throw new IllegalArgumentException(
                    "El " + tipoBusqueda + " y los datos de actualización no pueden ser nulos");
        }

        try {

            DocumentReference documentReference = firestore.collection(COLLECTION).document(identificador);

            ApiFuture<DocumentSnapshot> futureSnapshot = documentReference.get();
            DocumentSnapshot document = futureSnapshot.get();

            if (!document.exists()) {
                throw new RuntimeException(
                        "No se puede actualizar. No existe registro con el " + tipoBusqueda + ": " + identificador);
            }

            ApiFuture<WriteResult> futureWrite = documentReference.set(personaActualizada);
            futureWrite.get();

            return new PersonaModel(
                    identificador,
                    personaActualizada.nombre(),
                    personaActualizada.apellidoPaterno(),
                    personaActualizada.apellidoMaterno(),
                    personaActualizada.fechaDeNacimiento(),
                    personaActualizada.genero(),
                    personaActualizada.estatusMigratorio(),
                    personaActualizada.estatura(),
                    personaActualizada.peso(),
                    personaActualizada.telefono(),
                    personaActualizada.email(),
                    document.getString("curp"),
                    document.getString("rfc"),
                    document.getDouble("imc"));

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(
                    "Falló la actualización de la persona con " + tipoBusqueda + ": " + identificador, e);
        }
    }

    public PersonaModel actualizarPersonaPorId(String id, PersonaModel personaActualizada) {
        return ejecutarActualizacionBase(id, personaActualizada, "ID");
    }

    public PersonaModel actualizarPersonaPorCurp(String curp, PersonaModel personaActualizada) {
        return ejecutarActualizacionBase(curp, personaActualizada, "CURP");
    }

    public PersonaModel actualizarPersonaPorRFC(String rfc, PersonaModel personaActualizada) {
        return ejecutarActualizacionBase(rfc, personaActualizada, "RFC");
    }


    public List<PersonaModel> getPersonasPorNombre(String nombre, String apellidoPaterno, String apellidoMaterno) {
        // Validamos que los tres datos vengan con información
        if (nombre == null || nombre.isBlank() || apellidoPaterno == null || apellidoPaterno.isBlank() || apellidoMaterno == null || apellidoMaterno.isBlank()) {
            return new ArrayList<>();
        }
        
        try {
            Query query = firestore.collection(COLLECTION)
                    .whereEqualTo("nombre", nombre)
                    .whereEqualTo("apellidoPaterno", apellidoPaterno)
                    .whereEqualTo("apellidoMaterno", apellidoMaterno);
                    
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            List<PersonaModel> personasEncontradas = new ArrayList<>();

            // AQUÍ ESTÁ EL CAMBIO CLAVE
            for (DocumentSnapshot document : documents) {
                // Eliminamos toObject() y el if. Extraemos directo del document.
                personasEncontradas.add(new PersonaModel(
                        document.getId(),
                        document.getString("nombre"),
                        document.getString("apellidoPaterno"),
                        document.getString("apellidoMaterno"),
                        document.getString("fechaDeNacimiento"),
                        document.getString("genero"),
                        document.getString("estatusMigratorio"),
                        document.getDouble("estatura"),
                        document.getDouble("peso"),
                        document.getString("telefono"),
                        document.getString("email"),
                        document.getString("curp"),
                        document.getString("rfc"),
                        document.getDouble("imc")
                ));
            }
            
            if (personasEncontradas.isEmpty()) {
                System.out.println("No se encontró ninguna persona con el nombre: " + nombre + " " + apellidoPaterno + " " + apellidoMaterno);
            }
            return personasEncontradas;
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("No fue posible obtener a las personas por el nombre: " + nombre
                    + " por el error: " + e.getMessage());
        }
    }

    public PersonaModel guardar(PersonaModel persona) {
        try {
            // 1. Calculamos los datos usando los métodos que ya tienes en el Repositorio
            String curpCalculado = generarCurp(persona);
            String rfcCalculado = generarRfc(persona);
            Double imcCalculado = calcularIMC(persona.peso(), persona.estatura());

            DocumentReference documentReference = firestore.collection(COLLECTION).document();
            
            // 2. Usamos HashMap porque Map.of explota si le pasas más de 10 datos
            Map<String, Object> datos = new java.util.HashMap<>();
            datos.put("nombre", persona.nombre());
            datos.put("apellidoPaterno", persona.apellidoPaterno());
            datos.put("apellidoMaterno", persona.apellidoMaterno());
            datos.put("fechaDeNacimiento", persona.fechaDeNacimiento());
            datos.put("genero", persona.genero());
            datos.put("estatusMigratorio", persona.estatusMigratorio());
            datos.put("estatura", persona.estatura());
            datos.put("peso", persona.peso());
            datos.put("telefono", persona.telefono());
            datos.put("email", persona.email());
            // Agregamos los calculados
            datos.put("curp", curpCalculado);
            datos.put("rfc", rfcCalculado);
            datos.put("imc", imcCalculado);

            ApiFuture<WriteResult> future = documentReference.set(datos);
            future.get();
            
            // 3. Retornamos el objeto con todos sus datos completos
            return new PersonaModel(
                    documentReference.getId(),
                    persona.nombre(),
                    persona.apellidoPaterno(),
                    persona.apellidoMaterno(),
                    persona.fechaDeNacimiento(),
                    persona.genero(),
                    persona.estatusMigratorio(),
                    persona.estatura(),
                    persona.peso(),
                    persona.telefono(),
                    persona.email(),
                    curpCalculado,
                    rfcCalculado,
                    imcCalculado
            );
        } catch (Exception e) {
            throw new RuntimeException("No fue posible guardar la persona por el error: " + e);
        }
    }

}
