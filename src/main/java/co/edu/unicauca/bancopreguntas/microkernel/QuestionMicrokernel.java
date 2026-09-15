package co.edu.unicauca.bancopreguntas.microkernel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Nucleo (core) del patron Microkernel.
 *
 * Responsabilidades del nucleo, segun la guia de la actividad:
 * <ul>
 *     <li>Almacenar el banco de preguntas generadas por los plugins.</li>
 *     <li>Registrar plugins (ver {@link PluginLoader}, que los descubre por
 *     reflexion a partir de {@code plugins.properties}).</li>
 *     <li>Ejecutar plugins sobre una solicitud de pregunta.</li>
 *     <li>Gestionar el ciclo de vida de los plugins (registrar/retirar).</li>
 * </ul>
 * El nucleo no sabe nada de seleccion multiple, casos, multimedia, etc.: esa
 * logica esta encapsulada en cada plugin, lo que permite incorporar nuevos
 * tipos de pregunta sin modificar esta clase (RNF-13, RNF-14).
 */
public class QuestionMicrokernel {

    private final Map<String, Question> questions = new LinkedHashMap<>();
    private final Map<String, QuestionPlugin> plugins = new LinkedHashMap<>();

    /**
     * Registra un plugin en el nucleo (alta en el ciclo de vida de plugins).
     *
     * @param plugin plugin a registrar; se indexa por su nombre.
     */
    public void registerPlugin(QuestionPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("El plugin no puede ser nulo");
        }
        plugins.put(plugin.getName(), plugin);
    }

    /**
     * Retira un plugin previamente registrado (baja en el ciclo de vida).
     *
     * @param pluginName nombre del plugin a retirar.
     */
    public void unregisterPlugin(String pluginName) {
        plugins.remove(pluginName);
    }

    /** @return nombres de los plugins actualmente registrados. */
    public Collection<String> listPluginNames() {
        return List.copyOf(plugins.keySet());
    }

    /**
     * Ejecuta el plugin que soporte el tipo de pregunta solicitado y
     * almacena en el nucleo la pregunta que este genere.
     *
     * @param type    tipo de pregunta (por ejemplo "MULTIPLE_CHOICE").
     * @param request datos de la pregunta a generar.
     * @return la pregunta generada y ya almacenada en el banco del nucleo.
     * @throws NoSuchElementException si ningun plugin registrado soporta el tipo.
     */
    public Question executePlugin(String type, QuestionRequest request) {
        QuestionPlugin plugin = findPluginFor(type);
        Question generated = plugin.generate(request);
        questions.put(generated.getId(), generated);
        return generated;
    }

    private QuestionPlugin findPluginFor(String type) {
        for (QuestionPlugin plugin : plugins.values()) {
            if (plugin.supports(type)) {
                return plugin;
            }
        }
        throw new NoSuchElementException("Ningun plugin registrado soporta el tipo: " + type);
    }

    /** @return todas las preguntas almacenadas por el nucleo. */
    public List<Question> getQuestions() {
        return new ArrayList<>(questions.values());
    }

    public Map<String, Question> getQuestionsById() {
        return new LinkedHashMap<>(questions);
    }
}
