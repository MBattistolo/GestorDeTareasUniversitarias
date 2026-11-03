import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InterfazGestor extends JFrame {

    // Gestor que administra las tareas
    private GestorTareas gestor;

    // Campos de registro
    private JTextField campoId;
    private JTextField campoTitulo;
    private JTextField campoDescripcion;
    private JTextField campoFecha;
    private JTextField campoPeso;
    private JTextField campoTiempo;
    private JTextField campoCodigoAsignatura;
    private JTextField campoNombreAsignatura;

    // Tabla de tareas
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // Constructor
    public InterfazGestor(GestorTareas gestor) {

        this.gestor = gestor;

        setTitle("Gestor de Tareas Universitarias");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        crearInterfaz();
        mostrarPendientes();
    }

    // Crea los componentes de la interfaz
    private void crearInterfaz() {

        setLayout(new BorderLayout(10, 10));

        // Panel para registrar tareas
        JPanel panelRegistro = new JPanel(
                new GridLayout(8, 2, 5, 5)
        );

        panelRegistro.setBorder(
                BorderFactory.createTitledBorder("Registrar tarea")
        );

        campoId = new JTextField();
        campoTitulo = new JTextField();
        campoDescripcion = new JTextField();
        campoFecha = new JTextField();
        campoPeso = new JTextField();
        campoTiempo = new JTextField();
        campoCodigoAsignatura = new JTextField();
        campoNombreAsignatura = new JTextField();

        panelRegistro.add(new JLabel("ID:"));
        panelRegistro.add(campoId);

        panelRegistro.add(new JLabel("Título:"));
        panelRegistro.add(campoTitulo);

        panelRegistro.add(new JLabel("Descripción:"));
        panelRegistro.add(campoDescripcion);

        panelRegistro.add(new JLabel("Fecha (AAAA-MM-DD):"));
        panelRegistro.add(campoFecha);

        panelRegistro.add(new JLabel("Peso de la nota (%):"));
        panelRegistro.add(campoPeso);

        panelRegistro.add(new JLabel("Tiempo estimado (min):"));
        panelRegistro.add(campoTiempo);

        panelRegistro.add(new JLabel("Código asignatura:"));
        panelRegistro.add(campoCodigoAsignatura);

        panelRegistro.add(new JLabel("Nombre asignatura:"));
        panelRegistro.add(campoNombreAsignatura);

        add(panelRegistro, BorderLayout.NORTH);

        // Tabla de tareas
        String[] columnas = {
            "ID",
            "Título",
            "Asignatura",
            "Fecha",
            "Peso (%)",
            "Tiempo (min)",
            "Estado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(
                new GridLayout(2, 3, 5, 5)
        );

        JButton botonRegistrar = new JButton("Registrar tarea");
        JButton botonPendientes = new JButton("Ver pendientes");
        JButton botonConsultar = new JButton("Consultar siguiente");
        JButton botonCompletar = new JButton("Completar siguiente");
        JButton botonCompletarId = new JButton("Completar por ID");
        JButton botonHistorial = new JButton("Ver historial");

        panelBotones.add(botonRegistrar);
        panelBotones.add(botonPendientes);
        panelBotones.add(botonConsultar);
        panelBotones.add(botonCompletar);
        panelBotones.add(botonCompletarId);
        panelBotones.add(botonHistorial);

        add(panelBotones, BorderLayout.SOUTH);

        // Acciones de los botones
        botonRegistrar.addActionListener(e -> registrarTarea());

        botonPendientes.addActionListener(e -> mostrarPendientes());

        botonConsultar.addActionListener(e -> consultarSiguiente());

        botonCompletar.addActionListener(e -> completarSiguiente());

        botonCompletarId.addActionListener(e -> completarPorId());

        botonHistorial.addActionListener(e -> mostrarHistorial());
    }

    // Registra una tarea nueva
    private void registrarTarea() {

        try {

            int id = Integer.parseInt(campoId.getText().trim());

            String titulo = campoTitulo.getText().trim();

            String descripcion = campoDescripcion.getText().trim();

            LocalDate fecha = LocalDate.parse(
                    campoFecha.getText().trim()
            );

            double peso = Double.parseDouble(
                    campoPeso.getText().trim()
            );

            int tiempo = Integer.parseInt(
                    campoTiempo.getText().trim()
            );

            String codigo = campoCodigoAsignatura.getText().trim();

            String nombre = campoNombreAsignatura.getText().trim();

            // Validamos campos obligatorios
            if (titulo.isEmpty() || codigo.isEmpty() || nombre.isEmpty()) {

                mostrarError(
                        "El título, código y nombre de la asignatura son obligatorios."
                );

                return;
            }

            // El gestor verifica el ID y busca o crea la asignatura
            gestor.registrarTarea(
                    id,
                    titulo,
                    descripcion,
                    fecha,
                    peso,
                    tiempo,
                    codigo,
                    nombre
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Tarea registrada correctamente."
            );

            limpiarCampos();
            mostrarPendientes();

        } catch (NumberFormatException ex) {

            mostrarError("El ID, peso y tiempo deben ser numéricos.");

        } catch (DateTimeParseException ex) {

            mostrarError("La fecha debe tener el formato AAAA-MM-DD.");

        } catch (EFueraRango | EEntradaInvalida ex) {

            mostrarError(ex.getMessage());
        }
    }

    // Consulta la tarea de mayor prioridad
    private void consultarSiguiente() {

        try {

            Tarea tarea = gestor.consultarSiguiente();

            JOptionPane.showMessageDialog(
                    this,
                    "Siguiente tarea:\n\n"
                    + "ID: " + tarea.getId() + "\n"
                    + "Título: " + tarea.getTitulo() + "\n"
                    + "Descripción: " + tarea.getDescripcion() + "\n"
                    + "Asignatura: " + tarea.getAsignatura().getNombre() + "\n"
                    + "Fecha: " + tarea.getFechaEntrega() + "\n"
                    + "Peso: " + tarea.getPesoNota() + "%\n"
                    + "Tiempo estimado: "
                    + tarea.getTiempoEstimadoMin() + " minutos"
            );

        } catch (EColaVacia ex) {

            mostrarError(ex.getMessage());
        }
    }

    // Completa la tarea de mayor prioridad
    private void completarSiguiente() {

        try {

            Tarea tarea = gestor.completarSiguiente();

            JOptionPane.showMessageDialog(
                    this,
                    "Tarea completada:\n" + tarea.getTitulo()
            );

            mostrarPendientes();

        } catch (EColaVacia ex) {

            mostrarError(ex.getMessage());
        }
    }

    // Completa una tarea por su ID
    private void completarPorId() {

        String respuesta = JOptionPane.showInputDialog(
                this,
                "Ingrese el ID de la tarea:"
        );

        // Si el usuario cancela, no hacemos nada
        if (respuesta == null) {
            return;
        }

        try {

            int id = Integer.parseInt(respuesta.trim());

            Tarea tarea = gestor.completarPorId(id);

            JOptionPane.showMessageDialog(
                    this,
                    "Tarea completada:\n" + tarea.getTitulo()
            );

            mostrarPendientes();

        } catch (NumberFormatException ex) {

            mostrarError("Debe ingresar un ID numérico.");

        } catch (EEntradaInvalida ex) {

            mostrarError(ex.getMessage());
        }
    }

    // Muestra las tareas pendientes por prioridad
    private void mostrarPendientes() {

        List<Tarea> pendientes = gestor.obtenerPendientes();

        cargarTabla(pendientes);
    }

    // Muestra las tareas completadas
    private void mostrarHistorial() {

        List<Tarea> historial = gestor.obtenerHistorial();

        cargarTabla(historial);
    }

    // Carga las tareas en la tabla
    private void cargarTabla(List<Tarea> tareas) {

        // Limpiamos la tabla
        modeloTabla.setRowCount(0);

        // Recorremos las tareas con un for tradicional
        for (int i = 0; i < tareas.size(); i++) {

            Tarea tarea = tareas.get(i);

            Object[] fila = {
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getAsignatura().getNombre(),
                tarea.getFechaEntrega(),
                tarea.getPesoNota(),
                tarea.getTiempoEstimadoMin(),
                tarea.getEstado()
            };

            modeloTabla.addRow(fila);
        }
    }

    // Limpia los campos del formulario
    private void limpiarCampos() {

        campoId.setText("");
        campoTitulo.setText("");
        campoDescripcion.setText("");
        campoFecha.setText("");
        campoPeso.setText("");
        campoTiempo.setText("");
        campoCodigoAsignatura.setText("");
        campoNombreAsignatura.setText("");
    }

    // Muestra los errores en una ventana
    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // MAIN PARA EJECUTAR DIRECTAMENTE LA INTERFAZ
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // Creamos el gestor de tareas
            GestorTareas gestor = new GestorTareas();

            // Creamos la interfaz conectada al gestor
            InterfazGestor interfaz = new InterfazGestor(gestor);

            // Mostramos la ventana
            interfaz.setVisible(true);
        });
    }
}


