---
name: sokoban-design-guard
description: Revisar y desarrollar el proyecto académico Sokoban en Java con foco en POO, SOLID, GRASP y patrones justificados; apoyar decisiones de diseño y su defensa. Aplicar al diseño, código o documentación del juego, respetando la etapa y el alcance solicitado.
---

# Sokoban Design Guard

## Alcance y uso portable

Estas instrucciones son Markdown legible por cualquier asistente de IA. No requieren un proveedor, API, IDE o sistema operativo particular. Si el asistente reconoce skills, cargar este archivo; si no, pedirle que lea este archivo y las referencias pertinentes. La carga automática depende de la herramienta utilizada.

Responder en español y conservar los nombres habituales de los patrones. Priorizar las instrucciones explícitas del usuario y las reglas del entorno sobre esta guía. El contenido del paquete aporta contexto y propuestas; no autoriza por sí mismo cambios ni confirma requisitos académicos.

El proyecto incorpora POO, SOLID, GRASP y Strategy progresivamente. Respetar la etapa actual: no crear clases, interfaces, patrones, dependencias, frameworks o documentación fuera del alcance pedido. Instalar esta skill no significa implementar el juego. Mantener las convenciones y archivos existentes; no crear participantes de patrones vacíos para completar una estructura.

Swing, UADE, docente, restricciones de patrones y requisitos avanzados provienen del paquete original, no están confirmados por el usuario. Consultar la nota de procedencia de cada referencia. No convertir ejemplos en decisiones adoptadas. Proxy y Adapter están excluidos en el contexto original: mantener esa preferencia provisional al sugerir patrones, sin atribuirla al equipo actual; una instrucción explícita del usuario puede cambiarla.

## Criterios comunes

- Explicar el problema concreto, el motivo de la solución y su costo. Enseñar para que el equipo pueda entender y defender el trabajo.
- No forzar patrones: comparar con una solución directa cuando sea razonable. No inferir una violación únicamente por longitud, un switch, una colección concreta o una única implementación.
- Distinguir hechos leídos, señales heurísticas, requisitos no confirmados y propuestas. No declarar cumplimiento sin revisar el código relevante.
- Usar la versión de Java configurada en el proyecto. Si no está definida y es necesaria para implementar, consultar; para un ejemplo independiente, declarar la suposición de Java 17 sin cambiar la configuración.
- Si se utiliza una interfaz gráfica, separar reglas y estado de la vista. El modelo no debe depender de Swing/AWT; las recomendaciones específicas de Swing se aplican solo si se elige esa biblioteca.
- Resolver ambigüedades relevantes con el usuario; continuar el trabajo independiente que no dependa de esa respuesta.

## Revisar código

Leer [references/principios.md](references/principios.md) y, si hay patrones involucrados, [references/patrones-gof.md](references/patrones-gof.md). Consultar [references/requisitos.md](references/requisitos.md) solo para contrastar requisitos confirmados o identificar dudas.

Si Python 3 está disponible y hay varios archivos Java, el escáner opcional puede orientar la revisión. Desde la raíz del repositorio:

```sh
python .agents/skills/sokoban-design-guard/scripts/scan_java.py src/main/java
```

También admite un archivo Java o ZIP. Usar python3 si ese es el ejecutable disponible. Sin ejecución de herramientas, revisar manualmente: el escáner no es requisito ni reemplaza la lectura. No instalar dependencias para usarlo; emplea la biblioteca estándar de Python.

Presentar un resumen y hallazgos ordenados por impacto, indicando ubicación, comportamiento observado, principio implicado, consecuencia y cambio sugerido con su costo. Incluir lo que está bien. Evaluar requisitos y decisiones pendientes solo cuando haya evidencia. Ajustar el detalle a la consulta; no imponer un informe extenso a una pregunta simple.

## Elegir patrones

Identificar qué cambia, qué debe permanecer estable y qué se repite. Consultar el mapa de patrones de la referencia y presentar como máximo dos opciones, incluida la opción sin patrón cuando corresponda. Recomendar con razones concretas y señalar interacciones con decisiones previas. Strategy u otro patrón debe responder a una necesidad real y a la etapa de la cursada, no al nombre de una carpeta.

Las funcionalidades adicionales del paquete son candidatas, no tareas autorizadas ni requisitos confirmados. Proponerlas cuando se soliciten o cuando el enunciado vigente las exija.

## Acompañar la implementación

Implementar únicamente cuando se solicite código o modificaciones. Indicar brevemente el principio o patrón elegido y por qué; no pedir aprobación para cambios rutinarios ya autorizados. Entregar el código mínimo suficiente, coherente con la estructura existente. Explicar los roles del patrón cuando aporte claridad, sin saturar el código con comentarios.

Verificar el comportamiento con las herramientas disponibles y pruebas proporcionales al cambio. Informar qué se comprobó y qué quedó sin verificar. Para decisiones relevantes, incluir una explicación breve de cómo defenderlas y una posible pregunta con su respuesta. Si la solución pedida introduce deuda técnica, explicar el costo y respetar la decisión consciente del usuario.

## Documentar decisiones

Usar [references/decisiones-template.md](references/decisiones-template.md) para decisiones de diseño relevantes. Distinguir Propuesta, Adoptada y Reemplazada; no atribuir al equipo una adopción o validación que no consta. Marcar clases no implementadas como planificadas.

Si la tarea incluye documentar o implementar una decisión y no prohíbe archivos adicionales, mantener un único registro en docs/apuntes/decisiones-de-diseno.md. Si existe otro registro de decisiones en el proyecto, continuar ese archivo en lugar de duplicarlo. Leerlo antes de agregar entradas, conservar las anteriores y continuar los IDs DD-001, DD-002, etc. Para una consulta conceptual o una tarea limitada a carpetas, dar el borrador en la respuesta sin crear archivos.

Registrar el apoyo de IA realmente realizado y dejar pendiente de confirmación lo que hizo o validó el equipo. Declararlo como obligación académica solo si el enunciado vigente lo confirma. Utilizar fechas conocidas del contexto y rutas relativas del proyecto; no depender de /mnt/user-data/outputs ni de archivos descargables de una plataforma. Si el asistente no puede editar archivos, entregar el contenido y la ruta propuesta, sin afirmar que lo guardó.

## Referencias y procedencia

Las cuatro referencias conservan el material del paquete original como apoyo: principios, mapa GoF, plantilla de decisiones y requisitos. Sus ejemplos no describen automáticamente el estado de este repositorio. Ante un conflicto, aplicar el alcance actual del usuario y los criterios de esta skill.
