package co.edu.unicauca.bancopreguntas.microkernel.plugins;

import co.edu.unicauca.bancopreguntas.microkernel.Question;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionPlugin;
import co.edu.unicauca.bancopreguntas.microkernel.QuestionRequest;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.CorrectAnswerValidationFilter;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.OptionsValidationFilter;
import co.edu.unicauca.bancopreguntas.microkernel.pipeline.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Plugin del microkernel que simula la importacion de preguntas desde un
 * archivo externo (por ejemplo, Excel). Segun las restricciones academicas
 * del proyecto, esta integracion se implementa como un prototipo funcional:
 * no depende de una libreria de hojas de calculo, solo valida que se haya
 * indicado un archivo de origen y arma la pregunta con la informacion que
 * ya vino en la solicitud (como si hubiera sido leida de una fila del
 * archivo).
 */
public class ExcelImportQuestionPlugin implements QuestionPlugin {

    private final OptionsValidationFilter optionsFilter = new OptionsValidationFilter();
    private final CorrectAnswerValidationFilter correctAnswerFilter = new CorrectAnswerValidationFilter();

    public ExcelImportQuestionPlugin() {
    }

    @Override
    public String getName() {
        return "excel-import";
    }

    @Override
    public boolean supports(String type) {
        return "EXCEL_IMPORT".equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) {
        List<String> errors = new ArrayList<>();
        if (request.getSourceFilePath() == null || request.getSourceFilePath().isBlank()) {
            errors.add("La importacion requiere la ruta del archivo de origen");
        }
        if (request.getContext() == null || request.getContext().isBlank()) {
            errors.add("La fila importada no tiene contexto");
        }
        if (request.getDirectQuestion() == null || request.getDirectQuestion().isBlank()) {
            errors.add("La fila importada no tiene pregunta directa");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        optionsFilter.process(request);
        correctAnswerFilter.process(request);

        System.out.println("Importando pregunta desde archivo: " + request.getSourceFilePath());

        String content = request.getContext() + System.lineSeparator()
                + request.getDirectQuestion() + System.lineSeparator()
                + "Importado desde: " + request.getSourceFilePath();

        return new Question(
                UUID.randomUUID().toString(),
                request.getTitle(),
                content,
                request.getType()
        );
    }
}
