package co.edu.unicauca.bancopreguntas.microkernel.pipeline;

import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClassificationFilterTest {

    private final ClassificationFilter filter = new ClassificationFilter();

    private QuestionRequest validRequest() {
        QuestionRequest request = new QuestionRequest();
        request.setCompetencia("Razonamiento cuantitativo");
        request.setTema("Arquitectura de software");
        request.setNivelDificultad("MEDIO");
        return request;
    }

    @Test
    void deberiaAceptarClasificacionCompleta() {
        assertDoesNotThrow(() -> filter.process(validRequest()));
    }

    @Test
    void deberiaAsignarSubtemaPorDefectoCuandoNoSeIndica() {
        QuestionRequest request = validRequest();
        filter.process(request);
        assertEquals("Arquitectura de software", request.getSubtema());
    }

    @Test
    void deberiaRechazarCompetenciaVacia() {
        QuestionRequest request = validRequest();
        request.setCompetencia(" ");
        assertThrows(ValidationException.class, () -> filter.process(request));
    }

    @Test
    void deberiaRechazarNivelDeDificultadInvalido() {
        QuestionRequest request = validRequest();
        request.setNivelDificultad("EXTREMO");
        assertThrows(ValidationException.class, () -> filter.process(request));
    }
}
