package org.alejandro.vaca.persona.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.alejandro.vaca.persona.model.PersonaModel;
import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

@Repository
public class FirestorePersonaRepository implements PersonaRepository {
    private static final String COLLECTION = "personas";
    private final Firestore firestore;

    public FirestorePersonaRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Optional<PersonaModel> obtenerPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        try {
            DocumentReference documentReference = firestore.collection(COLLECTION).document(id);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                return Optional.of(mapDocumentToPersona(document));
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error al obtener persona por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<PersonaModel> getPersonaPorCurp(String curp) {
        try {
            Query query = firestore.collection(COLLECTION).whereEqualTo("curp", curp);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            if (!documents.isEmpty()) {
                return Optional.of(mapDocumentToPersona(documents.get(0)));
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error al obtener persona por CURP: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<PersonaModel> getPersonaPorRFC(String rfc) {
        try {
            Query query = firestore.collection(COLLECTION).whereEqualTo("rfc", rfc);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            if (!documents.isEmpty()) {
                return Optional.of(mapDocumentToPersona(documents.get(0)));
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error al obtener persona por RFC: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deletePersonaPorId(String id) {
        try {
            DocumentReference documentReference = firestore.collection(COLLECTION).document(id);
            ApiFuture<WriteResult> future = documentReference.delete();
            future.get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    @Override
    public PersonaModel actualizarPersonaPorId(String id, PersonaModel personaActualizada) {
        try {
            DocumentReference documentReference = firestore.collection(COLLECTION).document(id);
            
            Map<String, Object> datos = mapPersonaToMap(personaActualizada);
            ApiFuture<WriteResult> futureWrite = documentReference.set(datos);
            futureWrite.get();

            return new PersonaModel(
                    id,
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
                    personaActualizada.curp(),
                    personaActualizada.rfc(),
                    personaActualizada.imc());

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error al actualizar persona: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PersonaModel> getPersonasPorNombre(String nombre, String apellidoPaterno, String apellidoMaterno) {
        try {
            Query query = firestore.collection(COLLECTION);
        
            if (nombre != null && !nombre.isEmpty()) {
                query = query.whereEqualTo("nombre", nombre);
            }
            if (apellidoPaterno != null && !apellidoPaterno.isEmpty()) {
                query = query.whereEqualTo("apellidoPaterno", apellidoPaterno);
            }
            if (apellidoMaterno != null && !apellidoMaterno.isEmpty()) {
                query = query.whereEqualTo("apellidoMaterno", apellidoMaterno);
            }
        
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<PersonaModel> personasEncontradas = new ArrayList<>();
        
            for (DocumentSnapshot document : documents) {
                personasEncontradas.add(mapDocumentToPersona(document));
            }
            return personasEncontradas;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error al consultar personas: " + e.getMessage(), e);
        }
    }

    @Override
    public PersonaModel guardar(PersonaModel persona) {
        try {
            DocumentReference documentReference = firestore.collection(COLLECTION).document();
            Map<String, Object> datos = mapPersonaToMap(persona);
            
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
                    persona.estatura(),
                    persona.peso(),
                    persona.telefono(),
                    persona.email(),
                    persona.curp(),
                    persona.rfc(),
                    persona.imc()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar persona: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletePersonaPorRFC(String rfc) {
        getPersonaPorRFC(rfc).ifPresent(p -> deletePersonaPorId(p.id()));
    }

    @Override
    public void deletePersonaPorCurp(String curp) {
        getPersonaPorCurp(curp).ifPresent(p -> deletePersonaPorId(p.id()));
    }

    @Override
    public PersonaModel actualizarPersonaPorCurp(String curp, PersonaModel personaActualizada) {
        PersonaModel existente = getPersonaPorCurp(curp)
                .orElseThrow(() -> new RuntimeException("No se encontró persona con CURP: " + curp));
        return actualizarPersonaPorId(existente.id(), personaActualizada);
    }

    @Override
    public PersonaModel actualizarPersonaPorRFC(String rfc, PersonaModel personaActualizada) {
        PersonaModel existente = getPersonaPorRFC(rfc)
                .orElseThrow(() -> new RuntimeException("No se encontró persona con RFC: " + rfc));
        return actualizarPersonaPorId(existente.id(), personaActualizada);
    }

    private PersonaModel mapDocumentToPersona(DocumentSnapshot document) {
        return new PersonaModel(
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
        );
    }

    private Map<String, Object> mapPersonaToMap(PersonaModel persona) {
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
        datos.put("curp", persona.curp());
        datos.put("rfc", persona.rfc());
        datos.put("imc", persona.imc());
        return datos;
    }
}
