package co.edu.unicauca.bancopreguntas.service.user;

import java.util.List;

/** Se lanza cuando la contrasena no cumple con la politica de seguridad. */
public class InvalidPasswordException extends RuntimeException {

    private final List<String> errors;

    public InvalidPasswordException(List<String> errors) {
        super("La contrasena no cumple con la politica de seguridad: " + String.join(", ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
