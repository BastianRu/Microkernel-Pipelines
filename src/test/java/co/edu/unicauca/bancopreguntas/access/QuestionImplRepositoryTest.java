package co.edu.unicauca.bancopreguntas.access;

import co.edu.unicauca.bancopreguntas.domain.Question;
import co.edu.unicauca.bancopreguntas.domain.QuestionDistractors;
import co.edu.unicauca.bancopreguntas.domain.QuestionRepository;
import co.edu.unicauca.bancopreguntas.domain.QuestionState;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de la implementacion en memoria del repositorio.
 */
class QuestionImplRepositoryTest {

    private QuestionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new QuestionImplRepository();
    }

    private Question question(String id) {
        return new Question(id, "Pregunta " + id, "Enunciado de " + id,
                new QuestionDistractors(List.of("Opcion A", "Opcion B"), 0),
                QuestionState.BORRADOR);
    }

    @Test
    @DisplayName("Carga preguntas de ejemplo al construirse")
    void shouldLoadSampleData() {
        assertFalse(repository.findAll().isEmpty());
        assertTrue(repository.findById("P-001").isPresent());
    }

    @Test
    @DisplayName("Buscar por un id inexistente o nulo devuelve vacio")
    void shouldReturnEmptyForUnknownId() {
        assertTrue(repository.findById("NO-EXISTE").isEmpty());
        assertTrue(repository.findById(null).isEmpty());
    }

    @Test
    @DisplayName("Guarda una pregunta nueva y rechaza ids duplicados")
    void shouldSaveOnlyNewQuestions() {
        int initial = repository.findAll().size();

        assertTrue(repository.save(question("P-100")));
        assertFalse(repository.save(question("P-100")));
        assertFalse(repository.save(null));
        assertEquals(initial + 1, repository.findAll().size());
    }

    @Test
    @DisplayName("Actualiza solo preguntas existentes")
    void shouldUpdateOnlyExistingQuestions() {
        Question stored = repository.findById("P-001").orElseThrow();
        stored.changeState(QuestionState.ELIMINADA);

        assertTrue(repository.update(stored));
        assertEquals(QuestionState.ELIMINADA, repository.findById("P-001").orElseThrow().getState());
        assertFalse(repository.update(question("NO-EXISTE")));
        assertFalse(repository.update(null));
    }

    @Test
    @DisplayName("La lista devuelta es una copia y no altera el almacenamiento")
    void shouldReturnDefensiveCopyOfList() {
        int initial = repository.findAll().size();

        repository.findAll().clear();

        assertEquals(initial, repository.findAll().size());
    }
}
