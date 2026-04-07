package org.alejandro.vaca.persona;

import java.util.Scanner;
import java.util.Set;


import org.alejandro.vaca.persona.model.PersonaModel;
import org.alejandro.vaca.persona.service.PersonaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@SpringBootApplication
public class Persona{
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Persona.class, args);
        System.out.println("Aplicacion Persona Inicializada Correctamente");
        System.out.println("Endpoint Disponible En:");
        System.out.println("http://localhost:8080/personas/personas-api");
        System.out.println("El sistema se apagara en 10 segundos");
        Thread.sleep(10000);
        System.out.println("Cerrando El Servicio");
        System.exit(0);
    }
    @Bean
    //@Profile("!test")
    //@ConditionalOnProperty(name = "app.cargar-datos", havingValue = "true", matchIfMissing = true)
    CommandLineRunner init(PersonaService personaService, Validator validator) {
        return args -> {
            System.out.println("Aplicacion Persona Inicializada Correctamente");
            Scanner scanner = new Scanner(System.in);
            PersonaModel personaValida = null;
            /*
            while (personaValida == null) {
                try {
                    System.out.println("Ingresa El Nombre De La Persona:");
                    String nombre = scanner.nextLine();
                    System.out.println("Ingresa El Apellido Paterno De La Persona:");
                    String apellidoPaterno = scanner.nextLine();
                    System.out.println("Ingresa El Apellido Materno De La Persona:");
                    String apellidoMaterno = scanner.nextLine();
                    System.out.println("Ingresa La Fecha De Nacimiento De La Persona:");
                    String fechaDeNacimiento = scanner.nextLine();
                    System.out.println("Ingresa El Genero De La Persona:");
                    String genero = scanner.nextLine();
                    System.out.println("Ingresa El Estatus Migratorio De La Persona:");
                    String estatusMigratorio = scanner.nextLine();
                    System.out.println("Ingresa La Estatura De La Persona:");
                    Double estatura = Double.parseDouble(scanner.nextLine());
                    System.out.println("Ingresa El Peso De La Persona:");
                    Double peso = Double.parseDouble(scanner.nextLine());
                    System.out.println("Ingresa El Telefono De La Persona:");
                    String telefono = scanner.nextLine();
                    System.out.println("Ingresa El Email De La Persona:");
                    String email = scanner.nextLine();
                    PersonaModel personaTemporal = new PersonaModel(null, nombre, apellidoPaterno, apellidoMaterno, fechaDeNacimiento, genero, estatusMigratorio, estatura, peso, telefono, email);
                    Set<ConstraintViolation<PersonaModel>> constraintViolations = validator.validate(personaTemporal);
                    if (!constraintViolations.isEmpty()) {
                        System.out.println("Se Encontraron Errores En Los Datos Ingresados:");
                        for (ConstraintViolation<PersonaModel> constraintViolation : constraintViolations) {
                            System.out.println(" - " + constraintViolation.getPropertyPath() + ":"
                                    + constraintViolation.getMessage());
                        }
                        System.out.println("Vuelve A Intentarlo\n");
                    } else {
                        personaValida = personaTemporal;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("La Fecha De Nacimiento Debe Ser Un Valor Numerico");
                } catch (Exception e) {
                    System.out.println("No Se pudo regitrar a la persona por el error" + e + ". Vuelva A Intentarlo");
                }
            } 
            try{
                personaService.registrarPersona(personaValida);
                System.out.println("Persona Registrada Correctamente");
            } catch (Exception e) {
                System.out.println("No Se Pudo Registrar La Persona Por El Error " + e + ". Vuelva A Intentarlo");
            } */
                try {
                   
                    String nombre = "Alejandro".toLowerCase();
                    String apellidoP = "Pintor".toLowerCase();
                    String apellidoM = "Vaca".toLowerCase();
                  
                    System.out.println("Buscando a: " + nombre + " " + apellidoP + " " + apellidoM + "...");
                    
                   
                    personaService.getPersonasPorNombre(nombre, apellidoP, apellidoM)
                                  .forEach(System.out::println);
    
                } catch(Exception e) {
                    System.out.println("Ocurrió un error en la consulta: " + e.getMessage());
                }
           
        };
    }
}