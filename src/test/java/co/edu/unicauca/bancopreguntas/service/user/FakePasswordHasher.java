package co.edu.unicauca.bancopreguntas.service.user;

import co.edu.unicauca.bancopreguntas.domain.user.IPasswordHasher;

/**
 * Implementacion FALSA (no segura) de IPasswordHasher, usada solo en
 * pruebas para no depender del algoritmo real (Argon2 es deliberadamente
 * lento) y para aislar las pruebas de UserService de la capa de acceso.
 *
 * NUNCA debe usarse en produccion; Main sigue usando Argon2PasswordHasher.
 */
public class FakePasswordHasher implements IPasswordHasher {

    private static final String PREFIX = "hashed:";

    @Override
    public String hash(String rawPassword) {
        return PREFIX + rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return (PREFIX + rawPassword).equals(hashedPassword);
    }
}
