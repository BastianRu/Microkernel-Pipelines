package co.edu.unicauca.bancopreguntas.microkernel;

import co.edu.unicauca.bancopreguntas.domain.IQuestionService;
import co.edu.unicauca.bancopreguntas.domain.QuestionDistractors;
import co.edu.unicauca.bancopreguntas.domain.QuestionState;

import java.util.ArrayList;
import java.util.List;

/**
 * Punto de union entre la arquitectura Microkernel (HU03) y el banco de
 * preguntas de HU02 (patron de capas + micro patron MVC).
 *
 * Cuando un plugin del microkernel genera una {@link Question} valida (ya
 * paso por la tuberia de filtros), esta clase la traduce al modelo de
 * dominio mas rico usado por la gestion del banco de preguntas
 * ({@code co.edu.unicauca.bancopreguntas.domain.Question}, con distractores
 * y estado) y la registra en el {@link IQuestionService} compartido, de
 * modo que aparece automaticamente en la vista de HU02 (GUIQuestions) y
 * dispara la notificacion a sus observadores de estadisticas.
 */
public class QuestionBankBridge {

    private final IQuestionService questionService;

    public QuestionBankBridge(IQuestionService questionService) {
        if (questionService == null) {
            throw new IllegalArgumentException("El servicio de preguntas es obligatorio");
        }
        this.questionService = questionService;
    }

    /**
     * Publica en el banco de preguntas (HU02) la pregunta generada por un
     * plugin del microkernel (HU03).
     *
     * @param generated pregunta ya validada y generada por el plugin.
     * @param request   solicitud original, de donde se toman las opciones de
     *                  respuesta y la clasificacion.
     * @return true si la pregunta se agrego al banco.
     */
    public boolean publish(Question generated, QuestionRequest request) {
        List<String> options = new ArrayList<>(request.getOptions());
        QuestionDistractors distractors = new QuestionDistractors(options, request.getCorrectIndex());

        co.edu.unicauca.bancopreguntas.domain.Question domainQuestion =
                new co.edu.unicauca.bancopreguntas.domain.Question(
                        generated.getId(),
                        generated.getTitle(),
                        request.getDirectQuestion(),
                        distractors,
                        QuestionState.BORRADOR
                );

        return questionService.registerQuestion(domainQuestion);
    }
}
