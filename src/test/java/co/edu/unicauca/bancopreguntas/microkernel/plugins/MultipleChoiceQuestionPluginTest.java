package co.edu.unicauca.bancopreguntas.microkernel.plugins;

import co.edu.unicauca.bancopreguntas.microkernel.Question;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultipleChoiceQuestionPluginTest {

    private final MultipleChoiceQuestionPlugin plugin = new MultipleChoiceQuestionPlugin();

    private QuestionRequest validRequest() {
        QuestionRequest request = new QuestionRequest();
        request.setType("MULTIPLE_CHOICE");
        request.setTitle("Principio DIP");
        request.setContext("En el diseno orientado a objetos, el principio de inversion de dependencias es clave.");
        request.setDirectQuestion("De que deben depender los modulos de alto nivel?");
        request.setOptions(List.of(
                "De abstracciones",
                "De los modulos de bajo nivel",
                "Del framework de persistencia",
                "De la interfaz grafica",
                "De la base de datos"));
        request.setCorrectIndex(0);
        request.setCompetencia("Diseno de software");
        request.setTema("Principios SOLID");
        request.setNivelDificultad("MEDIO");
        return request;
    }

    @Test
    void deberiaReconocerElTipoMultipleChoice() {
        assertTrue(plugin.supports("MULTIPLE_CHOICE"));
        assertTrue(plugin.supports("multiple_choice"));
        assertEquals("multiple-choice", plugin.getName());
    }

    @Test
    void deberiaGenerarPreguntaCuandoLaSolicitudEsValida() {
        Question question = plugin.generate(validRequest());

        assertNotNull(question.getId());
        assertEquals("Principio DIP", question.getTitle());
        assertEquals("MULTIPLE_CHOICE", question.getType());
    }

    @Test
    void deberiaRechazarSolicitudQueNoPaseLaTuberiaDeFiltros() {
        QuestionRequest request = validRequest();
        request.setOptions(List.of("Correcta", "Un solo distractor"));

        assertThrows(ValidationException.class, () -> plugin.generate(request));
    }
}
