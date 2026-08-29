error id: file:///C:/Users/Heiner/Desktop/BiblioScala/src/main/scala/S02_S03_Funcional/Main.scala:`<none>`.
file:///C:/Users/Heiner/Desktop/BiblioScala/src/main/scala/S02_S03_Funcional/Main.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -javax/swing/JFrame.EXIT_ON_CLOSE.
	 -javax/swing/JFrame.EXIT_ON_CLOSE#
	 -javax/swing/JFrame.EXIT_ON_CLOSE().
	 -java/awt/JFrame.EXIT_ON_CLOSE.
	 -java/awt/JFrame.EXIT_ON_CLOSE#
	 -java/awt/JFrame.EXIT_ON_CLOSE().
	 -Datos.JFrame.EXIT_ON_CLOSE.
	 -Datos.JFrame.EXIT_ON_CLOSE#
	 -Datos.JFrame.EXIT_ON_CLOSE().
	 -Funciones.JFrame.EXIT_ON_CLOSE.
	 -Funciones.JFrame.EXIT_ON_CLOSE#
	 -Funciones.JFrame.EXIT_ON_CLOSE().
	 -Lambdas.JFrame.EXIT_ON_CLOSE.
	 -Lambdas.JFrame.EXIT_ON_CLOSE#
	 -Lambdas.JFrame.EXIT_ON_CLOSE().
	 -JFrame.EXIT_ON_CLOSE.
	 -JFrame.EXIT_ON_CLOSE#
	 -JFrame.EXIT_ON_CLOSE().
	 -scala/Predef.JFrame.EXIT_ON_CLOSE.
	 -scala/Predef.JFrame.EXIT_ON_CLOSE#
	 -scala/Predef.JFrame.EXIT_ON_CLOSE().
offset: 2464
uri: file:///C:/Users/Heiner/Desktop/BiblioScala/src/main/scala/S02_S03_Funcional/Main.scala
text:
```scala
package S02_S03_Funcional

// Importamos las librerías gráficas de Java (Swing)
import javax.swing._
import java.awt._

object Main {
  def main(args: Array[String]): Unit = {
    
    // ---------------------------------------------------------
    // PARTE 1: LÓGICA FUNCIONAL (Lo que evalúa el profesor)
    // Aquí aplicamos val, def, map, filter, reduce y lambdas
    // ---------------------------------------------------------
    import Datos._
    import Funciones._
    import Lambdas._

    // 1. Obtener el catálogo completo formateado
    val catalogo = Datos.biblioteca.map(Datos.imprimirLibro).mkString("\n")

    // 2. Filtrar Distopías
    val distopias = Funciones.filtrarPorCategoria(Datos.biblioteca, "Distopía")
      .map(Datos.imprimirLibro).mkString("\n")

    // 3. Libros cortos usando Lambdas
    val librosCortos = Lambdas.obtenerReporteLibrosCortos(Datos.biblioteca)
      .mkString("\n")

    // 4. Estadísticas
    val promedio = Funciones.promedioPaginas(Datos.biblioteca)

    // ---------------------------------------------------------
    // PARTE 2: INTERFAZ GRÁFICA (La ventana visual)
    // Construimos el texto que se mostrará en la ventana
    // ---------------------------------------------------------
    val reporte = new StringBuilder()
    
    reporte.append("══════════════════════════════════════════\n")
    reporte.append("       BIBLIOSCALA - REPORTE GENERAL      \n")
    reporte.append("══════════════════════════════════════════\n\n")
    
    reporte.append("[1] CATALOGO COMPLETO:\n")
    reporte.append(catalogo).append("\n\n")
    
    reporte.append("[2] LIBROS DE CATEGORIA 'DISTOPIA':\n")
    reporte.append(distopias).append("\n\n")
    
    reporte.append("[3] LIBROS CORTOS (Menos de 200 pags - LAMBDAS):\n")
    reporte.append(librosCortos).append("\n\n")
    
    reporte.append("══════════════════════════════════════════\n")
    reporte.append(f"[4] ESTADISTICAS:\n")
    reporte.append(f" -> Promedio de paginas: $promedio%.2f\n")
    reporte.append("══════════════════════════════════════════\n")

    // ---------------------------------------------------------
    // PARTE 3: CREAR Y MOSTRAR LA VENTANA (Java Swing)
    // ---------------------------------------------------------
    // Creamos la ventana principal
    val frame = new JFrame("BiblioScala - Sistema de Gestion")
    frame.setSize(650, 600) // Tamaño de la ventana
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE@@) // Para que cierre al dar X
    frame.setLocationRelativeTo(null) // Para que salga en el centro de la pantalla

    // Creamos un área de texto para mostrar el reporte
    val textArea = new JTextArea(reporte.toString())
    textArea.setEditable(false) // El usuario no puede editar el texto
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 14)) // Fuente tipo consola
    textArea.setMargin(new Insets(15, 15, 15, 15)) // Margen interno

    // Agregamos una barra de desplazamiento por si el texto es muy largo
    val scrollPane = new JScrollPane(textArea)
    frame.add(scrollPane)

    // ¡Mostramos la ventana!
    frame.setVisible(true)
    
    println("✅ Ventana gráfica iniciada correctamente.")
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.