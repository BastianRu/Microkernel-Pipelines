package co.edu.unicauca.bancopreguntas.domain.user;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PasswordPolicyTest {

    @Test
    void deberiaAceptarContrasenaValida() {
        assertTrue(PasswordPolicy.isValid("Clave123!"));
    }

    @Test
    void deberiaRechazarContrasenaCorta() {
        List<String> errors = PasswordPolicy.validate("A1!");
        assertTrue(errors.stream().anyMatch(e -> e.contains("6 caracteres")));
    }

    @Test
    void deberiaRechazarContrasenaSinDigito() {
        List<String> errors = PasswordPolicy.validate("Clavee!");
        assertTrue(errors.stream().anyMatch(e -> e.contains("digito")));
    }

    @Test
    void deberiaRechazarContrasenaSinMayuscula() {
        List<String> errors = PasswordPolicy.validate("clave123!");
        assertTrue(errors.stream().anyMatch(e -> e.contains("mayuscula")));
    }

    @Test
    void deberiaRechazarContrasenaSinCaracterEspecial() {
        List<String> errors = PasswordPolicy.validate("Clave123");
        assertTrue(errors.stream().anyMatch(e -> e.contains("especial")));
    }

    @Test
    void deberiaRechazarContrasenaNula() {
        List<String> errors = PasswordPolicy.validate(null);
        assertFalse(errors.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abcdef1!", "Xyz987#Z", "Password1$"})
    void variasContrasenasValidasDeberianPasar(String password) {
        assertTrue(PasswordPolicy.isValid(password));
    }
}
