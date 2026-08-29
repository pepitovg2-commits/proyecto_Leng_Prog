package S02_S03_Funcional

import javax.swing._
import java.awt._
import java.awt.event.ActionEvent

object Main {
  def main(args: Array[String]): Unit = {
    import Datos._
    import Funciones._
    import Lambdas._

    // Función PURA para formatear un libro como String
    def formatearLibro(libro: (String, String, String, Int)): String = {
      s"📖 ${libro._1} | Autor: ${libro._2} | ${libro._3} | ${libro._4} págs."
    }

    // --- CONFIGURACIÓN DE LA VENTANA (JFrame) ---
    val frame = new JFrame("BiblioScala - Sistema Interactivo")
    frame.setSize(700, 500)
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.setLocationRelativeTo(null) // Centrar en pantalla
    frame.setLayout(new BorderLayout(10, 10)) // Diseño con bordes

    // --- PANEL SUPERIOR (JPanel) para la búsqueda ---
    val panelSuperior = new JPanel()
    panelSuperior.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10))
    
    val labelBusqueda = new JLabel("Buscar por Autor o Categoría:")
    val campoBusqueda = new JTextField(25) // Campo de texto interactivo
    val botonBuscar = new JButton("🔍 Buscar")
    
    panelSuperior.add(labelBusqueda)
    panelSuperior.add(campoBusqueda)
    panelSuperior.add(botonBuscar)

    // --- ÁREA CENTRAL (JTextArea con Scroll) para los resultados ---
    val areaResultados = new JTextArea()
    areaResultados.setEditable(false)
    areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 14))
    areaResultados.setMargin(new Insets(15, 15, 15, 15))
    
    val scrollPane = new JScrollPane(areaResultados)
    scrollPane.setBorder(BorderFactory.createTitledBorder("Resultados de la Búsqueda"))

    // --- LÓGICA FUNCIONAL ASOCIADA AL BOTÓN ---
    // Cuando se hace clic en el botón, ejecutamos una composición de funciones
    botonBuscar.addActionListener((e: ActionEvent) => {
      val texto = campoBusqueda.getText().trim().toLowerCase()
      
      if (texto.isEmpty) {
        areaResultados.setText("⚠️ Por favor, escribe un autor o categoría para buscar.")
      } else {
        // COMPOSICIÓN DE FUNCIONES (Sesión 3): filter + map
        val resultados = Datos.biblioteca
          .filter(libro => 
            libro._2.toLowerCase().contains(texto) || // Busca en el autor
            libro._3.toLowerCase().contains(texto)    // O busca en la categoría
          )
          .map(formatearLibro)
        
        if (resultados.isEmpty) {
          areaResultados.setText(s"❌ No se encontraron libros que coincidan con: '$texto'")
        } else {
          val reporte = s"✅ Se encontraron ${resultados.length} libro(s) para '$texto':\n\n" + 
                        resultados.mkString("\n")
          areaResultados.setText(reporte)
        }
      }
    })

    // --- ENSAMBLAR LA VENTANA ---
    frame.add(panelSuperior, BorderLayout.NORTH)
    frame.add(scrollPane, BorderLayout.CENTER)

    // Mostrar mensaje inicial
    areaResultados.setText("👋 ¡Bienvenido a BiblioScala!\n\nEscribe un autor (ej: 'Cervantes', 'Tolkien')\no una categoría (ej: 'Distopía', 'Clásico')\ny presiona el botón 'Buscar'.")

    // --- MOSTRAR LA VENTANA ---
    frame.setVisible(true)
    println("✅ Ventana gráfica interactiva iniciada correctamente.")
  }
}