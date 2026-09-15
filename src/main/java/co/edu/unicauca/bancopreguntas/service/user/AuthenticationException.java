package co.edu.unicauca.bancopreguntas.service.user;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
