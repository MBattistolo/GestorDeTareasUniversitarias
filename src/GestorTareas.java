import Exceptions.EColaVacia;
import Exceptions.EEntradaInvalida;
import Exceptions.EFueraRango;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class GestorTareas {

    // Cola que organiza las tareas por prioridad
    private final PriorityQueue<EntradaCola> cola;

    // Lleva el orden en que se registran las tareas
    private int siguienteOrden;

    // Guarda las tareas que ya se completaron
    private final List<Tarea> historial;
    
    // Guarda las asignaturas que se crean
    private final ArrayList<Asignatura> asignaturas;

    // Constructor
    public GestorTareas() {
        cola = new PriorityQueue<>();
        siguienteOrden = 0;
        historial = new ArrayList<>();
        asignaturas = new ArrayList<>();
    }


private Asignatura buscarOCrearAsignatura(String codigo, String nombre) {

    // Empezamos buscando desde la primera posición
    int i = 0;
    // Recorremos la lista mientras no encontremos la asignatura
    while (i < asignaturas.size() && !asignaturas.get(i).getCodigo().equalsIgnoreCase(codigo)) {
        i++;
    }
    // Si i está dentro de la lista, encontramos la asignatura
    if (i < asignaturas.size()) {
        return asignaturas.get(i);
    }
    // Si llegamos al final, la asignatura no existe
    Asignatura nueva = new Asignatura(codigo, nombre);

    // Guardamos la nueva asignatura
    asignaturas.add(nueva);

    // Devolvemos la asignatura creada
    return nueva;
}


    
    
public void registrarTarea(int id, String titulo, String descripcion,
        LocalDate fechaEntrega, double pesoNota, int tiempoEstimadoMin,
        String codigoAsignatura, String nombreAsignatura)
        throws EFueraRango, EEntradaInvalida {

    // Verificamos que el ID no exista entre las tareas pendientes
    for (EntradaCola entrada : cola) {
        if (entrada.getTarea().getId() == id) {
            throw new EEntradaInvalida("Ya existe una tarea con ese ID");
        }
    }
    // Verificamos que el ID no exista en el historial
    int i = 0;
    while (i < historial.size() && historial.get(i).getId() != id) {
        i++;
    }
    if (i < historial.size()) {
        throw new EEntradaInvalida("Ya existe una tarea con ese ID");
    }
    // Buscamos o creamos la asignatura
    Asignatura asignatura = buscarOCrearAsignatura(codigoAsignatura, nombreAsignatura);
    // Creamos la tarea
    Tarea tarea = new Tarea(id, titulo, descripcion, fechaEntrega,pesoNota, tiempoEstimadoMin, asignatura);
    // Creamos la entrada con su orden de llegada
    EntradaCola entrada = new EntradaCola(tarea, siguienteOrden);
            
    // Agregamos la tarea a la cola de prioridad
    cola.offer(entrada);
    // Incrementamos el orden de llegada
    siguienteOrden++;
}

 
    public Tarea consultarSiguiente() throws EColaVacia {

        // Obtiene la entrada con mayor prioridad
        EntradaCola entrada = cola.peek();

        // Si la cola está vacía, lanza una excepción
        if (entrada == null) {
            throw new EColaVacia("No hay tareas pendientes para consultar.");
        }

        // Devuelve la tarea de mayor prioridad
        return entrada.getTarea();
    }

 // Completa y elimina la tarea más prioritaria
    public Tarea completarSiguiente() throws EColaVacia {

        // Saca la entrada más prioritaria de la cola
        EntradaCola entrada = cola.poll();

        // Si la cola está vacía, lanza una excepción
        if (entrada == null) {
            throw new EColaVacia("No hay tareas pendientes para completar.");
        }

        // Obtiene la tarea y cambia su estado
        Tarea tarea = entrada.getTarea();
        tarea.marcarCompletada();

        // Guarda la tarea completada en el historial
        historial.add(tarea);

        return tarea;
    }

    // Devuelve las tareas pendientes ordenadas
    public List<Tarea> obtenerPendientes() {

        // Copia la cola para no modificar la original
        PriorityQueue<EntradaCola> copia = new PriorityQueue<>(cola);

        List<Tarea> pendientes = new ArrayList<>();

        // Extrae las tareas de la copia por prioridad
        while (!copia.isEmpty()) {

            EntradaCola entrada = copia.poll();

            if (entrada.getTarea().getEstado()== EstadoTarea.PENDIENTE) {

                pendientes.add(entrada.getTarea());
            }
        }

        return pendientes;
    }

    public Tarea completarPorId(int id) throws EEntradaInvalida {

        // Crea una copia de la cola original
        PriorityQueue<EntradaCola> copia = new PriorityQueue<>(cola);

        Tarea tareaEncontrada = null;

        // Recorre las entradas de la copia
        while (!copia.isEmpty() && tareaEncontrada == null) {

            // Extrae una entrada de la copia
            EntradaCola entrada = copia.poll();

            // Comprueba si es la tarea buscada
            if (entrada.getTarea().getId() == id) {

                // Elimina la entrada de la cola original
                cola.remove(entrada);

                // Marca la tarea como completada
                tareaEncontrada = entrada.getTarea();
                tareaEncontrada.marcarCompletada();

                // Guarda la tarea en el historial
                historial.add(tareaEncontrada);
            }
        }

        if (tareaEncontrada == null) {
            throw new EEntradaInvalida("No existe una tarea pendiente con ese ID.");
        }

        return tareaEncontrada;
    }

    // Devuelve el historial de tareas completadas
    public List<Tarea> obtenerHistorial() {
        return historial;
    }
}

  

