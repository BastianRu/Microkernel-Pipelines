package co.edu.unicauca.bancopreguntas.microkernel.pipeline;

import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OptionsValidationFilterTest {

    private final OptionsValidationFilter filter = new OptionsValidationFilter();

    private QuestionRequest requestWithOptions(List<String> options) {
        QuestionRequest request = new QuestionRequest();
        request.setOptions(options);
        return request;
    }

    @Test
    void deberiaAceptarUnaRespuestaYCuatroDistractoresValidos() {
        QuestionRequest request = requestWithOptions(List.of(
                "Modelar el dominio del negocio",
                "Disenar bases de datos",
                "Eliminar la necesidad de pruebas",
                "Reemplazar el analisis de requisitos",
                "Sustituir la documentacion tecnica"));
        assertDoesNotThrow(() -> filter.process(request));
    }

    @Test
    void deberiaRechazarSiNoHayExactamenteCuatroDistractores() {
        QuestionRequest request = requestWithOptions(List.of("Correcta", "Distractor 1", "Distractor 2"));
        assertThrows(ValidationException.class, () -> filter.process(request));
    }

    @Test
    void deberiaRechazarOpcionesDuplicadas() {
        QuestionRequest request = requestWithOptions(List.of(
                "Opcion repetida", "Distractor 1", "Distractor 2", "Distractor 3", "Opcion repetida"));
        assertThrows(ValidationException.class, () -> filter.process(request));
    }

    @Test
    void deberiaRechazarExpresionTodasLasAnteriores() {
        QuestionRequest request = requestWithOptions(List.of(
                "Correcta", "Distractor 1", "Distractor 2", "Distractor 3", "Todas las anteriores"));
        assertThrows(ValidationException.class, () -> filter.process(request));
    }

    @Test
    void deberiaRechazarExpresionNingunaDeLasAnteriores() {
        QuestionRequest request = requestWithOptions(List.of(
                "Correcta", "Distractor 1", "Distractor 2", "Ninguna de las anteriores", "Distractor 4"));
        assertThrows(ValidationException.class, () -> filter.process(request));
    }

    @Test
    void deberiaRechazarOpcionVacia() {
        QuestionRequest request = requestWithOptions(java.util.Arrays.asList(
                "Correcta", "", "Distractor 2", "Distractor 3", "Distractor 4"));
        assertThrows(ValidationException.class, () -> filter.process(request));
    }
}
