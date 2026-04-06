package org.alejandro.vaca.persona.model;

public record PersonaModel() {
    /*
     * • id (String) y será administrado por la Cloud Firestore.
     * • nombre (String).
     * • apellidos (String).
     * • fechaDeNacimiento (String) con el formato "dd/mm/yyyy".
     * • genero (String) limitado a "Femenino", "Masculino", "Binario",
     * "No Binario",
     * "Sin Definición".
     * • estatusMigratorio (String) limitado a "Mexicano", "Ciudadano",
     * "Nacionalizado", "Residente Temporal" y "Residente Permanente".
     * • estatura (double o Double) y se expresará en kilos.
     * • peso (double o Double) y se expresará en metros.
     * • telefono (String) y con una extensión obligatoria de 10 dígitos y con un
     * inicio de
     * "55").
     * • email (String).
     */
}
