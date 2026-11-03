import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        GestorTareas gestor = new GestorTareas();

        try {

            // Registrar tareas
            gestor.registrarTarea(1, "Taller de pilas", "Resolver ejercicios",LocalDate.of(2026, 11, 10), 15, 120,
                    "EDA", "Estructuras de Datos");

            gestor.registrarTarea(2, "Parcial de calculo", "Estudiar derivadas",LocalDate.of(2026, 11, 5), 25, 180,
                    "CAL", "Calculo");

            gestor.registrarTarea(3, "Trabajo de finanzas", "Analizar estados financieros",LocalDate.of(2026, 11, 8), 30, 150,
                    "FIN", "Finanzas");

            gestor.registrarTarea(4, "Quiz de estructuras", "Estudiar colas",LocalDate.of(2026, 11, 5), 10, 60,
                    "EDA", "Estructuras de Datos");

            gestor.registrarTarea(5, "Proyecto final", "Terminar proyecto",LocalDate.of(2026, 11, 5), 25, 240,
                    "EDA", "Estructuras de Datos");

            // Tarea con misma fecha y peso que la tarea 5,
            // pero con mayor tiempo estimado
            gestor.registrarTarea(6, "Proyecto de calculo", "Resolver proyecto",
                    LocalDate.of(2026, 11, 5), 25, 300,
                    "CAL", "Calculo");

            // Tarea con los mismos criterios que la tarea 6
            // Se desempata por orden de llegada
            gestor.registrarTarea(7, "Informe de calculo", "Preparar informe",
                    LocalDate.of(2026, 11, 5), 25, 300,
                    "CAL", "Calculo");

            // Consultar la siguiente tarea
            System.out.println("===== SIGUIENTE TAREA =====");

            Tarea siguiente = gestor.consultarSiguiente();

            System.out.println("ID: " + siguiente.getId());
            System.out.println("Titulo: " + siguiente.getTitulo());
            System.out.println("Fecha: " + siguiente.getFechaEntrega());
            System.out.println("Peso: " + siguiente.getPesoNota() + "%");
            System.out.println("Tiempo: " + siguiente.getTiempoEstimadoMin() + " minutos");

            // Mostrar tareas pendientes
            System.out.println("\n===== TAREAS PENDIENTES =====");

            List<Tarea> pendientes = gestor.obtenerPendientes();

            for (int i = 0; i < pendientes.size(); i++) {

                Tarea tarea = pendientes.get(i);

                System.out.println(
                        tarea.getId() + " - "
                        + tarea.getTitulo() + " - "
                        + tarea.getFechaEntrega() + " - "
                        + tarea.getPesoNota() + "% - "
                        + tarea.getTiempoEstimadoMin() + " min"
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

            // Verificar que no se pueda repetir un ID
            System.out.println("\n===== VALIDACION DE ID DUPLICADO =====");

            try {

                gestor.registrarTarea(1, "Tarea repetida", "Prueba de ID",
                        LocalDate.of(2026, 11, 12), 10, 60,
                        "EDA", "Estructuras de Datos");

            } catch (EEntradaInvalida e) {

                System.out.println("Error detectado: " + e.getMessage());
            }

            // Mostrar tareas pendientes actualizadas
            System.out.println("\n===== PENDIENTES ACTUALIZADAS =====");

            pendientes = gestor.obtenerPendientes();

            for (int i = 0; i < pendientes.size(); i++) {

                Tarea tarea = pendientes.get(i);

                System.out.println(
                        tarea.getId() + " - "
                        + tarea.getTitulo() + " - "
                        + tarea.getFechaEntrega() + " - "
                        + tarea.getPesoNota() + "% - "
                        + tarea.getTiempoEstimadoMin() + " min"
                );
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

            // Completar todas las tareas pendientes
            System.out.println("\n===== COMPLETAR TODAS LAS TAREAS =====");

            while (!gestor.obtenerPendientes().isEmpty()) {

                Tarea tarea = gestor.completarSiguiente();

                System.out.println("Completada: " + tarea.getTitulo());
            }

            // Verificar el funcionamiento con la cola vacia
            System.out.println("\n===== PRUEBA DE COLA VACIA =====");

            try {

                gestor.consultarSiguiente();

            } catch (EColaVacia e) {

                System.out.println("Error detectado: " + e.getMessage());
            }

        } catch (EFueraRango | EColaVacia | EEntradaInvalida e) {

            System.out.println("Error: " + e.getMessage());
        }
    }
}
