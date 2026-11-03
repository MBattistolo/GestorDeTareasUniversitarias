# Gestor de Tareas Universitarias

Proyecto de Estructuras de Datos que permite registrar tareas universitarias, consultar cuál realizar primero y llevar un historial de las tareas completadas. Utiliza una **cola de prioridad (`PriorityQueue`)** para organizar las entregas según su fecha, peso en la nota, tiempo estimado y orden de llegada.

Incluye una interfaz gráfica en **Java Swing**, una demostración por consola y un simulador que compara el costo de insertar y extraer tareas usando una cola de prioridad, una lista ordenada y una lista desordenada.

## Idea central

Las tareas universitarias no siempre deben resolverse en el orden en que se registran. Una entrega próxima puede requerir atención antes que una tarea registrada previamente; si ambas tienen la misma fecha, conviene considerar su peso en la nota y el tiempo necesario para realizarlas.

La clase `EntradaCola` implementa `Comparable<EntradaCola>` y define los siguientes criterios **en orden de importancia**:

| Orden | Criterio | Qué tarea tiene prioridad |
| --- | --- | --- |
| 1 | `fechaEntrega` | La que tenga la fecha de entrega más temprana. |
| 2 | `pesoNota` | Si las fechas coinciden, la que tenga mayor porcentaje en la nota. |
| 3 | `tiempoEstimadoMin` | Si coinciden fecha y peso, la que requiera más minutos de trabajo. |
| 4 | `ordenLlegada` | Si todos los criterios anteriores coinciden, la que se haya registrado primero. |

**Los criterios se aplican como desempates, no como una suma de puntajes.** Por ejemplo, una tarea con una fecha posterior no supera a otra con una fecha anterior, aunque tenga mayor peso en la nota.

`GestorTareas` almacena las tareas pendientes en una `PriorityQueue<EntradaCola>`. Esta estructura mantiene en la cabeza la entrada que resulta menor según `compareTo`, es decir, la de mayor prioridad académica según nuestras reglas.

Para evaluar alternativas, `Simulacion` compara tres estructuras usando el mismo criterio de prioridad:

| Estructura | Cómo se agregan las tareas | Cómo se obtiene la siguiente |
| --- | --- | --- |
| `PriorityQueue` | Se insertan con `offer`, manteniendo la propiedad del heap. | Se extrae la más prioritaria con `poll`. |
| Lista ordenada (`ArrayList`) | Se busca la posición correspondiente y se inserta en orden. | Se elimina el primer elemento con `remove(0)`. |
| Lista desordenada (`ArrayList`) | Se agregan al final con `add`. | Se recorre la lista para encontrar la más prioritaria y eliminarla. |

Las tres alternativas pueden seleccionar las tareas por la misma prioridad. La diferencia está en el trabajo que requiere mantener o encontrar ese orden. La aplicación utiliza `PriorityQueue`; las listas se emplean únicamente en la simulación.

## Funcionalidades

- Registrar tareas con ID, título, descripción, fecha de entrega, peso de la nota, tiempo estimado y asignatura.
- Consultar la siguiente tarea sin eliminarla.
- Mostrar las tareas pendientes ordenadas por prioridad.
- Completar la tarea más prioritaria o una tarea específica mediante su ID.
- Consultar el historial de tareas completadas.
- Usar una interfaz gráfica con pestañas de pendientes, registro e historial.
- Validar desde el formulario los campos obligatorios, el formato de fecha, los valores numéricos y los IDs repetidos.

Para mostrar todas las tareas en orden, `obtenerPendientes()` crea una copia de la cola y extrae sus elementos con `poll`. **Recorrer directamente una `PriorityQueue` no garantiza un recorrido ordenado.**

## Simulador y métricas

`MainSimulacion` genera **20 tareas** de una asignatura y compara las tres estructuras mediante `Simulacion.medirTiempo()`:

- Usa `Random` con la **semilla fija `67`**.
- Genera fechas entre el 1 y el 30 de noviembre de 2026 y pesos entre 1 y 100.
- Asigna a todas las tareas un tiempo estimado de 60 minutos y un orden de llegada consecutivo.
- Entrega la misma lista de tareas a las tres alternativas.
- Para cada estructura, repite **10.000 veces** el ciclo de insertar las 20 tareas y extraerlas todas.
- Mide con `System.nanoTime()` y muestra el tiempo promedio por ciclo en milisegundos.

| Dato o métrica | Qué representa | Para qué sirve |
| --- | --- | --- |
| Tareas: 20 | Tamaño del conjunto usado en cada repetición. | Compara las estructuras con la misma carga. |
| Repeticiones: 10.000 | Cantidad de ciclos completos por estructura. | Calcula un promedio sobre varias ejecuciones. |
| Tiempo promedio (ms) | Tiempo total medido, convertido a milisegundos y dividido entre 10.000. | Compara el costo de insertar y extraer todo el conjunto. |

La generación de las tareas queda fuera del intervalo medido. Dentro del intervalo se incluye la creación de los contenedores y las operaciones de inserción y extracción. No se mide la interfaz gráfica ni la finalización de tareas por ID.

**Alcance de la prueba:** al usar el mismo tiempo estimado para todas las tareas, la simulación no permite evaluar el desempate por duración. Tampoco calcula métricas de espera, justicia ni verificaciones automáticas del orden de salida.

## Resultados

### Orden esperado en la demostración

`Main.java` registra cinco tareas. De acuerdo con sus datos y las reglas de `EntradaCola.compareTo()`, el orden inicial esperado es:

| Posición | ID | Tarea | Fecha de entrega | Peso | Tiempo estimado |
| --- | --- | --- | --- | --- | --- |
| 1 | 5 | Proyecto final | 2026-11-05 | 25 % | 240 min |
| 2 | 2 | Parcial de calculo | 2026-11-05 | 25 % | 180 min |
| 3 | 4 | Quiz de estructuras | 2026-11-05 | 10 % | 60 min |
| 4 | 3 | Trabajo de finanzas | 2026-11-08 | 30 % | 150 min |
| 5 | 1 | Taller de pilas | 2026-11-10 | 15 % | 120 min |

El proyecto final supera al parcial porque ambos tienen la misma fecha y peso, pero el proyecto requiere más tiempo. El trabajo de finanzas, aunque pesa un 30 %, queda después de las tareas del 5 de noviembre porque su entrega es posterior.

La demostración completa primero el proyecto final y luego el trabajo de finanzas por su ID. Al terminar, quedan pendientes los IDs **2, 4 y 1**, en ese orden, y el historial contiene los IDs **5 y 3**.

### Comparación de eficiencia

Los tiempos concretos se obtienen ejecutando `MainSimulacion`; no se incluyen valores de referencia medidos en este README. El costo teórico de las operaciones implementadas es:

| Estructura | Insertar una tarea | Extraer la más prioritaria | Insertar y extraer las `n` tareas |
| --- | --- | --- | --- |
| `PriorityQueue` | O(log n) amortizado | O(log n) | O(n log n) |
| Lista ordenada | O(n), por búsqueda y desplazamientos | O(n), por desplazamientos en `remove(0)` | O(n²) |
| Lista desordenada | O(1) amortizado | O(n), por búsqueda y posible desplazamiento | O(n²) |

*`n` representa la cantidad de tareas. Los costos de inserción amortizados contemplan el crecimiento ocasional del arreglo interno.*

**Interpretación**

- **La cola de prioridad ofrece un mejor costo asintótico para insertar y extraer repetidamente por prioridad.** Además, consultar su cabeza mediante `peek` cuesta O(1).
- **La lista ordenada mantiene el orden a costa de las inserciones y los desplazamientos.** En esta implementación, eliminar el primer elemento también desplaza los restantes.
- **La lista desordenada simplifica la inserción, pero busca la mejor tarea en cada extracción.** Ese recorrido se repite hasta vaciarla.
- **Una muestra de 20 tareas no basta para demostrar escalabilidad.** Los tiempos pueden variar por la máquina, la JVM, el calentamiento y el recolector de basura; una estructura con mejor complejidad no necesariamente será la más rápida en cada ejecución pequeña.

La simulación es una comparación educativa, sin calentamiento separado ni un marco especializado de benchmarking. Su resultado debe interpretarse junto con la complejidad de cada alternativa.

## Cómo ejecutar

Se necesita un **JDK con `java` y `javac` disponibles**. El código usa bibliotecas estándar de Java y no requiere dependencias externas. La interfaz Swing necesita un entorno gráfico.

### Desde un IDE

1. Clonar o descargar el repositorio.
2. Abrirlo como proyecto Java en IntelliJ IDEA, Eclipse u otro IDE y configurar `src` como carpeta de código fuente.
3. Seleccionar la clase que se desea ejecutar:

| Clase | Qué ejecuta |
| --- | --- |
| `InterfazGestor.java` | La aplicación gráfica para registrar y gestionar tareas. |
| `Main.java` | La demostración por consola con cinco tareas, consultas y finalizaciones. |
| `MainSimulacion.java` | La comparación de tiempos entre las tres estructuras. |

**`Main` ejecuta la demostración por consola; la ventana se inicia desde el método `main` de `InterfazGestor`.**

### Desde la terminal

Clonar y entrar al proyecto:

```bash
git clone https://github.com/MBattistolo/GestorDeTareasUniversitarias.git
cd GestorDeTareasUniversitarias
```

Compilar desde la raíz del repositorio:

```bash
javac -encoding UTF-8 -d out src/Exceptions/*.java src/*.java
```

Ejecutar la interfaz gráfica:

```bash
java -cp out InterfazGestor
```

Ejecutar la demostración por consola:

```bash
java -cp out Main
```

Ejecutar la simulación:

```bash
java -cp out MainSimulacion
```

## Estructura del proyecto

```text
src/
├── Main.java                     # Demostración del gestor por consola
├── MainSimulacion.java           # Generación de tareas y comparación de estructuras
├── InterfazGestor.java           # Interfaz gráfica Swing y punto de entrada visual
├── GestorTareas.java             # Cola de pendientes e historial de completadas
├── EntradaCola.java              # Tarea, orden de llegada y reglas de comparación
├── Tarea.java                    # Datos de la tarea y cambio a estado completada
├── Asignatura.java               # Código y nombre de la asignatura
├── EstadoTarea.java              # PENDIENTE y COMPLETADA
├── Simulacion.java               # Medición de PriorityQueue y las dos listas
└── Exceptions/
    ├── EColaVacia.java           # Consulta o extracción sin tareas pendientes
    ├── EEntradaInvalida.java     # Finalización por un ID pendiente inexistente
    └── EFueraRango.java          # Peso de nota fuera del rango de 0 a 100
```

## Supuestos de alcance

- Los datos se guardan **en memoria** durante la ejecución; al cerrar el programa se pierden las tareas y el historial.
- El sistema es local y no incluye cuentas de usuario, sincronización, base de datos ni almacenamiento de archivos adjuntos.
- Registrar una tarea significa ingresar sus datos; no cargar documentos a una plataforma.
- La prioridad usa los cuatro criterios descritos. La asignatura, el título y la descripción no cambian el orden.
- Las fechas anteriores se ordenan primero, incluso si ya vencieron; no hay una regla especial para excluir tareas vencidas.
- El peso se expresa entre 0 y 100 para cada tarea; no se controla que los pesos de una asignatura sumen 100.
- Cada tarea pertenece a una asignatura y tiene únicamente dos estados: `PENDIENTE` o `COMPLETADA`.
- Completar una tarea la retira de la cola y la agrega al historial. Se permite completar por ID aunque no sea la más prioritaria.
- El formulario evita IDs repetidos entre pendientes e historial y valida los campos de entrada; `GestorTareas.registrarTarea()` no realiza por sí mismo todas esas comprobaciones.
- No hay opciones de edición, eliminación manual ni reapertura de tareas completadas en la interfaz actual.
