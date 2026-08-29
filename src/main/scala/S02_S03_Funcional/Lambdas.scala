package S02_S03_Funcional

// =====================================================
// LAMBDAS.scala
// Proyecto: BiblioScala
// =====================================================
// TEMAS APLICADOS (Sesión 3):
// ✅ Funciones Anónimas (Lambdas) con sintaxis =>
// ✅ Composición de funciones en listas (map, filter, reduce)
// =====================================================

object Lambdas {

  // --------------------------------------------------
  // TEMA SESIÓN 3: Función ANÓNIMA (Lambda)
  // Verifica si un libro es "corto" (menos de 200 páginas)
  // --------------------------------------------------
  val esLibroCorto: ((String, String, String, Int)) => Boolean = 
    libro => libro._4 < 200

  // --------------------------------------------------
  // TEMA SESIÓN 3: Función ANÓNIMA para formatear
  // --------------------------------------------------
  val formatearLibro: ((String, String, String, Int)) => String = 
    libro => s"[${libro._3}] ${libro._1} por ${libro._2}"

  // --------------------------------------------------
  // TEMA SESIÓN 3: COMPOSICIÓN de funciones en listas
  // Filtra y transforma en un solo encadenamiento
  // --------------------------------------------------
  def obtenerReporteLibrosCortos(
    libros: List[(String, String, String, Int)]
  ): List[String] = {
    libros
      .filter(esLibroCorto)          // 1. Filtra con la lambda
      .map(formatearLibro)           // 2. Transforma con la lambda
  }
}