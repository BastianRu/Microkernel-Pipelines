package co.edu.unicauca.bancopreguntas.microkernel.plugins;

import co.edu.unicauca.bancopreguntas.microkernel.Question;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionPlugin;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.ContentValidationFilter;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.CorrectAnswerValidationFilter;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.OptionsValidationFilter;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Plugin del microkernel que crea preguntas apoyadas en un recurso
 * multimedia (imagen, audio o video). Valida el contenido y las opciones
 * con los mismos filtros de HU03 y ademas exige una URL o ruta de recurso
 * valida.
 */
public class MultimediaQuestionPlugin implements QuestionPlugin {

    private final ContentValidationFilter contentFilter = new ContentValidationFilter();
    private final OptionsValidationFilter optionsFilter = new OptionsValidationFilter();
    private final CorrectAnswerValidationFilter correctAnswerFilter = new CorrectAnswerValidationFilter();

    public MultimediaQuestionPlugin() {
    }

    @Override
    public String getName() {
        return "multimedia";
    }

    @Override
    public boolean supports(String type) {
        return "MULTIMEDIA".equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) {
        List<String> errors = new ArrayList<>();
        if (request.getMediaUrl() == null || request.getMediaUrl().isBlank()) {
            errors.add("La pregunta multimedia requiere una URL o ruta del recurso (imagen, audio o video)");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        contentFilter.process(request);
        optionsFilter.process(request);
        correctAnswerFilter.process(request);

        System.out.println("Generando pregunta multimedia con recurso: " + request.getMediaUrl());

        String content = request.getContext() + System.lineSeparator()
                + request.getDirectQuestion() + System.lineSeparator()
                + "Recurso multimedia: " + request.getMediaUrl();

        return new Question(
                UUID.randomUUID().toString(),
                request.getTitle(),
                content,
                request.getType()
        );
    }
}
