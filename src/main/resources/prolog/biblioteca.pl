% Programa logico de recomendaciones para Lumen Libris.
% Cada hecho libro/5 es un termino compuesto: libro(Id, Titulo, Autor, Categoria, Paginas).
% Los textos entre comillas son atomos; Id y Paginas son constantes numericas.
% libro/5 y recomendacion/2 son predicados que describen hechos y relaciones.
% Los hechos libro/5 no estan en este archivo: Scala los genera desde Datos.biblioteca
% y los carga junto con estas reglas al ejecutar la consulta.

% Reglas: la cabeza expresa una recomendacion y el cuerpo sus condiciones.
% La unificacion enlaza Id, Categoria y Paginas con los valores de cada hecho;
% la resolucion intenta satisfacer el cuerpo usando estos hechos y reglas.
recomendacion(Id, lectura_breve) :-
    libro(Id, _, _, _, Paginas),
    Paginas =< 250.

recomendacion(Id, fantasia) :-
    libro(Id, _, _, 'Fantasía', _).

recomendacion(Id, clasico) :-
    libro(Id, _, _, 'Clásico', Paginas),
    Paginas >= 300.

% La conjuncion (,) controla el orden de ejecucion: primero se recorre libro/5,
% luego se comprueba la categoria o el limite de paginas.
% Scala envia a Prolog una consulta con findall/3 y member/2: Prolog recoge todas
% las respuestas en una lista y las imprime; luego Scala las lee y extrae.
% Prolog devuelve varios libros (indeterminismo); un mismo libro apareceria
% con dos motivos si cumpliera mas de una regla.

% Listas: se escriben [a, b, c]. La notacion [H|T] separa la cabeza H (primer
% elemento) de la cola T (el resto), y [] es la lista vacia.
% total_paginas(Ids, Total) suma las paginas de los libros cuyos ids estan en la lista.
% Caso base: una lista vacia suma 0 paginas; aqui termina la recursion.
total_paginas([], 0).
% Caso recursivo: se toma la cabeza Id, se buscan sus Paginas en libro/5,
% se resuelve la cola Resto y se suman ambos valores con is.
total_paginas([Id|Resto], Total) :-
    libro(Id, _, _, _, Paginas),
    total_paginas(Resto, TotalResto),
    Total is Paginas + TotalResto.

% ---------------------------------------------------------------------------
% Libros relacionados (analogo a hermano/2 y tio/2 del caso de la familia).
% Dos libros distintos estan relacionados si comparten autor o categoria.
% A \= B evita que un libro se relacione consigo mismo, igual que X \= Y en hermano/2.
mismo_autor(A, B) :-
    libro(A, _, Autor, _, _),
    libro(B, _, Autor, _, _),
    A \= B.

misma_categoria(A, B) :-
    libro(A, _, _, Categoria, _),
    libro(B, _, _, Categoria, _),
    A \= B.

% relacionado(A, B, Motivo): regla que se apoya en otras dos reglas (inferencia en
% cadena). Con la consulta relacionado(3, X, Motivo), Prolog unifica A con 3 y
% busca por backtracking todos los X y Motivo que satisfacen alguna clausula.
relacionado(A, B, mismo_autor)     :- mismo_autor(A, B).
relacionado(A, B, misma_categoria) :- misma_categoria(A, B).
