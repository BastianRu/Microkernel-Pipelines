package co.edu.unicauca.bancopreguntas.access.user;

import co.edu.unicauca.bancopreguntas.domain.user.IPasswordHasher;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * Implementacion de IPasswordHasher usando la libreria Argon2 (RNF-08:
 * hashing seguro de contrasenas).
 */
public class Argon2PasswordHasher implements IPasswordHasher {

    private final Argon2 argon2 = Argon2Factory.create();

    private static final int ITERATIONS = 2;
    private static final int MEMORY = 65536;
    private static final int PARALLELISM = 1;

    @Override
    public String hash(String rawPassword) {
        char[] passwordChars = rawPassword.toCharArray();
        try {
            return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        char[] passwordChars = rawPassword.toCharArray();
        try {
            return argon2.verify(hashedPassword, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }
}
