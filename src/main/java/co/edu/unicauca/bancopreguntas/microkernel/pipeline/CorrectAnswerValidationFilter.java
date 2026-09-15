package co.edu.unicauca.bancopreguntas.microkernel.pipeline;

import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Ultimo filtro de la tuberia (salida: pregunta procesada y lista para
 * publicar): valida que exista una unica respuesta correcta y que sea
 * consistente con las opciones (RF-11).
 */
public class CorrectAnswerValidationFilter implements Filter {

    @Override
    public QuestionRequest process(QuestionRequest request) {
        List<String> errors = new ArrayList<>();
        List<String> options = request.getOptions();
        int correctIndex = request.getCorrectIndex();

        if (correctIndex < 0) {
            errors.add("RF-11: debe indicarse cual opcion es la respuesta correcta");
        } else if (options == null || correctIndex >= options.size()) {
            errors.add("RF-11: el indice de la respuesta correcta esta fuera de rango");
        } else {
            String correctOption = options.get(correctIndex);
            if (correctOption == null || correctOption.isBlank()) {
                errors.add("RF-11: la respuesta correcta no puede estar vacia");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return request;
    }
}
