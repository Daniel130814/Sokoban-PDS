package sokoban.audio;

public enum Efecto {
    PASO("paso"),
    EMPUJE("empuje"),
    CAJA_ROTA("caja_rota"),
    CERROJO("cerrojo"),
    DESHACER("deshacer"),
    NIVEL_COMPLETADO("nivel_completado");

    private final String archivo;

    Efecto(String archivo) {
        this.archivo = archivo;
    }

    public String recurso() {
        return "/sounds/" + archivo + ".wav";
    }
}
