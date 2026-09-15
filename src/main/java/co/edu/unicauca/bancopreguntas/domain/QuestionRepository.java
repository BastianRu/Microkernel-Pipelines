package co.edu.unicauca.bancopreguntas.domain;

import java.util.List;
import java.util.Optional;

/**
 * Capa de dominio.
 * Abstraccion de la persistencia de preguntas. El dominio define el contrato y
 * la capa de acceso a datos lo implementa.
 *
 * DIP: la logica de negocio depende de esta interfaz y no de una tecnologia de
 * almacenamiento concreta, por lo que la implementacion en memoria se puede
 * reemplazar por una base de datos sin tocar el dominio.
 */
public interface QuestionRepository {

    /**
     * @return todas las preguntas almacenadas.
     */
    List<Question> findAll();

    /**
     * @param id identificador de la pregunta.
     * @return la pregunta si existe.
     */
    Optional<Question> findById(String id);

    /**
     * Almacena una pregunta nueva.
     *
     * @param question pregunta a almacenar.
     * @return true si se almaceno, false si ya existia una con el mismo id.
     */
    boolean save(Question question);

    /**
     * Actualiza una pregunta existente.
     *
     * @param question pregunta con los datos modificados.
     * @return true si se actualizo, false si no existia.
     */
    boolean update(Question question);
}
