package co.edu.unicauca.bancopreguntas.microkernel.pipeline;

import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;

/**
 * Contrato del patron Tuberias y Filtros. Cada filtro valida un aspecto
 * particular de la pregunta y, si es correcto, devuelve la misma solicitud
 * (o una version enriquecida de ella) para que el siguiente filtro continue
 * procesandola.
 */
public interface Filter {

    /**
     * @param request solicitud recibida del filtro anterior (o la original).
     * @return la solicitud, lista para el siguiente filtro.
     * @throws ValidationException si la solicitud no cumple las reglas de este filtro.
     */
    QuestionRequest process(QuestionRequest request);
}
