package co.edu.unicauca.bancopreguntas.microkernel;

import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de solicitud que un cliente (la UI del microkernel) entrega a un
 * {@link QuestionPlugin}. Transporta toda la informacion cruda de la
 * pregunta que la tuberia de filtros (HU03 - Validacion estructural) debe
 * revisar antes de que el plugin la convierta en un {@link Question} del
 * nucleo.
 *
 * No es un objeto de dominio: es deliberadamente permisivo (listas mutables,
 * sin invariantes) porque su proposito es llegar "crudo" a los filtros para
 * que sean ellos quienes apliquen las reglas de negocio de HU03.
 */
public class QuestionRequest {

    private String type;
    private String title;
    private String context;
    private String directQuestion;
    private final List<String> options = new ArrayList<>();
    private int correctIndex = -1;
    private String competencia;
    private String tema;
    private String subtema;
    private String nivelDificultad;
    private String mediaUrl;
    private String sourceFilePath;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDirectQuestion() {
        return directQuestion;
    }

    public void setDirectQuestion(String directQuestion) {
        this.directQuestion = directQuestion;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> newOptions) {
        options.clear();
        if (newOptions != null) {
            options.addAll(newOptions);
        }
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(int correctIndex) {
        this.correctIndex = correctIndex;
    }

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getSubtema() {
        return subtema;
    }

    public void setSubtema(String subtema) {
        this.subtema = subtema;
    }

    public String getNivelDificultad() {
        return nivelDificultad;
    }

    public void setNivelDificultad(String nivelDificultad) {
        this.nivelDificultad = nivelDificultad;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }
}
