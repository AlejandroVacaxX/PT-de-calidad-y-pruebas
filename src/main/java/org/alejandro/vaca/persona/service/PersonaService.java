package org.alejandro.vaca.persona.service;

import com.google.cloud.firestore.Firestore;
import java.util.List;
import org.alejandro.vaca.persona.config.FirebaseConfig;
import org.alejandro.vaca.persona.model.PersonaModel;
import org.alejandro.vaca.persona.repository.PersonaRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonaService {
    
    private final Firestore firestore;
    private final FirebaseConfig firebaseConfig;
    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository, FirebaseConfig firebaseConfig, Firestore firestore) {
        this.personaRepository = personaRepository;
        this.firebaseConfig = firebaseConfig;
        this.firestore = firestore;
    }

    public List<PersonaModel> getPersonasPorNombre(String nombre,String apellidoP,String apellidoM) {
        return personaRepository.getPersonasPorNombre(nombre,apellidoP,apellidoM);
    }

    public PersonaModel buscarPersonaPorId(String id) {
        return personaRepository.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("El Id " + id + " No Existe"));
    }
    
    public PersonaModel buscarPersonaPorRfc(String rfc) {
        return personaRepository.getPersonaPorRFC(rfc)
                .orElseThrow(() -> new IllegalArgumentException("El RFC " + rfc + " No Existe"));
    }
    public PersonaModel buscarPersonaPorCurp(String curp){
        return personaRepository.getPersonaPorCurp(curp)
            .orElseThrow(() -> new IllegalArgumentException("El curp" + curp + "no existe"));
        
    }

    public PersonaModel registrarPersona(PersonaModel persona) {
        return personaRepository.guardar(persona);
    }


    public PersonaModel actualizarPersonaPorId(String id, PersonaModel persona) {
        return personaRepository.actualizarPersonaPorId(id, persona);
    }

    public PersonaModel actualizarPersonaPorCurp(String curp, PersonaModel persona) {
        return personaRepository.actualizarPersonaPorCurp(curp, persona);
    }

    public PersonaModel actualizarPersonaPorRfc(String rfc, PersonaModel persona) {
        return personaRepository.actualizarPersonaPorRFC(rfc, persona);
    }

    public boolean eliminarPersona(String id) {
        return personaRepository.deletePersonaPorId(id);
    }

    public void eliminarPersonaPorCurp(String curp) {
        personaRepository.deletePersonaPorCurp(curp);
    }

    public void eliminarPersonaPorRfc(String rfc) {
        personaRepository.deletePersonaPorRFC(rfc);
    }
    public void crearCurp(PersonaModel personaModel){
       personaRepository.crearCurp(personaModel);
      
    }
    public void crearRFC(PersonaModel personaModel){
        personaRepository.crearRFC(personaModel);
      
    }
    public void  calcularIMC(Double peso, Double estatura){
        personaRepository.calcularIMC(peso, estatura);  
    }
}
