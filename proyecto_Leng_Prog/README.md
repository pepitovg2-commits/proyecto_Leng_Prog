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
- SWI-Prolog (para las recomendaciones lógicas; debe estar en `PATH`)

## Ejecutar la aplicación

```powershell
cd "C:\Users\JoseVale\Downloads\BiblioScala\BiblioScala"
sbt "runMain S02_S03_Funcional.WebServer"
```

Luego abrir en el navegador:

```text
http://localhost:8080
```

Si `swipl` no está en `PATH`, configura `SWIPL_PATH` con la ruta completa al ejecutable de SWI-Prolog antes de iniciar SBT.

## Funcionalidades

- Catálogo de libros
- Búsqueda por autor, título o categoría
- Estadísticas de la colección
- Diseño responsivo
- Navegación tipo vista por secciones
- Recomendaciones basadas en reglas Prolog desde el catálogo
- Libros relacionados (Prolog infiere libros con el mismo autor o categoría)

## Integración con Prolog

La interfaz consulta `/api/recomendaciones`. Scala genera hechos `libro/5` a partir de `Datos.biblioteca`, ejecuta las reglas de `src/main/resources/prolog/biblioteca.pl` con SWI-Prolog y transforma cada solución en JSON. Las reglas muestran unificación y resolución; `findall/3` recoge las respuestas y la búsqueda puede devolver más de una solución por libro.

Los comentarios de `biblioteca.pl` y `PrologService.scala` identifican los términos, átomos, constantes, predicados, reglas, listas, control de ejecución e indeterminismo que aparecen en esta integración.

### Libros relacionados (`/api/relacionados?id=N`)

Regla análoga a `hermano/2` del caso de la familia: `mismo_autor/2` y `misma_categoria/2` usan unificación (la variable `Autor` o `Categoria` se enlaza en ambos `libro/5`) y `A \= B` para no relacionar un libro consigo mismo; `relacionado/3` se apoya en ambas reglas. En la web se elige un libro y se muestra la consulta `?- relacionado(Id, X, Motivo).` con las soluciones que Prolog encuentra por backtracking. Ejemplo: `relacionado(1, X, M)` devuelve *Don Quijote* (mismo autor y misma categoría) y *La Metamorfosis* (misma categoría).

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
