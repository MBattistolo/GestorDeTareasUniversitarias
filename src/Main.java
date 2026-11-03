import Exceptions.EColaVacia;
import Exceptions.EEntradaInvalida;
import Exceptions.EFueraRango;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        GestorTareas gestor = new GestorTareas();

        Asignatura estructuras = new Asignatura("EDA", "Estructuras de Datos");
        Asignatura calculo = new Asignatura("CAL", "Calculo");
        Asignatura finanzas = new Asignatura("FIN", "Finanzas");

        try {

            // Registrar tareas
            gestor.registrarTarea(1, "Taller de pilas", "Resolver ejercicios",
                    LocalDate.of(2026, 11, 10), 15, 120, estructuras);

            gestor.registrarTarea(2, "Parcial de calculo", "Estudiar derivadas",
                    LocalDate.of(2026, 11, 5), 25, 180, calculo);

            gestor.registrarTarea(3, "Trabajo de finanzas", "Analizar estados financieros",
                    LocalDate.of(2026, 11, 8), 30, 150, finanzas);

            gestor.registrarTarea(4, "Quiz de estructuras", "Estudiar colas",
                    LocalDate.of(2026, 11, 5), 10, 60, estructuras);

            gestor.registrarTarea(5, "Proyecto final", "Terminar proyecto",
                    LocalDate.of(2026, 11, 5), 25, 240, estructuras);

            // Consultar la siguiente tarea
            System.out.println("===== SIGUIENTE TAREA =====");

            Tarea siguiente = gestor.consultarSiguiente();

            System.out.println("ID: " + siguiente.getId());
            System.out.println("Titulo: " + siguiente.getTitulo());
            System.out.println("Fecha: " + siguiente.getFechaEntrega());
            System.out.println("Peso: " + siguiente.getPesoNota() + "%");

            // Mostrar tareas pendientes
            System.out.println("\n===== TAREAS PENDIENTES =====");

            List<Tarea> pendientes = gestor.obtenerPendientes();

            for (int i = 0; i < pendientes.size(); i++) {

                Tarea tarea = pendientes.get(i);

                System.out.println(
                        tarea.getId() + " - "
                        + tarea.getTitulo() + " - "
                        + tarea.getFechaEntrega() + " - "
                        + tarea.getPesoNota() + "%"
                );
            }

            // Completar la siguiente tarea
            System.out.println("\n===== COMPLETAR SIGUIENTE =====");

            Tarea completada = gestor.completarSiguiente();

            System.out.println("Completada: " + completada.getTitulo());

            // Completar una tarea por ID
            System.out.println("\n===== COMPLETAR POR ID =====");

            Tarea completadaId = gestor.completarPorId(3);

            System.out.println("Completada: " + completadaId.getTitulo());

            // Mostrar tareas pendientes actualizadas
            System.out.println("\n===== PENDIENTES ACTUALIZADAS =====");

            pendientes = gestor.obtenerPendientes();

            for (int i = 0; i < pendientes.size(); i++) {

                Tarea tarea = pendientes.get(i);

                System.out.println(tarea.getId() + " - " + tarea.getTitulo());
            }

            // Mostrar historial
            System.out.println("\n===== HISTORIAL =====");

            List<Tarea> historial = gestor.obtenerHistorial();

            for (int i = 0; i < historial.size(); i++) {

                Tarea tarea = historial.get(i);

                System.out.println(
                        tarea.getId() + " - "
                        + tarea.getTitulo() + " - "
                        + tarea.getEstado()
                );
            }

        } catch (EFueraRango | EColaVacia | EEntradaInvalida e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
