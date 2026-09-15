package co.edu.unicauca.bancopreguntas.microkernel.pipeline;

import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Segundo filtro de la tuberia: valida la cantidad y calidad de las
 * opciones de respuesta.
 *
 * <ul>
 *     <li>RF-10: deben existir exactamente cuatro distractores (es decir,
 *     cinco opciones en total: un acierto + cuatro distractores).</li>
 *     <li>RF-12: se prohiben expresiones como "todas las anteriores" o
 *     "ninguna de las anteriores".</li>
 *     <li>RF-13: los distractores deben respetar una longitud minima y no
 *     repetirse ni estar vacios.</li>
 * </ul>
 */
public class OptionsValidationFilter implements Filter {

    private static final int REQUIRED_DISTRACTORS = 4;
    private static final int REQUIRED_TOTAL_OPTIONS = REQUIRED_DISTRACTORS + 1;
    private static final int MIN_OPTION_LENGTH = 3;
    private static final Set<String> FORBIDDEN_PHRASES = Set.of(
            "todas las anteriores",
            "ninguna de las anteriores"
    );

    @Override
    public QuestionRequest process(QuestionRequest request) {
        List<String> errors = new ArrayList<>();
        List<String> options = request.getOptions();

        if (options == null || options.size() != REQUIRED_TOTAL_OPTIONS) {
            int distractors = options == null ? 0 : Math.max(0, options.size() - 1);
            errors.add("RF-10: se requieren exactamente " + REQUIRED_DISTRACTORS
                    + " distractores (encontrados: " + distractors + ")");
            throw new ValidationException(errors);
        }

        Set<String> normalized = new HashSet<>();
        for (int i = 0; i < options.size(); i++) {
            String option = options.get(i);
            if (option == null || option.isBlank()) {
                errors.add("RF-13: la opcion " + (i + 1) + " no puede estar vacia");
                continue;
            }
            String trimmed = option.trim();
            if (trimmed.length() < MIN_OPTION_LENGTH) {
                errors.add("RF-13: la opcion \"" + trimmed + "\" es demasiado corta");
            }
            if (!Character.isUpperCase(trimmed.codePointAt(0))) {
                errors.add("RF-13: la opcion \"" + trimmed + "\" debe iniciar con mayuscula");
            }
            String lower = trimmed.toLowerCase(Locale.ROOT);
            if (FORBIDDEN_PHRASES.contains(lower)) {
                errors.add("RF-12: no se permite la expresion \"" + trimmed + "\"");
            }
            if (!normalized.add(lower)) {
                errors.add("RF-13: la opcion \"" + trimmed + "\" esta duplicada");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return request;
    }
}
