import Exceptions.EFueraRango;

import java.time.LocalDate;

public class Tarea {
    private int id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaEntrega;
    private double pesoNota;
    private Asignatura asignatura;
    private EstadoTarea estado = EstadoTarea.PENDIENTE;

    public Tarea(int id, String titulo, String descripcion, LocalDate fechaEntrega, double pesoNota, Asignatura asignatura) throws EFueraRango {
        if (pesoNota < 0 || pesoNota > 100)
            throw new EFueraRango(0, 100);

        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
        this.pesoNota = pesoNota;
        this.asignatura = asignatura;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public double getPesoNota() {
        return pesoNota;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void marcarCompletada() {
        estado = EstadoTarea.COMPLETADA;
    }

    @Override
    public String toString() {
        return titulo + " | " + asignatura.getNombre() + " | entrega: "
                + fechaEntrega + " | peso: " + pesoNota + "%";
    }
}
