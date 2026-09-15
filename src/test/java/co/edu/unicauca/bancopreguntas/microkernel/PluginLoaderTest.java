package co.edu.unicauca.bancopreguntas.microkernel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica que el registro dinamico de plugins por reflexion (uso
 * obligatorio segun la guia de la actividad) efectivamente instancia y
 * registra en el nucleo las clases declaradas en plugins.properties.
 */
class PluginLoaderTest {

    @Test
    void deberiaCargarLosCuatroPluginsDeclaradosEnPluginsProperties() {
        QuestionMicrokernel microkernel = new QuestionMicrokernel();

        int loaded = PluginLoader.loadFromProperties(microkernel, "plugins.properties");

        assertEquals(4, loaded);
        assertTrue(microkernel.listPluginNames().contains("multiple-choice"));
        assertTrue(microkernel.listPluginNames().contains("case-study"));
        assertTrue(microkernel.listPluginNames().contains("multimedia"));
        assertTrue(microkernel.listPluginNames().contains("excel-import"));
    }

    @Test
    void deberiaLanzarErrorSiElRecursoNoExiste() {
        QuestionMicrokernel microkernel = new QuestionMicrokernel();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () ->
                PluginLoader.loadFromProperties(microkernel, "no-existe.properties"));
    }
}
