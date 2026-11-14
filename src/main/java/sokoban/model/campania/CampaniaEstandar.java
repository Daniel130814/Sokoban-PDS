package sokoban.model.campania;

import java.util.function.Function;
import sokoban.model.Nivel;

/**
 * Pure Fabrication: arma la campaña del juego (tutorial y desafíos) a partir de los recursos de nivel.
 * Recibe cómo cargar un nivel para no depender de la carga desde archivo y poder probarse sola.
 */
public final class CampaniaEstandar {

    private CampaniaEstandar() {
    }

    public static Mundo crear(Function<String, Nivel> cargador) {
        Mundo tutorial = new Mundo("Tutorial", "Aprendé las mecánicas una por una")
                .agregar(nivel(cargador, 1, "Tutorial 1", "Empujá la caja hasta el destino"))
                .agregar(nivel(cargador, 2, "Tutorial 2", "Hielo: la caja se desliza"))
                .agregar(nivel(cargador, 3, "Tutorial 3", "Caja llave, cerrojo y muro"))
                .agregar(nivel(cargador, 4, "Tutorial 4", "Caja frágil: se rompe al empujarla"));
        Mundo desafios = new Mundo("Desafíos", "Niveles para pensar con calma")
                .agregar(nivel(cargador, 5, "Nivel 5", "Tres cajas, tres destinos"))
                .agregar(nivel(cargador, 6, "Nivel 6", "Tres cajas en fila"))
                .agregar(nivel(cargador, 7, "Nivel 7", "Dos pasillos, tres cajas"))
                .agregar(nivel(cargador, 8, "Nivel 8", "Ocho cajas: el desafío final"));
        return new Mundo("Campaña", "Todos los niveles").agregar(tutorial).agregar(desafios);
    }

    private static NivelDeCampania nivel(Function<String, Nivel> cargador, int numero, String titulo, String detalle) {
        return new NivelDeCampania(titulo, detalle, cargador.apply("/levels/nivel" + numero + ".txt"));
    }
}
