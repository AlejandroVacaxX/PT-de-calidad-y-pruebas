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
        System.out.println("http://localhost:8080/personamanager/personas");
    }
}