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
| `images/fondo_espacio.png` | Fondo de estrellas | Space Shooter Redux, `Backgrounds/black`, Kenney, CC0 |
| `images/fondo_menu.png` | Fondo del menú | Space Shooter Redux, `Backgrounds/darkPurple`, Kenney, CC0 |
| `images/boton.png` | Botones | Space Shooter Redux, `PNG/UI/buttonBlue`, Kenney, CC0 |
| `images/icono_nivel.png` | HUD: nivel | Space Shooter Redux, `PNG/UI/playerLife1_blue`, Kenney, CC0 |
| `images/icono_movimientos.png` | HUD: movimientos | Space Shooter Redux, `PNG/Power-ups/bolt_gold`, Kenney, CC0 |
| `images/icono_empujes.png` | HUD: empujes | Space Shooter Redux, `PNG/Power-ups/things_gold`, Kenney, CC0 |
| `images/icono_deshacer.png` | HUD: deshacer | Space Shooter Redux, `PNG/Power-ups/shield_silver`, Kenney, CC0 |
| `images/icono_puntaje.png` | HUD: puntaje | Space Shooter Redux, `PNG/Power-ups/star_gold`, Kenney, CC0 |
| `fonts/kenvector_future.ttf` | Fuente de títulos | Space Shooter Redux, `Bonus/kenvector_future`, Kenney, CC0 |
| `fonts/kenvector_future_thin.ttf` | Fuente del HUD y botones | Space Shooter Redux, `Bonus/kenvector_future_thin`, Kenney, CC0 |

Los assets generados por código de la versión anterior siguen disponibles en el historial de git
(`tools/GeneradorDeAssets.java`; si se vuelve a ejecutar, pisa estos archivos).
