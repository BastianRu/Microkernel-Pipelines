package co.edu.unicauca.bancopreguntas.microkernel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionMicrokernelTest {

    /** Plugin de prueba que no depende de la tuberia de filtros real. */
    private static class EchoPlugin implements QuestionPlugin {
        @Override
        public String getName() {
            return "echo";
        }

        @Override
        public boolean supports(String type) {
            return "ECHO".equalsIgnoreCase(type);
        }

        @Override
        public Question generate(QuestionRequest request) {
            return new Question("Q-1", request.getTitle(), request.getContext(), request.getType());
        }
    }

    private QuestionMicrokernel microkernel;

    @BeforeEach
    void setUp() {
        microkernel = new QuestionMicrokernel();
    }

    @Test
    void deberiaRegistrarYListarPlugins() {
        microkernel.registerPlugin(new EchoPlugin());
        assertTrue(microkernel.listPluginNames().contains("echo"));
    }

    @Test
    void deberiaEjecutarElPluginQueSoportaElTipoYAlmacenarLaPregunta() {
        microkernel.registerPlugin(new EchoPlugin());

        QuestionRequest request = new QuestionRequest();
        request.setType("ECHO");
        request.setTitle("Prueba");
        request.setContext("contenido");

        Question generated = microkernel.executePlugin("ECHO", request);

        assertEquals("Q-1", generated.getId());
        assertEquals(1, microkernel.getQuestions().size());
    }

    @Test
    void deberiaFallarSiNingunPluginSoportaElTipo() {
        QuestionRequest request = new QuestionRequest();
        request.setType("DESCONOCIDO");

        assertThrows(NoSuchElementException.class, () -> microkernel.executePlugin("DESCONOCIDO", request));
    }

    @Test
    void deberiaDejarDeUsarUnPluginRetirado() {
        microkernel.registerPlugin(new EchoPlugin());
        microkernel.unregisterPlugin("echo");

        QuestionRequest request = new QuestionRequest();
        request.setType("ECHO");

        assertThrows(NoSuchElementException.class, () -> microkernel.executePlugin("ECHO", request));
    }
}
