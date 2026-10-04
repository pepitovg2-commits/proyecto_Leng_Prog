package S02_S03_Funcional

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, StandardCopyOption}
import java.util.concurrent.TimeUnit

object PrologService {
  private val executable = sys.env.getOrElse("SWIPL_PATH", "swipl")
  private val answerPattern = """(\d+)\|([a-z_]+)""".r

  // Adaptador entre Scala y SWI-Prolog: se genera un archivo temporal con los
  // hechos libro/5 actuales, se ejecutan las reglas y se extraen sus respuestas.
  // Así los hechos no se duplican: Datos.biblioteca sigue siendo la fuente única.
  def recomendar(libros: List[(String, String, String, Int)]): List[(Int, String)] = {
    val goal =
      "findall(Id-Motivo, recomendacion(Id, Motivo), Respuestas), " +
        "forall(member(Id-Motivo, Respuestas), format('~d|~w~n', [Id, Motivo]))"
    ejecutar(libros, goal)
  }

  // Consulta relacionado(Id, X, Motivo): devuelve cada libro X relacionado con Id
  // junto con el motivo (mismo_autor o misma_categoria) que Prolog dedujo.
  def relacionados(libros: List[(String, String, String, Int)], id: Int): List[(Int, String)] = {
    val goal =
      s"findall(X-Motivo, relacionado($id, X, Motivo), Respuestas), " +
        "forall(member(X-Motivo, Respuestas), format('~d|~w~n', [X, Motivo]))"
    ejecutar(libros, goal)
  }

  // Carga hechos + reglas en SWI-Prolog, ejecuta el objetivo y lee las líneas id|motivo.
  private def ejecutar(libros: List[(String, String, String, Int)], goal: String): List[(Int, String)] = {
    val rulesFile = copyRules()
    val factsFile = Files.createTempFile("lumen-libros-", ".pl")

    try {
      val facts = libros.zipWithIndex.map { case ((titulo, autor, categoria, paginas), index) =>
        s"libro(${index + 1}, ${atom(titulo)}, ${atom(autor)}, ${atom(categoria)}, $paginas)."
      }.mkString("\n")
      // Reglas y hechos van en un solo archivo: así no depende de que la versión de
      // SWI-Prolog acepte varias opciones -s.
      val rules = Files.readString(rulesFile, StandardCharsets.UTF_8)
      Files.writeString(factsFile, ":- encoding(utf8).\n" + rules + "\n" + facts + "\n", StandardCharsets.UTF_8)

      val process = new ProcessBuilder(
        executable,
        "-q",
        "-s", factsFile.toString,
        "-g", goal,
        "-t", "halt"
      ).redirectErrorStream(true).start()

      if (!process.waitFor(10, TimeUnit.SECONDS)) {
        process.destroyForcibly()
        throw new IllegalStateException("SWI-Prolog superó el límite de ejecución de 10 segundos.")
      }

      val output = new String(process.getInputStream.readAllBytes(), StandardCharsets.UTF_8)
      if (process.exitValue() != 0) {
        throw new IllegalStateException(
          s"SWI-Prolog terminó con error: ${output.trim.take(500)}"
        )
      }

      // Cada solución se imprime como id|motivo; el id permite recuperar el libro original.
      output.linesIterator.flatMap {
        case answerPattern(rawId, motivo) => rawId.toIntOption.map(_ -> motivo)
        case _ => None
      }.toList
    } catch {
      case error: IOException if error.getMessage != null && error.getMessage.contains("CreateProcess") =>
        throw new IllegalStateException(
          "No se encontró SWI-Prolog. Instálalo y agrégalo a PATH, o configura SWIPL_PATH.",
          error
        )
    } finally {
      Files.deleteIfExists(factsFile)
      Files.deleteIfExists(rulesFile)
    }
  }

  private def copyRules(): Path = {
    val stream = Option(getClass.getResourceAsStream("/prolog/biblioteca.pl"))
      .getOrElse(throw new IllegalStateException("No se encontró el recurso prolog/biblioteca.pl."))
    val path = Files.createTempFile("lumen-reglas-", ".pl")
    try Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING)
    finally stream.close()
    path
  }

  // Los valores externos se convierten en átomos Prolog entre comillas y con escapes.
  private def atom(value: String): String = {
    val escaped = value.flatMap {
      case '\\' => "\\\\"
      case '\'' => "\\'"
      case '\n' => "\\n"
      case '\r' => "\\r"
      case '\t' => "\\t"
      case char => char.toString
    }
    s"'$escaped'"
  }
}