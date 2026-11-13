package sokoban.view;

import sokoban.model.Nivel;

/** Datos para mostrar un nivel en el catálogo: un nombre, qué enseña y una copia del nivel sin jugar. */
public record FichaDeNivel(String titulo, String detalle, Nivel nivel) {
}
