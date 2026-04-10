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
import com.google.firebase.internal.NonNull;

@Repository
public class PersonaRepository {
    // private final FirebaseConfig firebaseConfig;
    private static final String COLLECTION = "personas";
    public final Firestore firestore;
    public PersonaModel personaModel;

    public PersonaRepository(Firestore fire) {
        this.firestore = fire;
    }

    public Double calcularIMC(Double peso, Double estatura) {
        if (estatura == null || estatura <= 0) return 0.0;
        double imc = peso / (estatura * estatura);
        return Math.round(imc * 100.0) / 100.0;
    }

    private Double redondear(Double valor) {
        if (valor == null) return 0.0;
        return Math.round(valor * 100.0) / 100.0;
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
                return Optional.of(new PersonaModel(
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
            } else {
                System.out.println("No se encontro la persona con el id: " + id + " .");
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("No fue posible obtener a la persona por el error: " + e);
        }

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
        validarPersona(persona);

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
       
        validarPersona(persona);
        validarDatoPorNullYVacio(persona.estatusMigratorio());
        validarDatoPorNullYVacio(persona.genero());
    
       
        String raiz = generarRaizCurpRfc(persona);
        
        String inicialGenero = String.valueOf(persona.genero().charAt(0)).toUpperCase();
    
        return raiz + inicialGenero + "C" + "CN" + "A4";
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
        validarDatoPorNullYVacio(curp);
        try {
            Query query = firestore.collection(COLLECTION).whereEqualTo("curp", curp);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

           
            if (!documents.isEmpty()) {
                DocumentSnapshot document = documents.get(0);
                
                return Optional.of(new PersonaModel(
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
            } else {
                System.out.println("No se encontro la persona con el Curp: " + curp + ".");
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("No fue posible obtener a la persona por el error: " + e);
            }

    }

    public Optional<PersonaModel> getPersonaPorRFC(String rfc) {
        validarDatoPorNullYVacio(rfc);
        try {
           
            Query query = firestore.collection(COLLECTION).whereEqualTo("rfc", rfc);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

           
            if (!documents.isEmpty()) {
                DocumentSnapshot document = documents.get(0);
                
                return Optional.of(new PersonaModel(
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
            } else {
                System.out.println("No se encontro la persona con el RFC: " + rfc + ".");
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("No fue posible obtener a la persona por el error: " + e);
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

    // Metodo Generico para Update
    @NonNull
    private PersonaModel ejecutarActualizacionBase(String identificador, PersonaModel personaActualizada, String tipoBusqueda) {
        validarDatoPorNullYVacio(tipoBusqueda);
        validarDatoPorNullYVacio(tipoBusqueda);
        validarPersona(personaActualizada);
        
        try {
            DocumentReference documentReference = firestore.collection(COLLECTION).document(identificador);
            ApiFuture<DocumentSnapshot> futureSnapshot = documentReference.get();
            DocumentSnapshot document = futureSnapshot.get();

            if (!document.exists()) {
                throw new RuntimeException("No se puede actualizar. No existe registro con el " + tipoBusqueda + ": " + identificador);
            }

         
            String curpCalculado = generarCurp(personaActualizada);
            String rfcCalculado = generarRfc(personaActualizada);
            Double imcCalculado = calcularIMC(personaActualizada.peso(), personaActualizada.estatura());

            
            Map<String, Object> datos = new java.util.HashMap<>();
            datos.put("nombre", personaActualizada.nombre());
            datos.put("apellidoPaterno", personaActualizada.apellidoPaterno());
            datos.put("apellidoMaterno", personaActualizada.apellidoMaterno());
            datos.put("fechaDeNacimiento", personaActualizada.fechaDeNacimiento());
            datos.put("genero", personaActualizada.genero());
            datos.put("estatusMigratorio", personaActualizada.estatusMigratorio());
            datos.put("estatura", personaActualizada.estatura());
            datos.put("peso", personaActualizada.peso());
            datos.put("telefono", personaActualizada.telefono());
            datos.put("email", personaActualizada.email());
            datos.put("curp", curpCalculado);
            datos.put("rfc", rfcCalculado);
            datos.put("imc", imcCalculado);

        
            ApiFuture<WriteResult> futureWrite = documentReference.set(datos);
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
                    curpCalculado,
                    rfcCalculado,
                    imcCalculado);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Falló la actualización de la persona con " + tipoBusqueda + ": " + identificador, e);
        }
    }

   




    public PersonaModel actualizarPersonaPorId(String id, PersonaModel personaActualizada) {
        validarDatoPorNullYVacio(id);
        validarPersona(personaActualizada);

        System.out.println("Los datos de la persona con el ID: " + id + "Fueron actualizados exitosamente");
        return ejecutarActualizacionBase(id, personaActualizada, "ID");
    }

    public PersonaModel actualizarPersonaPorCurp(String curp, PersonaModel personaActualizada) {
        validarDatoPorNullYVacio(curp);
        validarPersona(personaActualizada);


        PersonaModel persona = getPersonaPorCurp(curp)
                .orElseThrow(() -> new RuntimeException("No se encontró a nadie con el CURP: " + curp));
                System.out.println("Los datos de la persona con el CURP: " + curp + "Fueron actualizados exitosamente");
        return ejecutarActualizacionBase(persona.id(), personaActualizada, "CURP");
    }

    public PersonaModel actualizarPersonaPorRFC(String rfc, PersonaModel personaActualizada) {
        validarDatoPorNullYVacio(rfc);
        validarPersona(personaActualizada);
        
        PersonaModel persona = getPersonaPorRFC(rfc)
                .orElseThrow(() -> new RuntimeException("No se encontró a nadie con el RFC: " + rfc));
                System.out.println("Los datos de la persona con el RFC: " + rfc + "Fueron actualizados exitosamente");
        return ejecutarActualizacionBase(persona.id(), personaActualizada, "RFC");
    }

    
    public void deletePersonaPorRFC(String rfc) {
        validarDatoPorNullYVacio(rfc);

        PersonaModel persona = getPersonaPorRFC(rfc)
                .orElseThrow(() -> new RuntimeException("No se encontró a nadie con el RFC: " + rfc));
        deletePersonaPorId(persona.id());
    }

    public void deletePersonaPorCurp(String curp) {
        validarDatoPorNullYVacio(curp);

        PersonaModel persona = getPersonaPorCurp(curp)
                .orElseThrow(() -> new RuntimeException("No se encontró a nadie con el CURP: " + curp));
        deletePersonaPorId(persona.id());
    }
    


    public List<PersonaModel> getPersonasPorNombre(String nombre, String apellidoPaterno, String apellidoMaterno) { 
        if (nombre == null && apellidoPaterno == null && apellidoMaterno == null) { 
            try { 
                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION).get(); 
                List<QueryDocumentSnapshot> documents = future.get().getDocuments(); 
                List<PersonaModel> todas = new ArrayList<>(); 
                for (DocumentSnapshot document : documents) { 
                    todas.add(new PersonaModel( 
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
                return todas; 
            } catch (Exception e) { throw new RuntimeException(e); } 
        } 
       
        
        
        
    
        try {
           
            Query query = firestore.collection(COLLECTION)
                    .whereEqualTo("nombre", nombre)
                    .whereEqualTo("apellidoPaterno", apellidoPaterno)
                    .whereEqualTo("apellidoMaterno", apellidoMaterno);
                    
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
    
            List<PersonaModel> personasEncontradas = new ArrayList<>();
                for (DocumentSnapshot document : documents) {
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
            return personasEncontradas;
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error al consultar personas: " + e.getMessage());
        }
    }

    public PersonaModel guardar(PersonaModel persona) {
        validarPersona(persona);
        try {
          
            Double pesoRedondeado = redondear(persona.peso());
            Double estaturaRedondeada = redondear(persona.estatura());
            String curpCalculado = generarCurp(persona);
            String rfcCalculado = generarRfc(persona);
            Double imcCalculado = calcularIMC(pesoRedondeado, estaturaRedondeada);

            DocumentReference documentReference = firestore.collection(COLLECTION).document();
            
           
            Map<String, Object> datos = new java.util.HashMap<>();
            datos.put("nombre", persona.nombre());
            datos.put("apellidoPaterno", persona.apellidoPaterno());
            datos.put("apellidoMaterno", persona.apellidoMaterno());
            datos.put("fechaDeNacimiento", persona.fechaDeNacimiento());
            datos.put("genero", persona.genero());
            datos.put("estatusMigratorio", persona.estatusMigratorio());
            datos.put("estatura", estaturaRedondeada);
            datos.put("peso", pesoRedondeado);
            datos.put("telefono", persona.telefono());
            datos.put("email", persona.email());
            // Agregamos los calculados
            datos.put("curp", curpCalculado);
            datos.put("rfc", rfcCalculado);
            datos.put("imc", imcCalculado);

            ApiFuture<WriteResult> future = documentReference.set(datos);
            future.get();
            
            return new PersonaModel(
                    documentReference.getId(),
                    persona.nombre(),
                    persona.apellidoPaterno(),
                    persona.apellidoMaterno(),
                    persona.fechaDeNacimiento(),
                    persona.genero(),
                    persona.estatusMigratorio(),
                    estaturaRedondeada,
                    pesoRedondeado,
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
