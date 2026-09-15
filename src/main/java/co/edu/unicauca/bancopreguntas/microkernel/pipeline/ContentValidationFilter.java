package co.edu.unicauca.bancopreguntas.microkernel.pipeline;

import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Primer filtro de la tuberia: valida el contenido basico de la pregunta.
 *
 * <ul>
 *     <li>RF-08: toda pregunta debe tener un contexto.</li>
 *     <li>RF-09: debe existir una unica pregunta directa (no vacia).</li>
 * </ul>
 */
public class ContentValidationFilter implements Filter {

    private static final int MIN_CONTEXT_LENGTH = 20;
    private static final int MIN_QUESTION_LENGTH = 10;

    @Override
    public QuestionRequest process(QuestionRequest request) {
        List<String> errors = new ArrayList<>();

        if (isBlank(request.getContext())) {
            errors.add("RF-08: la pregunta debe tener un contexto");
        } else if (request.getContext().trim().length() < MIN_CONTEXT_LENGTH) {
            errors.add("RF-08: el contexto debe tener al menos " + MIN_CONTEXT_LENGTH + " caracteres");
        }

        if (isBlank(request.getDirectQuestion())) {
            errors.add("RF-09: debe existir una unica pregunta directa");
        } else {
            String directQuestion = request.getDirectQuestion().trim();
            if (directQuestion.length() < MIN_QUESTION_LENGTH) {
                errors.add("RF-09: la pregunta directa es demasiado corta");
            }
            if (!directQuestion.endsWith("?")) {
                errors.add("RF-09: la pregunta directa debe tener formato de pregunta (terminar en '?')");
            }
            long questionMarks = directQuestion.chars().filter(c -> c == '?').count();
            if (questionMarks > 1) {
                errors.add("RF-09: debe existir una unica pregunta directa, no varias");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return request;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
