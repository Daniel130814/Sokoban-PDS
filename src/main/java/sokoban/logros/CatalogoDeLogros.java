package sokoban.logros;

import java.util.List;

public final class CatalogoDeLogros {

    private CatalogoDeLogros() {
    }

    public static List<Logro> estandar() {
        return List.of(
                Logro.porContador("Primer paso", "Completá un nivel", 1, EstadisticasDeJuego::nivelesCompletados),
                Logro.porContador("Sin red", "Completá un nivel sin usar deshacer", 1,
                        EstadisticasDeJuego::nivelesSinDeshacer),
                Logro.porContador("Cerrajero", "Abrí un muro con una caja llave", 1,
                        EstadisticasDeJuego::cerrojosActivados),
                Logro.porContador("Rompecajas", "Rompé una caja frágil", 1, EstadisticasDeJuego::cajasRotas),
                Logro.porContador("Empujador", "Empujá cajas 20 veces", 20, EstadisticasDeJuego::empujes));
    }
}
