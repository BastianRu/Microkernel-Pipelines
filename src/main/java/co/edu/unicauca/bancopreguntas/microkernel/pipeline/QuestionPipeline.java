package co.edu.unicauca.bancopreguntas.microkernel.pipeline;

import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Patron Tuberias y Filtros aplicado a HU03 - Validacion estructural.
 *
 * Encadena los filtros en el orden definido por la guia de la actividad:
 * Entrada (pregunta nueva) -&gt; ContentValidationFilter -&gt;
 * OptionsValidationFilter -&gt; ClassificationFilter -&gt;
 * CorrectAnswerValidationFilter -&gt; Salida (pregunta procesada y lista
 * para publicar). Si un filtro falla, la tuberia se detiene y reporta sus
 * errores; si el fallo es en un filtro posterior, se reportan tambien los
 * errores acumulados de haber vuelto a intentarlo.
 */
public class QuestionPipeline {

    private final List<Filter> filters = new ArrayList<>();

    public QuestionPipeline() {
        filters.add(new ContentValidationFilter());
        filters.add(new OptionsValidationFilter());
        filters.add(new ClassificationFilter());
        filters.add(new CorrectAnswerValidationFilter());
    }

    /**
     * Permite construir tuberias a la medida (por ejemplo, para pruebas
     * unitarias con un subconjunto de filtros).
     */
    public QuestionPipeline(List<Filter> customFilters) {
        filters.addAll(customFilters);
    }

    /**
     * Ejecuta la solicitud a traves de todos los filtros, en orden.
     *
     * @param request solicitud de pregunta nueva.
     * @return la solicitud validada y lista para publicar.
     * @throws ValidationException con los errores del primer filtro que falle.
     */
    public QuestionRequest run(QuestionRequest request) {
        QuestionRequest current = request;
        for (Filter filter : filters) {
            current = filter.process(current);
        }
        return current;
    }
}
