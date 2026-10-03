% Programa logico de recomendaciones para Lumen Libris.
% Cada hecho libro/5 es un termino compuesto: libro(Id, Titulo, Autor, Categoria, Paginas).
% Los textos entre comillas son atomos; Id y Paginas son constantes numericas.
% libro/5 y recomendacion/2 son predicados que describen hechos y relaciones.

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
% findall/3 y member/2 en Scala recogen y extraen todas las respuestas posibles:
% Prolog puede devolver varios libros, e incluso varios motivos para un libro.