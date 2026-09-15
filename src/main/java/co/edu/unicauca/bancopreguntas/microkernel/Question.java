package co.edu.unicauca.bancopreguntas.microkernel;

/**
 * Nucleo del microkernel.
 *
 * Representacion minima de una pregunta que administra el microkernel,
 * segun el diseno propuesto en la guia "Patrones Microkernel, Tuberias y
 * Filtros": un identificador, un titulo, un contenido y un tipo. El detalle
 * pedagogico completo (distractores, competencia, estado, etc.) vive en
 * {@link co.edu.unicauca.bancopreguntas.domain.Question}, al que esta clase
 * se traduce mediante {@link QuestionBankBridge} una vez que la tuberia de
 * filtros la valida.
 */
public class Question {

    private String id;
    private String title;
    private String content;
    private String type;

    public Question(String id, String title, String content, String type) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
