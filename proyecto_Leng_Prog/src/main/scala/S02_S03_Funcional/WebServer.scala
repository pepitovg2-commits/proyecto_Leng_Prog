package S02_S03_Funcional

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import java.io.{OutputStream, InputStream}
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicReference
import scala.io.Source

object WebServer {
  import Datos._

  case class Libro(titulo: String, autor: String, categoria: String, paginas: Int)

  val libros: List[Libro] = biblioteca.map { case (titulo, autor, categoria, paginas) =>
    Libro(titulo, autor, categoria, paginas)
  }

  private def readResource(path: String): Array[Byte] = {
    val stream: InputStream = getClass.getClassLoader.getResourceAsStream(path)
    if (stream == null) {
      throw new IllegalArgumentException(s"No se encontró el recurso: $path")
    }
    try Source.fromInputStream(stream, "UTF-8").mkString.getBytes(StandardCharsets.UTF_8)
    finally stream.close()
  }

  private def buscarLibros(texto: String): List[Libro] = {
    if (texto.trim.isEmpty) libros
    else {
      val q = texto.toLowerCase.trim
      libros.filter(l =>
        l.autor.toLowerCase.contains(q) ||
          l.categoria.toLowerCase.contains(q) ||
          l.titulo.toLowerCase.contains(q)
      )
    }
  }

  private def jsonLibros(items: List[Libro]): String = {
    items.map(l =>
      s"{\"titulo\":\"${l.titulo}\",\"autor\":\"${l.autor}\",\"categoria\":\"${l.categoria}\",\"paginas\":${l.paginas}}"
    ).mkString("[", ",", "]")
  }

  private def send(exchange: HttpExchange, status: Int, contentType: String, body: Array[Byte]): Unit = {
    exchange.getResponseHeaders().set("Content-Type", contentType)
    exchange.sendResponseHeaders(status, body.length.toLong)
    val os: OutputStream = exchange.getResponseBody
    os.write(body)
    os.close()
  }

  private def createContext(server: HttpServer): Unit = {
    server.createContext("/", new HttpHandler {
      override def handle(exchange: HttpExchange): Unit = {
        val rawPath = exchange.getRequestURI.getPath
        val query = exchange.getRequestURI.getQuery
        val params = Option(query).map(_.split("&")).getOrElse(Array.empty[String]).flatMap { part =>
          val idx = part.indexOf('=')
          if (idx >= 0) Some(part.substring(0, idx) -> part.substring(idx + 1)) else None
        }.toMap

        rawPath match {
          case "/" =>
            val html = readResource("public/index.html")
            send(exchange, 200, "text/html; charset=utf-8", html)

          case "/styles.css" =>
            val css = readResource("public/styles.css")
            send(exchange, 200, "text/css; charset=utf-8", css)

          case "/app.js" =>
            val js = readResource("public/app.js")
            send(exchange, 200, "application/javascript; charset=utf-8", js)

          case "/api/libros" =>
            val body =
              jsonLibros(libros)
                .getBytes(StandardCharsets.UTF_8)

            send(
              exchange,
              200,
              "application/json; charset=utf-8",
              body
            )

          case "/api/buscar" =>
            val texto =
              params.getOrElse("q", "")

            val body =
              jsonLibros(buscarLibros(texto))
                .getBytes(StandardCharsets.UTF_8)

            send(
              exchange,
              200,
              "application/json; charset=utf-8",
              body
            )

          case "/api/estadisticas" =>
            val totalLibros =
              Datos.biblioteca.length

            val promedio =
              Funciones.promedioPaginas(
                Datos.biblioteca)

            val categorias =
              Datos.biblioteca
                .map(libro => libro._3)
                .distinct
                .sorted

            val libroLargo =
              Funciones.libroMasLargo(
                Datos.biblioteca
              )

            val libroCorto =
              Funciones.libroMasCorto(
                Datos.biblioteca
              )

            val cantidadLibrosCortos =
              Datos.biblioteca.count(
                Lambdas.esLibroCorto
              )

            val distribucionCategorias =
              Funciones.contarPorCategoria(
                Datos.biblioteca
              )

            val tituloMasLargo =
              libroLargo
                .map(libro => libro._1)
                .getOrElse("Sin datos")

            val paginasMasLargo =
              libroLargo
                .map(libro => libro._4)
                .getOrElse(0)

            val tituloMasCorto =
              libroCorto
                .map(libro => libro._1)
                .getOrElse("Sin datos")

            val paginasMasCorto =
              libroCorto
                .map(libro => libro._4)
                .getOrElse(0)

            val distribucionJson =
              distribucionCategorias
                .map { case (categoria, cantidad) =>
                  s"""{"categoria":"$categoria","cantidad":$cantidad}"""
                }
                .mkString("[", ",", "]")

            val payload =
              s"""
                 |{
                 |  "totalLibros": $totalLibros,
                 |  "promedioPaginas": $promedio,
                 |  "categorias": [${categorias.map(c => s""""$c"""").mkString(",")}],
                 |  "libroMasLargo": {
                 |    "titulo": "$tituloMasLargo",
                 |    "paginas": $paginasMasLargo
                 |  },
                 |  "libroMasCorto": {
                 |    "titulo": "$tituloMasCorto",
                 |    "paginas": $paginasMasCorto
                 |  },
                 |  "cantidadLibrosCortos": $cantidadLibrosCortos,
                 |  "distribucionCategorias": $distribucionJson
                 |}
                 |""".stripMargin

            send(
              exchange,
              200,
              "application/json; charset=utf-8",
              payload.getBytes(
                StandardCharsets.UTF_8
              )
            )

         

          case "/api/categoria" =>
            val categoriaSeleccionada = params.getOrElse("cat", "")
            // Decodificamos caracteres especiales de la URL (por si tiene espacios o tildes)
            val catDecodificada = java.net.URLDecoder.decode(categoriaSeleccionada, "UTF-8")
            
            // ✅ TEMA APLICADO (Sesión 3): Invocamos tu función definida de Funciones.scala
            val librosFiltradosTuplas = Funciones.filtrarPorCategoria(Datos.biblioteca, catDecodificada)
            
            // Mapeamos las tuplas resultantes a la estructura de la clase Libro de tu WebServer
            val librosFiltrados = librosFiltradosTuplas.map { case (titulo, autor, cat, paginas) =>
              Libro(titulo, autor, cat, paginas)
            }
            
            val body = jsonLibros(librosFiltrados).getBytes(StandardCharsets.UTF_8)
            send(exchange, 200, "application/json; charset=utf-8", body)

          case "/api/ordenar" =>
            val direccion = params.getOrElse("dir", "asc")
            val ascendente = direccion == "asc"

            val librosOrdenadosTuplas =
              Funciones.ordenarPorPaginas(Datos.biblioteca, ascendente)

            val librosOrdenados = librosOrdenadosTuplas.map { case (titulo, autor, cat, paginas) =>
              Libro(titulo, autor, cat, paginas)
            }

            val body = jsonLibros(librosOrdenados).getBytes(StandardCharsets.UTF_8)
            send(exchange, 200, "application/json; charset=utf-8", body)

          case "/api/recomendaciones" =>
            try {
              val respuestas = PrologService.recomendar(Datos.biblioteca)
              val recomendaciones = respuestas.flatMap { case (id, motivo) =>
                Datos.biblioteca.lift(id - 1).map { libro =>
                  val (titulo, autor, categoria, paginas) = libro
                  val motivoTexto = motivo match {
                    case "lectura_breve" => "Lectura breve (250 páginas o menos)"
                    case "fantasia" => "Aventura de fantasía"
                    case "clasico" => "Clásico para explorar"
                  }
                  s"{\"titulo\":${jsonString(titulo)},\"autor\":${jsonString(autor)}," +
                    s"\"categoria\":${jsonString(categoria)},\"paginas\":$paginas," +
                    s"\"motivo\":${jsonString(motivoTexto)}}"
                }
              }
              val body = recomendaciones.mkString("[", ",", "]").getBytes(StandardCharsets.UTF_8)
              send(exchange, 200, "application/json; charset=utf-8", body)
            } catch {
              case error: IllegalStateException =>
                val body = s"{\"error\":${jsonString(error.getMessage)}}".getBytes(StandardCharsets.UTF_8)
                send(exchange, 503, "application/json; charset=utf-8", body)
            }

          case "/api/relacionados" =>
            try {
              val id = params.get("id").flatMap(_.toIntOption).getOrElse(0)
              if (id < 1 || id > Datos.biblioteca.length) {
                val body = "{\"error\":\"Libro no válido\"}".getBytes(StandardCharsets.UTF_8)
                send(exchange, 400, "application/json; charset=utf-8", body)
              } else {
                val respuestas = PrologService.relacionados(Datos.biblioteca, id)
                // Un libro puede cumplir varias reglas: se agrupan sus motivos.
                val orden = respuestas.map(_._1).distinct
                val items = orden.flatMap { otro =>
                  Datos.biblioteca.lift(otro - 1).map { case (titulo, autor, categoria, paginas) =>
                    val motivos = respuestas.collect { case (`otro`, m) => m }.map {
                      case "mismo_autor" => "Mismo autor"
                      case "misma_categoria" => "Misma categoría"
                      case otroMotivo => otroMotivo
                    }
                    s"{\"titulo\":${jsonString(titulo)},\"autor\":${jsonString(autor)}," +
                      s"\"categoria\":${jsonString(categoria)},\"paginas\":$paginas," +
                      s"\"motivos\":${motivos.map(jsonString).mkString("[", ",", "]")}}"
                  }
                }
                val body = items.mkString("[", ",", "]").getBytes(StandardCharsets.UTF_8)
                send(exchange, 200, "application/json; charset=utf-8", body)
              }
            } catch {
              case error: IllegalStateException =>
                val body = s"{\"error\":${jsonString(error.getMessage)}}".getBytes(StandardCharsets.UTF_8)
                send(exchange, 503, "application/json; charset=utf-8", body)
            }

          case _ =>
            val notFound = "404 - Página no encontrada".getBytes(StandardCharsets.UTF_8)
            send(exchange, 404, "text/plain; charset=utf-8", notFound)
        }
      }
    })
  }

  private def jsonString(value: String): String = {
    val escaped = value.flatMap {
      case '"' => "\\\""
      case '\\' => "\\\\"
      case '\n' => "\\n"
      case '\r' => "\\r"
      case '\t' => "\\t"
      case char if char < ' ' => f"\\u${char.toInt}%04x"
      case char => char.toString
    }
    s"\"$escaped\""
  }

  def main(args: Array[String]): Unit = {
    val server = HttpServer.create(new InetSocketAddress(8080), 0)
    createContext(server)
    server.setExecutor(null)
    server.start()
    println("✅ Servidor web de Lumen Libris activo en http://localhost:8080")
    println("Presiona Ctrl+C para detenerlo.")
  }
}