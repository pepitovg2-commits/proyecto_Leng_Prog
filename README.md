# Lumen Libris

Proyecto desarrollado en Scala para demostrar conceptos de programación funcional y una interfaz web moderna para gestionar una biblioteca.

## Tecnologías

- Scala 3
- SBT
- HTML
- CSS
- JavaScript
- Java HTTP Server

## Requisitos

- Java JDK 17+
- SBT

## Ejecutar la aplicación

```powershell
cd "C:\Users\JoseVale\Downloads\BiblioScala\BiblioScala"
sbt "runMain S02_S03_Funcional.WebServer"
```

Luego abrir en el navegador:

```text
http://localhost:8080
```

## Funcionalidades

- Catálogo de libros
- Búsqueda por autor, título o categoría
- Estadísticas de la colección
- Diseño responsivo
- Navegación tipo vista por secciones

## Estructura del proyecto

```text
src/
  main/
    resources/
      public/
        app.js
        index.html
        styles.css
    scala/
      S02_S03_Funcional/
        Datos.scala
        Funciones.scala
        Lambdas.scala
        Main.scala
        WebServer.scala
build.sbt
```

## Descripción del ejemplo

Este proyecto representa un sistema de biblioteca simple donde se aplican:

- listas inmutables
- funciones
- lambdas
- filtros y transformaciones
- lógica funcional para buscar y analizar datos
