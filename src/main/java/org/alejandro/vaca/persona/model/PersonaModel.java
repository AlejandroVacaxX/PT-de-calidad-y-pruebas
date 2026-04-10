package org.alejandro.vaca.persona.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PersonaModel(

        String id, // administrado pori Cloud Firestore
        @NotBlank(message = "El nombre no puede esta vacio") @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres") String nombre,
        @NotBlank(message = "El apellido paterno no puede esta vacio") @Size(min = 2, max = 100, message = "El apellido paterno debe tener entre 2 y 100 caracteres") String apellidoPaterno,
        @NotBlank(message = "El apellido materno no puede esta vacio") @Size(min = 2, max = 100, message = "El apellido materno debe tener entre 2 y 100 caracteres") String apellidoMaterno,
        @NotBlank(message = "La fecha de nacimiento no puede esta vacia") @Pattern(regexp = "^(0[1-9]|1[0-9]|2[0-9]|3[01])/(0[1-9]|1[0-2])/(19|20)\\d{2}$", message = "La fecha de nacimiento debe tener el formato dd/mm/yyyy") String fechaDeNacimiento,
        @NotBlank(message = "El genero no puede esta vacio") @Pattern(regexp = "^(femenino|masculino|binario|nobinario|sindefinicion)$", message = "El género debe ser uno de los valores permitidos") String genero,
        @NotBlank(message = "El estatus migratorio no puede esta vacio") @Pattern(regexp = "^(mexicano|ciudadano|nacionalizado|residentetemporal|residentepermanente)$", message = "El estatus migratorio debe ser uno de los valores permitidos") String estatusMigratorio,
        @Min(value = 0, message = "La estatura no puede ser negativa") @Max(value = 3, message = "La estatura no puede ser mayor a 3 metros") @Digits(integer = 1, fraction = 2, message = "La estatura debe tener máximo 1 entero y 2 decimales") Double estatura,
        @Min(value = 0, message = "El peso no puede ser negativo") @Max(value = 280, message = "El peso no puede sobrepasar los 280kg") @Digits(integer = 3, fraction = 2, message = "El peso debe tener máximo 3 entero y 2 decimales") Double peso,
        @NotBlank(message = "El teléfono no puede esta vacio") @Pattern(regexp = "^(55\\d{8})$", message = "El teléfono debe tener 10 dígitos y comenzar con 55, No debe contener Letras ni caracteres especiales.") String telefono,
        @NotBlank(message = "El email no puede esta vacio") @Email(message = "El email debe tener un formato válido ejem: 'persona@gmail.com' ") String email,
        String curp,
        String rfc,
        Double imc

) {
    public PersonaModel {
        nombre = nombre == null ? null : nombre.trim();
        apellidoPaterno = apellidoPaterno == null ? null : apellidoPaterno.trim();
        apellidoMaterno = apellidoMaterno == null ? null : apellidoMaterno.trim();
        genero = genero == null ? null : genero.trim();
        estatusMigratorio = estatusMigratorio == null ? null : estatusMigratorio.trim();
        telefono = telefono == null ? null : telefono.trim().toLowerCase();
        email = email == null ? null : email.trim();
        curp = curp == null ? null : curp.trim();
        rfc = rfc == null ? null : rfc.trim();
    }
    @Override
    public String toString() {
        return String.format("""
                
                ===================================================
                👤 PERFIL DE PERSONA
                ===================================================
                🆔 ID:              %s
                📝 Nombre Completo: %s %s %s
                📅 Nacimiento:      %s
                ⚧️  Género:          %s
                🌎 Estatus:         %s
                📏 Físico:          Estatura: %.2fm | Peso: %.2fkg
                📞 Teléfono:        %s
                📧 Email:           %s
                   Curp:            %s
                   RFC:             %s
                   IMC:             %s
                ===================================================""",
                id, nombre, apellidoPaterno, apellidoMaterno,
                fechaDeNacimiento, genero, estatusMigratorio,
                estatura, peso, telefono, email,curp,rfc,imc);
    }

}
