package S02_S03_Funcional

// =====================================================
// DATOS.scala
// Proyecto: BiblioScala - Sistema de Gestión de Biblioteca
// =====================================================
// TEMAS APLICADOS (Sesión 2):
// ✅ val → Valores inmutables
// ✅ List → Listas inmutables
// ✅ def → Funciones definidas
// =====================================================

object Datos {

  // Base de datos de libros representada como tuplas
  // Estructura: (título, autor, categoría, número de páginas)
  val biblioteca: List[(String, String, String, Int)] = List(
    ("El Quijote",          "Cervantes",       "Clásico",         863),
    ("Cien años de soledad","García Márquez", "Realismo Mágico", 471),
    ("1984",                "George Orwell",   "Distopía",        328),
    ("El Principito",       "Saint-Exupéry",   "Infantil",         96),
    ("Don Quijote",         "Cervantes",       "Clásico",         900),
    ("Fahrenheit 451",      "Ray Bradbury",    "Distopía",        249),
    ("La Metamorfosis",     "Franz Kafka",     "Clásico",         120),
    ("El Hobbit",           "Tolkien",         "Fantasía",        310),
    ("Orgullo y Prejuicio", "Jane Austen",     "Romance",         432),
    ("Drácula",             "Bram Stoker",     "Terror",          418)
  )

  // Función que imprime un libro en formato legible
  def imprimirLibro(libro: (String, String, String, Int)): Unit = {
    println(s"📖 ${libro._1} | Autor: ${libro._2} | ${libro._3} | ${libro._4} págs.")
  }

  // Función que imprime una lista completa de libros
  def imprimirBiblioteca(libros: List[(String, String, String, Int)]): Unit = {
    libros.foreach(imprimirLibro)
  }

  // Función que muestra un mensaje de título/sección
  def mostrarTitulo(mensaje: String): Unit = {
    println("\n" + "=" * 50)
    println(mensaje)
    println("=" * 50)
  }
}