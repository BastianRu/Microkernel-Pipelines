package co.edu.unicauca.bancopreguntas.domain.user;

public interface IPasswordHasher {
    String hash(String password);
    boolean matches(String password, String hashedPassword);
}
