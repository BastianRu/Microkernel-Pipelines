# Banco de Preguntas Saber Pro

Taller 5 - Laboratorio de Ingenieria del Software II (Periodo 2026.2) - Microkernel + Tuberias y Filtros

Este proyecto integra las tres historias de usuario implementadas hasta el momento en el
curso, todas sobre una unica base de codigo:

| Historia de usuario | Talleres previos de donde se integro | Patron/arquitectura |
|---|---|---|
| HU01 - Gestion de usuarios del sistema | `SOLID-principles` (login, registro, roles, hashing Argon2, SQLite) | Principios SOLID, capas |
| HU02 - Gestion del banco de preguntas | `LayersPattern-MicropatternMVC` (CRUD de preguntas, ciclo de vida basico, estadisticas) | Patron de capas + micro patron MVC + Observer |
| HU03 - Validacion estructural | Nueva, especifica de este taller | **Microkernel** (nucleo + plugins registrados por reflexion) + **Tuberias y Filtros** |

La interfaz grafica de usuario, homogenea en todo el proyecto, esta implementada en
**Java Desktop con Swing**.

## Como se unieron los proyectos anteriores

El codigo de `SOLID-principles` (JavaFX) se porto a Swing conservando intactas sus capas de
dominio, acceso a datos y servicio (`domain/user`, `access/user`, `service/user`); solo cambio
la capa de presentacion (`presentation/user`). El codigo de `LayersPattern-MicropatternMVC`
(ya en Swing) se copio sin modificaciones (`domain`, `access`, `infra`, `presentation`). El
punto de entrada (`Main`) ensambla ambos modulos: tras iniciar sesion (HU01), el menu
principal permite abrir la gestion del banco de preguntas (HU02) y el nuevo generador de
preguntas basado en microkernel (HU03); ambos comparten el mismo `QuestionService`, de modo
que las preguntas generadas por los plugins de HU03 aparecen automaticamente en la vista de
HU02.

## Arquitectura de HU03: Microkernel + Tuberias y Filtros

- **Nucleo (`microkernel`)**: `QuestionMicrokernel` almacena el banco de preguntas del
  microkernel en un `Map<String, Question>` y administra el ciclo de vida de los plugins
  (`registerPlugin` / `unregisterPlugin` / `executePlugin`).
- **Plugins (`microkernel.plugins`)**: cuatro plugins que implementan el contrato comun
  `QuestionPlugin` (`getName`, `supports`, `generate`): `MultipleChoiceQuestionPlugin`,
  `CaseQuestionPlugin`, `MultimediaQuestionPlugin` y `ExcelImportQuestionPlugin`. Se registran
  **por reflexion** (uso obligatorio segun la guia de la actividad) a partir de
  `src/main/resources/plugins.properties`, mediante `PluginLoader`.
- **Tuberia de filtros (`microkernel.pipeline`)**: `QuestionPipeline` encadena
  `ContentValidationFilter -> OptionsValidationFilter -> ClassificationFilter ->
  CorrectAnswerValidationFilter`, implementando los requisitos RF-08 a RF-13 de HU03.
  `MultipleChoiceQuestionPlugin` es el plugin que ejecuta el pipeline completo, tal como pide
  la guia; los demas plugins reutilizan los filtros que les aplican.
- **Puente con HU02 (`QuestionBankBridge`)**: traduce la `Question` (minima) que produce el
  microkernel hacia el `Question` de dominio de HU02 (con distractores y estado) y la registra
  en el banco de preguntas compartido.

## Ejecutar el proyecto

```bash
mvn test      # ejecuta las pruebas unitarias (dominio, servicios, filtros, plugins, nucleo)
mvn package   # genera el jar
mvn exec:java # (requiere el plugin exec, o ejecutar co.edu.unicauca.bancopreguntas.Main desde el IDE)
```

Requiere JDK 17+. La base de datos SQLite (`banco-preguntas.db`) y las tablas de usuarios se
crean automaticamente en el primer arranque.
