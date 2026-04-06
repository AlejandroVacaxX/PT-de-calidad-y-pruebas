package org.alejandro.vaca.persona.service;

import java.util.List;

import org.alejandro.vaca.persona.model.PersonaModel;
import org.alejandro.vaca.persona.repository.PersonaRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonaService {
    private final PersonaRepository personaRepository;
    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }
    public List<PersonaModel> listarPersonas() {
        return personaRepository.obtenerTodos();
    }
    public PersonaModel buscarPersonaPorId(String id) {
        return personaRepository.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("El Id" + id + " No Existe"));
    }
    public PersonaModel buscarPersonaPorCurp(String curp) {
        return personaRepository.obtenerPorNombre(curp)
                .orElseThrow(() -> new IllegalArgumentException("El curp" + curp + " No Existe"));
    }
    public PersonaModel buscarPersonaPorRfc(String rfc) {
        return personaRepository.obtenerPorRfc(rfc)
                .orElseThrow(() -> new IllegalArgumentException("El rfc" + rfc + " No Existe"));
    }
    public PersonaModel registrarPersona(PersonaModel persona) {
        return personaRepository.guardar(persona);
    }
    public PersonaModel actualizarPersona(PersonaModel persona) {
        return personaRepository.actualizarPersonaPorId(persona.id(), persona);
    }
    public PersonaModel actualizarPersonaPorCurp(String curp, PersonaModel persona) {
        return personaRepository.actualizarPersonaPorCurp(curp, persona);
    }
    public PersonaModel actualizarPersonaPorRfc(String rfc, PersonaModel persona) {
        return personaRepository.actualizarPersonaPorRfc(rfc, persona);
    }
    public boolean eliminarPersona(String id) {
        return personaRepository.deletePersonaPorId(id);
    }
    public boolean eliminarPersonaPorCurp(String curp) {
        return personaRepository.deletePersonaPorCurp(curp, null);
    }
    public boolean eliminarPersonaPorRfc(String rfc) {
        return personaRepository.deletePersonaPorRfc(rfc, null);
    }
    
}