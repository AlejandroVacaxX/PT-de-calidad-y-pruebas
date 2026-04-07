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
                    
                    PersonaModel personaTemporal = new PersonaModel(null, nombre, apellidoPaterno, apellidoMaterno, fechaDeNacimiento, genero, estatusMigratorio, estatura, peso, telefono, email, null,null,null);
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
            } 
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
            try{
            System.out.println("==========================================");
            System.out.println("🚀 INICIANDO BATERÍA DE PRUEBAS CRUD");
            System.out.println("==========================================\n");
            String idGenerado = "142DvK7R1q6DKMvDTIAk";
            

            // ---------------------------------------------------------
            // 1. PRUEBA DE REGISTRO
            // ---------------------------------------------------------
            System.out.println("--- 1. PROBANDO REGISTRO ---");
            try {
                PersonaModel personaNueva = new PersonaModel(
                        null, "Sujeto", "De", "Pruebas", "01/01/2000", "No Binario", 
                        "Mexicano", 1.75, 70.0, "5511223344", "prueba@test.com","piva000101","piva000101",19.0
                );
                PersonaModel registrada = personaService.registrarPersona(personaNueva);
                String idGeneradoviejo = registrada.id(); // Guardamos el ID para usarlo después

                System.out.println("✅ Registro exitoso. ID generado: " + idGenerado);
            } catch (Exception e) {
                System.out.println("❌ Error al registrar: " + e.getMessage());
            }

            // ---------------------------------------------------------
            // 2. PRUEBAS DE BÚSQUEDA
            // ---------------------------------------------------------
            System.out.println("\n--- 2. PROBANDO BÚSQUEDAS ---");
            try {
                System.out.println("Buscando por ID: " + idGenerado);
                PersonaModel encontradaPorId = personaService.buscarPersonaPorId(idGenerado);
                System.out.println("✅ Persona encontrada exitosamente.");
            } catch (Exception e) {
                System.out.println("❌ Error en búsqueda por ID: " + e.getMessage());
            }

            try {
                System.out.println("Buscando por RFC (Document ID)...");
                // Nota: Esto fallará si no tienes un documento cuyo ID sea exactamente "RFC_PRUEBA"
                PersonaModel encontradaPorRfc = personaService.buscarPersonaPorRfc("RFC_PRUEBA");
                System.out.println("✅ Encontrada por RFC.");
            } catch (Exception e) {
                System.out.println("⚠️ Búsqueda por RFC arrojó: " + e.getMessage());
            }

            // ---------------------------------------------------------
            // 3. PRUEBAS DE ACTUALIZACIÓN
            // ---------------------------------------------------------
            System.out.println("\n--- 3. PROBANDO ACTUALIZACIONES ---");
            try {
                System.out.println("Actualizando datos de la persona con ID: " + idGenerado);
                PersonaModel datosNuevos = new PersonaModel(
                        idGenerado, "alejandro", "pintor", "vaca", "01/01/2000", "Masculino", 
                        "Mexicano", 1.80, 75.0, "5511223344", "editado@test.com","piva000101","piva000101",19.0
                );
                
                PersonaModel actualizada = personaService.actualizarPersonaPorId(idGenerado, datosNuevos);
                System.out.println("✅ Actualización por ID exitosa. Nuevo nombre: " + actualizada.apellidoMaterno());
                
                // Prueba del método abstracto que usa el ID interno
                personaService.actualizarPersona(datosNuevos);
                System.out.println("✅ Actualización general exitosa.");

            } catch (Exception e) {
                System.out.println("❌ Error al actualizar: " + e.getMessage());
            }

            // ---------------------------------------------------------
            // 4. PRUEBAS DE ELIMINACIÓN
            // ---------------------------------------------------------
            System.out.println("\n--- 4. PROBANDO ELIMINACIONES ---");
            try {
                System.out.println("Eliminando persona por ID: " + idGenerado);
                boolean eliminado = personaService.eliminarPersona(idGenerado);
                System.out.println(eliminado ? "✅ Eliminación por ID exitosa." : "❌ No se pudo eliminar la persona.");
            } catch (Exception e) {
                System.out.println("❌ Error al eliminar por ID: " + e.getMessage());
            }

            try {
                System.out.println("Eliminando por CURP (Document ID)...");
                personaService.eliminarPersonaPorCurp("CURP_PRUEBA");
                System.out.println("✅ Comando de eliminar por CURP enviado.");
                
                System.out.println("Eliminando por RFC (Document ID)...");
                personaService.eliminarPersonaPorRfc("RFC_PRUEBA");
                System.out.println("✅ Comando de eliminar por RFC enviado.");
            } catch (Exception e) {
                System.out.println("⚠️ Error en métodos de borrado por CURP/RFC: " + e.getMessage());
            }

            System.out.println("\n==========================================");
            System.out.println("🏁 BATERÍA DE PRUEBAS FINALIZADA");
            System.out.println("==========================================");
        }
        catch(Exception e){
            System.out.println("Sepa la verga pero hubo error");
        } 
        };
    }
}