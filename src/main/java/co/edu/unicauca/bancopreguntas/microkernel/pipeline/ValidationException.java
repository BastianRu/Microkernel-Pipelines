package co.edu.unicauca.bancopreguntas.microkernel.pipeline;

import java.util.List;

/**
 * Se lanza cuando un filtro de la tuberia detecta que la pregunta no cumple
 * alguna regla estructural de HU03. Acumula todos los errores encontrados
 * por el filtro que la lanza, para que la UI pueda mostrarlos de una vez.
 */
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
