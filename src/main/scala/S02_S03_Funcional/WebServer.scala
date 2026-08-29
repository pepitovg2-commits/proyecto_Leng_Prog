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
            val body = jsonLibros(libros).getBytes(StandardCharsets.UTF_8)
            send(exchange, 200, "application/json; charset=utf-8", body)

          case "/api/buscar" =>
            val texto = params.getOrElse("q", "")
            val body = jsonLibros(buscarLibros(texto)).getBytes(StandardCharsets.UTF_8)
            send(exchange, 200, "application/json; charset=utf-8", body)

          case "/api/estadisticas" =>
            val totalLibros = libros.length
            val promedio = if (libros.isEmpty) 0.0 else libros.map(_.paginas).sum.toDouble / libros.length
            val categorias = libros.map(_.categoria).distinct.sorted
            val payload =
              s"{\"totalLibros\":$totalLibros,\"promedioPaginas\":$promedio,\"categorias\":[${categorias.map(c => s"\"$c\"").mkString(",")}]}"
            send(exchange, 200, "application/json; charset=utf-8", payload.getBytes(StandardCharsets.UTF_8))

          case _ =>
            val notFound = "404 - Página no encontrada".getBytes(StandardCharsets.UTF_8)
            send(exchange, 404, "text/plain; charset=utf-8", notFound)
        }
      }
    })
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
