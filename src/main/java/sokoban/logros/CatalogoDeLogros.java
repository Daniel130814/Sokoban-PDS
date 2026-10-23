package sokoban.logros;

import java.util.List;

public final class CatalogoDeLogros {

    private CatalogoDeLogros() {
    }

    public static List<Logro> estandar() {
        return List.of(
                new Logro("Primer paso", "Completá un nivel", e -> e.nivelesCompletados() >= 1),
                new Logro("Sin red", "Completá un nivel sin usar deshacer", e -> e.nivelesSinDeshacer() >= 1),
                new Logro("Cerrajero", "Abrí un muro con una caja llave", e -> e.cerrojosActivados() >= 1),
                new Logro("Rompecajas", "Rompé una caja frágil", e -> e.cajasRotas() >= 1),
                new Logro("Empujador", "Empujá cajas 20 veces", e -> e.empujes() >= 20));
    }
}
