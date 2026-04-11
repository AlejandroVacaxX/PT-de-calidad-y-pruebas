package org.alejandro.vaca.persona.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    public List<PersonaModel> getPersonasPorNombre(String nombre, String apellidoP, String apellidoM) {
        return personaRepository.getPersonasPorNombre(nombre, apellidoP, apellidoM);
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

    private boolean existeCurpEnBaseDeDatos(String curp) {
        
        return personaRepository.getPersonaPorCurp(curp).isPresent();
    }
    private boolean existeRFCEnBaseDeDatos(String rfc) {
        
        return personaRepository.getPersonaPorRFC(rfc).isPresent();
    }

    public String generarCurpUnica(PersonaModel persona) {
        validarPersona(persona);
        validarDatoPorNullYVacio(persona.estatusMigratorio());
        validarDatoPorNullYVacio(persona.genero());
    
        String raiz = generarRaizCurpRfc(persona);
        String inicialGenero = String.valueOf(persona.genero().charAt(0)).toUpperCase();
        String sufijo = "CNA4"; 
    
       
        String curpFinal = raiz + inicialGenero + "C" + sufijo;
    
        // mientras el CURP exista en la db, sigue generando uno nuevo
        while (existeCurpEnBaseDeDatos(curpFinal)) {
            char caracterAleatorio = generarCharRandom();
         
            curpFinal = raiz + inicialGenero + String.valueOf(caracterAleatorio).toUpperCase() + sufijo;
        }
    
        return curpFinal;
    }
    public String generarRfc(PersonaModel persona) {
        if (!esMayorDe16Anios(persona.fechaDeNacimiento())) {
            throw new IllegalArgumentException("La persona debe ser mayor de 16 años para generar el RFC");
        }
        return generarRaizCurpRfc(persona);
    }
    public String generarRFCUnica(PersonaModel persona) {
        validarPersona(persona);
        validarDatoPorNullYVacio(persona.estatusMigratorio());
        validarDatoPorNullYVacio(persona.genero());
        if (!esMayorDe16Anios(persona.fechaDeNacimiento())) {
            throw new IllegalArgumentException("La persona debe ser mayor de 16 años para generar el RFC");
        }
    
       
        String raiz = generarRaizCurpRfc(persona);
        String homoclave = String.valueOf(generarCharRandom()) + generarCharRandom() + generarCharRandom();
        String rfcFinal = raiz + homoclave.toUpperCase();

        while (existeRFCEnBaseDeDatos(rfcFinal)) {
            homoclave = String.valueOf(generarCharRandom()) + generarCharRandom() + generarCharRandom();
            rfcFinal = raiz + homoclave.toUpperCase();
        }
        return rfcFinal;
    }
    public PersonaModel registrarPersona(PersonaModel persona) {
        validarPersona(persona);
    
        Double pesoRedondeado = redondear(persona.peso());
        Double estaturaRedondeada = redondear(persona.estatura());

        String curpValidado = generarCurpUnica(persona); 
        String rfcCalculado = generarRFCUnica(persona);
        Double imcCalculado = calcularIMC(pesoRedondeado, estaturaRedondeada);
        

        PersonaModel personaParaGuardar = new PersonaModel(
            null,
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
            curpValidado, 
            rfcCalculado,
            imcCalculado
        );
        
     
        return personaRepository.guardar(personaParaGuardar);
    }
    
   

    public PersonaModel actualizarPersonaPorId(String id, PersonaModel persona) {
        validarPersona(persona);
        
        Double pesoRedondeado = redondear(persona.peso());
        Double estaturaRedondeada = redondear(persona.estatura());
        String curpCalculado = generarCurpUnica(persona);
        String rfcCalculado = generarRFCUnica(persona);
        Double imcCalculado = calcularIMC(pesoRedondeado, estaturaRedondeada);
        
        PersonaModel personaParaActualizar = new PersonaModel(
            id,
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
        
        return personaRepository.actualizarPersonaPorId(id, personaParaActualizar);
    }

    public PersonaModel actualizarPersonaPorCurp(String curp, PersonaModel persona) {
        PersonaModel existente = buscarPersonaPorCurp(curp);
        return actualizarPersonaPorId(existente.id(), persona);
    }

    public PersonaModel actualizarPersonaPorRfc(String rfc, PersonaModel persona) {
        PersonaModel existente = buscarPersonaPorRfc(rfc);
        return actualizarPersonaPorId(existente.id(), persona);
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
    
    public Double calcularIMC(Double peso, Double estatura) {
        if (estatura == null || estatura <= 0) return 0.0;
        double imc = peso / (estatura * estatura);
        return Math.round(imc * 100.0) / 100.0;
    }

    private Double redondear(Double valor) {
        if (valor == null) return 0.0;
        return Math.round(valor * 100.0) / 100.0;
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

   
    private char generarCharRandom(){
        char randomChar = (char) ('a' + Math.random() * ('z' - 'a' + 1));
        return randomChar;

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
}
