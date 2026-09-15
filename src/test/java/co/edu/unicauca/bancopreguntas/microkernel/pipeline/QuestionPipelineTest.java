package co.edu.unicauca.bancopreguntas.microkernel.pipeline;

import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Prueba el patron Tuberias y Filtros de punta a punta: la solicitud debe
 * atravesar los cuatro filtros de HU03 en orden y solo sale "lista para
 * publicar" si los cumple todos.
 */
class QuestionPipelineTest {

    private final QuestionPipeline pipeline = new QuestionPipeline();

    private QuestionRequest validRequest() {
        QuestionRequest request = new QuestionRequest();
        request.setType("MULTIPLE_CHOICE");
        request.setTitle("Patron Microkernel");
        request.setContext("El patron Microkernel separa un nucleo minimo de las funcionalidades variables.");
        request.setDirectQuestion("Cual es la ventaja principal de este patron?");
        request.setOptions(List.of(
                "Permite agregar plugins sin modificar el nucleo",
                "Obliga a reescribir el nucleo por cada cambio",
                "Elimina la necesidad de pruebas unitarias",
                "Impide el uso de reflexion",
                "Requiere una base de datos distribuida"));
        request.setCorrectIndex(0);
        request.setCompetencia("Arquitectura de software");
        request.setTema("Estilos arquitectonicos");
        request.setNivelDificultad("MEDIO");
        return request;
    }

    @Test
    void deberiaProcesarUnaSolicitudCompletamenteValida() {
        assertDoesNotThrow(() -> pipeline.run(validRequest()));
    }

    @Test
    void deberiaDetenerseEnElPrimerFiltroQueFalle() {
        QuestionRequest request = validRequest();
        request.setContext(null);
        assertThrows(ValidationException.class, () -> pipeline.run(request));
    }

    @Test
    void deberiaRechazarSiFaltanDistractores() {
        QuestionRequest request = validRequest();
        request.setOptions(List.of("Correcta", "Solo un distractor"));
        assertThrows(ValidationException.class, () -> pipeline.run(request));
    }
}
