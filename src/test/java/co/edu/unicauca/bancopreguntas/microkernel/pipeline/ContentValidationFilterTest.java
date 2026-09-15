package co.edu.unicauca.bancopreguntas.microkernel.pipeline;

import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ContentValidationFilterTest {

    private final ContentValidationFilter filter = new ContentValidationFilter();

    private QuestionRequest validRequest() {
        QuestionRequest request = new QuestionRequest();
        request.setContext("En el contexto de la arquitectura de software, los estilos definen restricciones.");
        request.setDirectQuestion("Cual es el objetivo principal del estilo microkernel?");
        return request;
    }

    @Test
    void deberiaAceptarContextoYPreguntaValidos() {
        assertDoesNotThrow(() -> filter.process(validRequest()));
    }

    @Test
    void deberiaRechazarContextoVacio() {
        QuestionRequest request = validRequest();
        request.setContext("   ");
        assertThrows(ValidationException.class, () -> filter.process(request));
    }

    @Test
    void deberiaRechazarContextoDemasiadoCorto() {
        QuestionRequest request = validRequest();
        request.setContext("Muy corto");
        assertThrows(ValidationException.class, () -> filter.process(request));
    }

    @Test
    void deberiaRechazarPreguntaDirectaVacia() {
        QuestionRequest request = validRequest();
        request.setDirectQuestion("");
        assertThrows(ValidationException.class, () -> filter.process(request));
    }

    @Test
    void deberiaRechazarPreguntaSinSignoDeInterrogacion() {
        QuestionRequest request = validRequest();
        request.setDirectQuestion("Cual es el objetivo principal del estilo microkernel");
        assertThrows(ValidationException.class, () -> filter.process(request));
    }

    @Test
    void deberiaRechazarMultiplesPreguntasDirectas() {
        QuestionRequest request = validRequest();
        request.setDirectQuestion("Cual es el objetivo? Y el secundario?");
        assertThrows(ValidationException.class, () -> filter.process(request));
    }
}
