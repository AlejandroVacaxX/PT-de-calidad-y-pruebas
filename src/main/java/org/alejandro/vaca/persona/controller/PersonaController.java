package org.alejandro.vaca.persona.controller;

import org.alejandro.vaca.persona.service.PersonaService;
import org.alejandro.vaca.persona.model.PersonaModel;
import org.alejandro.vaca.persona.repository.PersonaRepository;
import java.util.List;

     import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
    
    @RestController
    @RequestMapping("personas/personas-api")
    public class PersonaController {
    private final PersonaService personaService;
    private final PersonaRepository personaRepository;
    public PersonaController(PersonaService personaService, PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
        this.personaService = personaService;
    }
    // Todos los metodos GET

    @GetMapping
    public List<PersonaModel> obtenerTodos() {
        return personaService.obtenerTodos();
    }

    @GetMapping("/nombreTodos/{nombre}")
        public List<PersonaModel> getPersonasPorNombre(
            @RequestParam(required = false) String nombre, 
            @RequestParam(required = false) String apellidoP, 
            @RequestParam(required = false) String apellidoM
        ) {
            return personaService.getPersonasPorNombre(nombre, apellidoP, apellidoM);
        }
    @GetMapping("/id/{id}")
    public PersonaModel obtenerPersonaPorId(@PathVariable String id) {
        return personaService.buscarPersonaPorId(id);
    }
    @GetMapping("/curp/{curp}")
    public PersonaModel obtenerPersonaPorCurp(@PathVariable String curp) {
        return personaRepository.getPersonaPorCurp(curp).orElseThrow(() -> new IllegalArgumentException("El curp" + curp + " No Existe"));
    }
    @GetMapping("/rfc/{rfc}")
    public PersonaModel obtenerPersonaPorRfc(@PathVariable String rfc) {
        return personaRepository.getPersonaPorRFC(rfc).orElseThrow(() -> new IllegalArgumentException("El rfc" + rfc + " No Existe"));
    }
    // El unico metodo POST
    @PostMapping
    public PersonaModel registrarPersona(@RequestBody PersonaModel persona) {
        return personaService.registrarPersona(persona);
    }
    // Todos los metodos PUT para actualizar los datos de la persona
    @PutMapping("/id/{id}")
    public PersonaModel actualizarPersona(@PathVariable String id, @RequestBody PersonaModel persona) {
        return personaService.actualizarPersonaPorId(id, persona);
    }
    @PutMapping("/curp/{curp}")
    public PersonaModel actualizarPersonaPorCurp(@PathVariable String curp, @RequestBody PersonaModel persona) {
        return personaService.actualizarPersonaPorCurp(curp, persona);
    }
    @PutMapping("/rfc/{rfc}")
    public PersonaModel actualizarPersonaPorRfc(@PathVariable String rfc, @RequestBody PersonaModel persona) {
        return personaService.actualizarPersonaPorRfc(rfc, persona);
    }
    // Todos los metodos DELETE para eliminar los datos de la persona
    @DeleteMapping("/id/{id}")
    public boolean eliminarPersona(@PathVariable String id) {
        return personaService.eliminarPersona(id);
    }
    @DeleteMapping("/curp/{curp}")
    public void eliminarPersonaPorCurp(@PathVariable String curp) {
        personaRepository.deletePersonaPorCurp(curp);
    }
    @DeleteMapping("/rfc/{rfc}")
    public void eliminarPersonaPorRfc(@PathVariable String rfc) {
        personaRepository.deletePersonaPorRFC(rfc);
    }
}