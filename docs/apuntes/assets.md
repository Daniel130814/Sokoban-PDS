# Assets del juego

Tema elegido: **astronauta en el espacio**. Los assets son de terceros, todos CC0 (dominio público), descargados de
OpenGameArt y guardados en `assets-externos/` (ver `assets-externos/LEEME.md`). Las imágenes se escalaron a 48x48 y
a los hielos se les aplicó un tinte celeste; los sonidos son los WAV ya convertidos desde OGG.

| Archivo en `resources` | Uso | Origen (pack, autor, CC0) |
|---|---|---|
| `images/pared.png` | Pared (oscurecida) | Sokoban 100+ tiles, `Blocks/block_06`, Kenney |
| `images/piso.png`, `hielo.png` | Piso e hielo (tinte celeste) | Sokoban 100+ tiles, `Ground/ground_06`, Kenney |
| `images/destino.png` | Destino | Sokoban 100+ tiles, `Ground/ground_02`, Kenney |
| `images/cerrojo.png` | Cerrojo | Sokoban 100+ tiles, `Environment/environment_07`, Kenney |
| `images/muro_cerrado.png`, `muro_abierto.png` | Muros de cerrojo | Sokoban 100+ tiles, `Blocks/block_08` y `Ground/ground_03`, Kenney |
| `images/caja.png`, `caja_fragil.png`, `caja_llave.png` | Tipos de caja | Sokoban 100+ tiles, `Crates/crate_02`, `crate_01`, `crate_04`, Kenney |
| `images/jugador.png` | Jugador | Top Down Astronaut, cuadro `0`, Technopeasant (basado en Magnesus) |
| `sounds/paso.wav` | Paso | Interface Sounds `click_001`, Kenney |
| `sounds/empuje.wav` | Empuje | Sci-Fi Sounds `impactMetal_002`, Kenney |
| `sounds/caja_rota.wav` | Rotura | Sci-Fi Sounds `explosionCrunch_000`, Kenney |
| `sounds/cerrojo.wav` | Cerrojo | Sci-Fi Sounds `doorOpen_000`, Kenney |
| `sounds/deshacer.wav` | Deshacer | Interface Sounds `back_001`, Kenney |
| `sounds/nivel_completado.wav` | Victoria | Interface Sounds `confirmation_001`, Kenney |

Los assets generados por código de la versión anterior siguen disponibles en el historial de git
(`tools/GeneradorDeAssets.java`; si se vuelve a ejecutar, pisa estos archivos).

