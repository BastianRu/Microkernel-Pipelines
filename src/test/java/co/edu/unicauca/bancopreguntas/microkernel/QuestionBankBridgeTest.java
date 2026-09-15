package co.edu.unicauca.bancopreguntas.microkernel;

import co.edu.unicauca.bancopreguntas.access.QuestionImplRepository;
import co.edu.unicauca.bancopreguntas.domain.QuestionRepository;
import co.edu.unicauca.bancopreguntas.domain.QuestionService;
import co.edu.unicauca.bancopreguntas.microkernel.plugins.MultipleChoiceQuestionPlugin;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica el enlace entre el microkernel (HU03) y el banco de preguntas de
 * HU02: una pregunta generada por un plugin debe terminar visible a traves
 * de IQuestionService, el mismo servicio que consume GUIQuestions.
 */
class QuestionBankBridgeTest {

    @Test
    void deberiaPublicarLaPreguntaGeneradaEnElBancoDePreguntas() {
        QuestionRepository repository = new QuestionImplRepository();
        int initialCount = repository.findAll().size();
        QuestionService service = new QuestionService(repository);
        QuestionBankBridge bridge = new QuestionBankBridge(service);

        QuestionRequest request = new QuestionRequest();
        request.setType("MULTIPLE_CHOICE");
        request.setTitle("Patron Observer");
        request.setContext("El patron Observer permite notificar a varios objetos un cambio de estado.");
        request.setDirectQuestion("Que problema resuelve el patron Observer?");
        request.setOptions(List.of(
                "Notificar automaticamente a varios objetos un cambio de estado",
                "Crear objetos sin exponer la logica de creacion",
                "Convertir una interfaz en otra",
                "Restringir una clase a una unica instancia",
                "Separar una abstraccion de su implementacion"));
        request.setCorrectIndex(0);
        request.setCompetencia("Diseno de software");
        request.setTema("Patrones de diseno");
        request.setNivelDificultad("MEDIO");

        Question generated = new MultipleChoiceQuestionPlugin().generate(request);
        boolean published = bridge.publish(generated, request);

        assertTrue(published);
        assertEquals(initialCount + 1, service.listQuestions().size());
        assertTrue(service.findById(generated.getId()).isPresent());
    }
}
