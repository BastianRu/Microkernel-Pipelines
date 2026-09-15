package co.edu.unicauca.bancopreguntas.microkernel.pipeline;

import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Tercer filtro de la tuberia: clasifica la pregunta asignando/validando su
 * competencia, tema y nivel de dificultad (RF-05: toda pregunta del banco
 * debe declarar competencia, tema, subtema y nivel de dificultad).
 */
public class ClassificationFilter implements Filter {

    private static final Set<String> VALID_LEVELS = Set.of("BAJO", "MEDIO", "ALTO");

    @Override
    public QuestionRequest process(QuestionRequest request) {
        List<String> errors = new ArrayList<>();

        if (isBlank(request.getCompetencia())) {
            errors.add("RF-05: la pregunta debe tener una competencia asignada");
        }
        if (isBlank(request.getTema())) {
            errors.add("RF-05: la pregunta debe tener un tema asignado");
        }
        if (isBlank(request.getNivelDificultad())) {
            errors.add("RF-05: la pregunta debe tener un nivel de dificultad asignado");
        } else if (!VALID_LEVELS.contains(request.getNivelDificultad().trim().toUpperCase())) {
            errors.add("RF-05: el nivel de dificultad debe ser uno de " + VALID_LEVELS);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // Establece la categoria por defecto cuando no se indico subtema.
        if (isBlank(request.getSubtema())) {
            request.setSubtema(request.getTema());
        }
        return request;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
