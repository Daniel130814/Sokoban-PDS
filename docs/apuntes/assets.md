# Assets del juego

Todos los assets son **obras originales del proyecto**, generadas por código con
[`tools/GeneradorDeAssets.java`](../../tools/GeneradorDeAssets.java) y dedicadas al dominio público (CC0).
No se usó ningún recurso externo, así que no hay fuentes de terceros que citar.

> Pendiente de confirmar con el enunciado vigente: si se exige usar imágenes y sonidos de terceros de dominio público.
> Si se reemplazan, anotar acá la fuente, el autor y la licencia de cada archivo.

| Archivo | Uso | Origen |
|---|---|---|
| `images/pared.png`, `piso.png`, `destino.png`, `hielo.png` | Celdas del tablero | Generado por `GeneradorDeAssets` (CC0) |
| `images/cerrojo.png`, `muro_cerrado.png`, `muro_abierto.png` | Cerrojos y muros | Generado por `GeneradorDeAssets` (CC0) |
| `images/caja.png`, `caja_fragil.png`, `caja_llave.png` | Tipos de caja | Generado por `GeneradorDeAssets` (CC0) |
| `images/jugador.png` | Jugador | Generado por `GeneradorDeAssets` (CC0) |
| `sounds/paso.wav`, `empuje.wav`, `caja_rota.wav` | Movimiento, empuje y rotura | Sintetizado por `GeneradorDeAssets` (CC0) |
| `sounds/cerrojo.wav`, `deshacer.wav`, `nivel_completado.wav` | Cerrojo, undo y victoria | Sintetizado por `GeneradorDeAssets` (CC0) |

Para regenerarlos, desde la raíz del repositorio:

```bash
java tools/GeneradorDeAssets.java
```
