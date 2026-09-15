package co.edu.unicauca.bancopreguntas.microkernel;

/**
 * Contrato comun que deben cumplir todos los plugins del microkernel, tal
 * como lo exige la guia de la actividad: cada plugin sabe generar un tipo
 * particular de pregunta (seleccion multiple, caso, multimedia, importada,
 * generada por IA, etc.) sin que el nucleo conozca esos detalles.
 */
public interface QuestionPlugin {

    /** @return nombre unico del plugin, usado para registrarlo en el nucleo. */
    String getName();

    /** @param type tipo de pregunta solicitado. @return si este plugin lo atiende. */
    boolean supports(String type);

    /**
     * Genera una pregunta a partir de una solicitud.
     *
     * @param request datos crudos de la pregunta.
     * @return la pregunta generada, ya validada por el plugin.
     * @throws co.edu.unicauca.bancopreguntas.microkernel.pipeline.ValidationException
     *         si la solicitud no cumple las reglas estructurales de HU03.
     */
    Question generate(QuestionRequest request);
}
