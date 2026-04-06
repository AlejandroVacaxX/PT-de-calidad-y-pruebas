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
                            persona.email()

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

    public void crearCurp(PersonaModel persona) {
        validarPersona(persona);
        try {
            String curpGenerada = generarCurp(persona);
            System.out.println("CURP Generada: " + curpGenerada);
        } catch (Exception e) {
            throw new RuntimeException("No fue posible generar la CURP por el error: " + e.getMessage(), e);
        }
    }

    public void crearRFC(PersonaModel persona) {
        validarPersona(persona);
        try {
            String rfcGenerado = generarRfc(persona);
            System.out.println("RFC Generado: " + rfcGenerado);
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
                            persona.email()));

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
                            persona.email()));

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
    private boolean deletePersonaGenerico(String identificador, PersonaModel personaDelete, String tipoBusqueda) {

        if (identificador == null || identificador.isBlank() || personaDelete == null) {
            throw new IllegalArgumentException(
                    "El " + tipoBusqueda + " y los datos no pueden ser nulos");
        }
        try {
            DocumentReference documentReference = firestore.collection(COLLECTION).document(identificador);
            ApiFuture<WriteResult> future = documentReference.delete();
            future.get();
            System.out.println("Los datos de la persona Fueron eliminados exitosamente");
            return true;

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error al intentar borrar los datos");
            return false;

        }
    }

    public void deletePersonaPorRFC(String rfc, PersonaModel personaActualizada) {
        deletePersonaGenerico(rfc, personaActualizada, "RFC");
    }

    public void deletePersonaPorCurp(String rfc, PersonaModel personaActualizada) {
        deletePersonaGenerico(rfc, personaActualizada, "CURP");
    }

    public void deletePersonaPorId(String rfc, PersonaModel personaActualizada) {
        deletePersonaGenerico(rfc, personaActualizada, "Id");
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
                    personaActualizada.email());

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

    public List<PersonaModel> getPersonasPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return new ArrayList<>();
        }
        try {
            Query query = firestore.collection(COLLECTION).whereEqualTo("nombre", nombre);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            List<PersonaModel> personasEncontradas = new ArrayList<>();

            for (DocumentSnapshot document : documents) {
                PersonaModel persona = document.toObject(PersonaModel.class);

                if (persona != null) {
                    personasEncontradas.add(new PersonaModel(
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
                            persona.email()));
                }
            }
            if (personasEncontradas.isEmpty()) {
                System.out.println("No se encontró ninguna persona con el nombre: " + nombre);
            }
            return personasEncontradas;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("No fue posible obtener a las personas por el nombre: " + nombre
                    + " por el error: " + e.getMessage());

        }
    }

    public PersonaModel guardar(PersonaModel persona) {
        try {
            CollectionReference collectionReference = firestore.collection(COLLECTION);
            DocumentReference documentReference = firestore.collection(COLLECTION).document();
            ApiFuture<WriteResult> future = documentReference.set(Map.of(
                    "nombre", persona.nombre(),
                    "apellidoPaterno", persona.apellidoPaterno(),
                    "apellidoMaterno", persona.apellidoMaterno(),
                    "fechaDeNacimiento", persona.fechaDeNacimiento(),
                    "genero", persona.genero(),
                    "estatusMigratorio", persona.estatusMigratorio(),
                    "estatura", persona.estatura(),
                    "peso", persona.peso(),
                    "telefono", persona.telefono(),
                    "email", persona.email()));

            future.get();
            return new PersonaModel(
                    documentReference.getId(),
                    personaModel.nombre(),
                    personaModel.apellidoPaterno(),
                    personaModel.apellidoMaterno(),
                    personaModel.fechaDeNacimiento(),
                    personaModel.genero(),
                    personaModel.estatusMigratorio(),
                    personaModel.estatura(),
                    personaModel.peso(),
                    personaModel.telefono(),
                    personaModel.email());
        } catch (Exception e) {
            throw new RuntimeException("No fue posible guardar la persona por el error: " + e);
        }
    }

}
