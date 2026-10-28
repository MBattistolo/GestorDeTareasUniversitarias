import Exceptions.EFueraRango;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorTareas {
    ColaPrioridad cola = new ColaPrioridad();
    private int siguienteOrden = 0;
    List<Tarea> historial = new ArrayList<>();

    public void registrarTarea(int id, String titulo, String descripcion, LocalDate fechaEntrega, double pesoNota, Asignatura asignatura) throws EFueraRango {
        Tarea t = new Tarea(id, titulo, descripcion, fechaEntrega, pesoNota, asignatura);
        historial.add(t);
        cola.encolar(new EntradaCola(t, historial.size()));
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
        return obtenerPendientes(new ArrayList<>(), 0);
    }

    private List<Tarea> obtenerPendientes(List<Tarea> pendientes, int i) {
        if (i == cola.tamano())
            return pendientes;

        if (cola.listarEnOrden().get(i).getTarea().getEstado() == EstadoTarea.PENDIENTE)
            pendientes.add(cola.listarEnOrden().get(i).getTarea());

        return obtenerPendientes(pendientes, i + 1);
    }

    public List<Tarea> obtenerHistorial() {
        return new ArrayList<>(historial);
    }
}
