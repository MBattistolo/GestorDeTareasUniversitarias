import Exceptions.EFueraRango;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorTareas {
    ColaPrioridad cola = new ColaPrioridad();
    private int siguienteOrden = 0;
    List<Tarea> historial = new ArrayList<>();

    public void registrarTarea(int id, String titulo, String descripcion, LocalDate fechaEntrega, double pesoNota, int tiempoEstimadoMin, Asignatura asignatura) throws EFueraRango {
        Tarea t = new Tarea(id, titulo, descripcion, fechaEntrega, pesoNota, tiempoEstimadoMin, asignatura);
        cola.encolar(new EntradaCola(t, siguienteOrden));
        siguienteOrden++;
    }

    public Tarea consultarSiguiente() {
        EntradaCola entrada = cola.verPrimero();

        return entrada == null ? null : entrada.getTarea();
    }

    public Tarea completarSiguiente() {
        EntradaCola entrada = cola.desencolar();

        if (entrada == null)
            return null;

        Tarea tarea = entrada.getTarea();
        tarea.marcarCompletada();
        historial.add(tarea);

        return tarea;
    }

    public List<Tarea> obtenerPendientes() {
        List<Tarea> pendientes = new ArrayList<>();

        for (EntradaCola entrada : cola.listarEnOrden()) {
            if (entrada.getTarea().getEstado() == EstadoTarea.PENDIENTE)
                pendientes.add(entrada.getTarea());
        }
        return pendientes;
    }

    public List<Tarea> obtenerHistorial() {
        return new ArrayList<>(historial);
    }
}
