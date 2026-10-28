public class EntradaCola {
    private Tarea tarea;
    private int ordenLlegada;

    public EntradaCola(Tarea t, int orden) {
        tarea = t;
        ordenLlegada = orden;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public int getOrdenLlegada() {
        return ordenLlegada;
    }
}
