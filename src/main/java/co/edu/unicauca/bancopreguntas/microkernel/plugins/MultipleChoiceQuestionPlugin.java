package co.edu.unicauca.bancopreguntas.microkernel.plugins;

import co.edu.unicauca.bancopreguntas.microkernel.Question;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionPlugin;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.QuestionPipeline;

import java.util.UUID;

/**
 * Plugin del microkernel para preguntas de seleccion multiple con unica
 * respuesta (RF-04/RF-05). Es el plugin que implementa el pipeline completo
 * de Tuberias y Filtros exigido por la guia de la actividad: la solicitud
 * solo se convierte en {@link Question} si pasa los cuatro filtros de HU03.
 *
 * Se instancia por reflexion desde {@code plugins.properties}
 * (ver {@link co.edu.unicauca.bancopreguntas.microkernel.PluginLoader}), por
 * lo que requiere un constructor publico sin argumentos.
 */
public class MultipleChoiceQuestionPlugin implements QuestionPlugin {

    private final QuestionPipeline pipeline = new QuestionPipeline();

    public MultipleChoiceQuestionPlugin() {
    }

    @Override
    public String getName() {
        return "multiple-choice";
    }

    @Override
    public boolean supports(String type) {
        return "MULTIPLE_CHOICE".equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) {
        // Entrada -> Tuberia de filtros (HU03) -> Salida lista para publicar.
        QuestionRequest validated = pipeline.run(request);

        System.out.println("Generando pregunta de seleccion multiple...");

        return new Question(
                UUID.randomUUID().toString(),
                validated.getTitle(),
                validated.getContext() + System.lineSeparator() + validated.getDirectQuestion(),
                validated.getType()
        );
    }
}
