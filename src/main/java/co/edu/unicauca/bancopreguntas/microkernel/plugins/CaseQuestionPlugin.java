package co.edu.unicauca.bancopreguntas.microkernel.plugins;

import co.edu.unicauca.bancopreguntas.microkernel.Question;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionPlugin;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.ClassificationFilter;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.ContentValidationFilter;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.CorrectAnswerValidationFilter;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.OptionsValidationFilter;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Plugin del microkernel que genera preguntas de analisis de caso: un
 * contexto extenso (el caso) seguido de una pregunta directa de seleccion
 * multiple. Reutiliza los filtros de la tuberia de HU03 para mantener las
 * mismas reglas estructurales que el resto del banco, y agrega una
 * validacion propia: el caso debe describir un escenario, no solo una frase
 * corta.
 */
public class CaseQuestionPlugin implements QuestionPlugin {

    private static final int MIN_CASE_LENGTH = 60;

    private final ContentValidationFilter contentFilter = new ContentValidationFilter();
    private final OptionsValidationFilter optionsFilter = new OptionsValidationFilter();
    private final ClassificationFilter classificationFilter = new ClassificationFilter();
    private final CorrectAnswerValidationFilter correctAnswerFilter = new CorrectAnswerValidationFilter();

    public CaseQuestionPlugin() {
    }

    @Override
    public String getName() {
        return "case-study";
    }

    @Override
    public boolean supports(String type) {
        return "CASE_STUDY".equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) {
        List<String> errors = new ArrayList<>();
        if (request.getContext() == null || request.getContext().trim().length() < MIN_CASE_LENGTH) {
            errors.add("El analisis de caso requiere un contexto de al menos " + MIN_CASE_LENGTH + " caracteres");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        contentFilter.process(request);
        optionsFilter.process(request);
        classificationFilter.process(request);
        correctAnswerFilter.process(request);

        System.out.println("Generando pregunta de analisis de caso...");

        return new Question(
                UUID.randomUUID().toString(),
                request.getTitle(),
                request.getContext() + System.lineSeparator() + request.getDirectQuestion(),
                request.getType()
        );
    }
}
