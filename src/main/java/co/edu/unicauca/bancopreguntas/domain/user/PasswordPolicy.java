package co.edu.unicauca.bancopreguntas.domain.user;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Politica de contrasenas de la aplicacion (RNF-08: hashing seguro implica
 * ademas exigir contrasenas minimamente robustas antes de hashearlas).
 */
public final class PasswordPolicy {

    private static final int MIN_LENGTH = 6;
    private static final Pattern HAS_DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern HAS_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern HAS_SPECIAL_CHAR = Pattern.compile(".*[^a-zA-Z0-9].*");

    private PasswordPolicy() {
    }

    public static List<String> validate(String rawPassword) {
        List<String> errors = new ArrayList<>();

        if (rawPassword == null || rawPassword.isEmpty()) {
            errors.add("La contrasena no puede estar vacia");
            return errors;
        }
        if (rawPassword.length() < MIN_LENGTH) {
            errors.add("La contrasena debe tener al menos " + MIN_LENGTH + " caracteres");
        }
        if (!HAS_DIGIT.matcher(rawPassword).matches()) {
            errors.add("La contrasena debe tener al menos un digito");
        }
        if (!HAS_UPPERCASE.matcher(rawPassword).matches()) {
            errors.add("La contrasena debe tener al menos una mayuscula");
        }
        if (!HAS_SPECIAL_CHAR.matcher(rawPassword).matches()) {
            errors.add("La contrasena debe tener al menos un caracter especial");
        }
        return errors;
    }

    public static boolean isValid(String rawPassword) {
        return validate(rawPassword).isEmpty();
    }
}
