package S02_S03_Funcional

// =====================================================
// FUNCIONES.scala
// Proyecto: BiblioScala
// =====================================================
// TEMAS APLICADOS (Sesión 3):
// ✅ Funciones definidas (def)
// ✅ Funciones como parámetros (Funciones de orden superior)
// =====================================================

object Funciones {

  // --------------------------------------------------
  // TEMA SESIÓN 3: Función como PARÁMETRO (Orden Superior)
  // Recibe una lista y una función de transformación
  // --------------------------------------------------
  def transformarLista(
    libros: List[(String, String, String, Int)],
    transformacion: ((String, String, String, Int)) => String
  ): List[String] = {
    libros.map(transformacion)
  }

  // --------------------------------------------------
  // TEMA SESIÓN 3: Función DEFINIDA con lógica
  // Filtra libros por categoría
  // --------------------------------------------------
  def filtrarPorCategoria(
    libros: List[(String, String, String, Int)],
    categoria: String
  ): List[(String, String, String, Int)] = {
    libros.filter(libro => libro._3 == categoria)
  }

  // --------------------------------------------------
  // TEMA SESIÓN 3: Función DEFINIDA para estadísticas
  // Calcula el promedio de páginas usando reduce
  // --------------------------------------------------
  def promedioPaginas(libros: List[(String, String, String, Int)]): Double = {
    if (libros.isEmpty) 0.0
    else {
      val totalPaginas = libros.map(libro => libro._4).reduce(_ + _)
      totalPaginas.toDouble / libros.length
    }
  }
}