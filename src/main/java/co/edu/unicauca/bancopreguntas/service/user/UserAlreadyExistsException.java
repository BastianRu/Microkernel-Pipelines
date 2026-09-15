package co.edu.unicauca.bancopreguntas.service.user;

/** Se lanza cuando se intenta registrar un usuario con un login ya existente. */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
