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

    // Constructor
    public GestorTareas() {
        cola = new PriorityQueue<>();
        siguienteOrden = 0;
        historial = new ArrayList<>();
    }
    
    public void registrarTarea(int id, String titulo,String descripcion, LocalDate fechaEntrega,double pesoNota,
    		int tiempoEstimadoMin,Asignatura asignatura) throws EFueraRango {

        // Crea la tarea con los datos recibidos
        Tarea tarea = new Tarea(id, titulo, descripcion,fechaEntrega, pesoNota, tiempoEstimadoMin, asignatura);

        // Guarda la tarea junto con su orden de llegada
        EntradaCola entrada = new EntradaCola(tarea, siguienteOrden);

        // Agrega la entrada a la cola de prioridad
        cola.offer(entrada);

        // Aumenta el orden para la siguiente tarea
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

