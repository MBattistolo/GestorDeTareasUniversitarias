public class EntradaCola implements Comparable<EntradaCola> {

    // Tarea que vamos a guardar en la cola
    private final Tarea tarea;

    // Orden en el que se registró la tarea
    private final int ordenLlegada;

    // Constructor
    public EntradaCola(Tarea tarea, int ordenLlegada) {
        this.tarea = tarea;
        this.ordenLlegada = ordenLlegada;
    }

    // Devuelve la tarea
    public Tarea getTarea() {
        return tarea;
    }

    // Devuelve el orden de registro
    public int getOrdenLlegada() {
        return ordenLlegada;
    }

    // Compara dos tareas para saber cuál tiene prioridad
    @Override
    public int compareTo(EntradaCola otra) {

        // Primero compara las fechas de entrega
        int fecha = tarea.getFechaEntrega().compareTo(otra.getTarea().getFechaEntrega());

        // Si las fechas son diferentes, gana la más próxima
        if (fecha != 0)
            return fecha;

        // Si las fechas son iguales, compara el peso de la nota
        int peso = Double.compare(otra.getTarea().getPesoNota(),tarea.getPesoNota());

        // Si los pesos son diferentes, gana el mayor
        if (peso != 0)
            return peso;

        // Si los pesos son iguales, compara el tiempo estimado en el que se realiza
        int tiempo = Integer.compare(otra.getTarea().getTiempoEstimadoMin(), tarea.getTiempoEstimadoMin());

        // Si los tiempos son diferentes, gana el mayor
        if (tiempo != 0)
            return tiempo;

        // Si las condiciones son iguales, gana la que se registró primero
        return Integer.compare(ordenLlegada,otra.getOrdenLlegada());
    }
}
