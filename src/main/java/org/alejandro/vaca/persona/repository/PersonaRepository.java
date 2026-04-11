package org.alejandro.vaca.persona.repository;

import org.alejandro.vaca.persona.model.PersonaModel;
import java.util.List;
import java.util.Optional;

public interface PersonaRepository {
    PersonaModel guardar(PersonaModel persona);
    PersonaModel actualizarPersonaPorId(String id, PersonaModel personaActualizada);
    PersonaModel actualizarPersonaPorCurp(String curp, PersonaModel personaActualizada);
    PersonaModel actualizarPersonaPorRFC(String rfc, PersonaModel personaActualizada);
    Optional<PersonaModel> obtenerPorId(String id);
    Optional<PersonaModel> getPersonaPorCurp(String curp);
    Optional<PersonaModel> getPersonaPorRFC(String rfc);
    List<PersonaModel> getPersonasPorNombre(String nombre, String apellidoPaterno, String apellidoMaterno);
    boolean deletePersonaPorId(String id);
    void deletePersonaPorRFC(String rfc);
    void deletePersonaPorCurp(String curp);
}
