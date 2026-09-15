package co.edu.unicauca.bancopreguntas.microkernel.pipeline;

import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CorrectAnswerValidationFilterTest {

    private final CorrectAnswerValidationFilter filter = new CorrectAnswerValidationFilter();

    @Test
    void deberiaAceptarRespuestaCorrectaValida() {
        QuestionRequest request = new QuestionRequest();
        request.setOptions(List.of("Correcta", "D1", "D2", "D3", "D4"));
        request.setCorrectIndex(0);
        assertDoesNotThrow(() -> filter.process(request));
    }

    @Test
    void deberiaRechazarIndiceNegativo() {
        QuestionRequest request = new QuestionRequest();
        request.setOptions(List.of("Correcta", "D1", "D2", "D3", "D4"));
        request.setCorrectIndex(-1);
        assertThrows(ValidationException.class, () -> filter.process(request));
    }

    @Test
    void deberiaRechazarIndiceFueraDeRango() {
        QuestionRequest request = new QuestionRequest();
        request.setOptions(List.of("Correcta", "D1", "D2", "D3", "D4"));
        request.setCorrectIndex(10);
        assertThrows(ValidationException.class, () -> filter.process(request));
    }
}
