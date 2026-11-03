import Exceptions.EColaVacia;
import Exceptions.EEntradaInvalida;
import Exceptions.EFueraRango;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class InterfazGestor extends JFrame {

    private final GestorTareas gestor;

    // Campos para registrar tareas
    private JTextField campoId;
    private JTextField campoTitulo;
    private JTextArea campoDescripcion;
    private JTextField campoFecha;
    private JTextField campoPeso;
    private JTextField campoTiempo;
    private JTextField campoCodigoAsignatura;
    private JTextField campoNombreAsignatura;

    // Tablas
    private JTable tablaPendientes;
    private JTable tablaHistorial;
    private DefaultTableModel modeloPendientes;
    private DefaultTableModel modeloHistorial;

    // Indicadores
    private JLabel cantidadPendientes;
    private JLabel cantidadCompletadas;
    private JLabel proximaEntrega;
    private JLabel cantidadHistorial;

    private JTabbedPane pestanas;

    // Colores
    private final Color FONDO = new Color(245, 247, 251);
    private final Color BLANCO = Color.WHITE;
    private final Color TEXTO = new Color(30, 41, 59);
    private final Color AZUL = new Color(220, 232, 255);
    private final Color VERDE = new Color(220, 245, 225);
    private final Color GRIS = new Color(230, 233, 239);

    // Constructor
    public InterfazGestor(GestorTareas gestor) {

        this.gestor = gestor;

        setTitle("Gestor de Tareas Universitarias");
        setSize(1150, 780);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        construirInterfaz();
        actualizarDatos();
    }

    // Construir la ventana
    private void construirInterfaz() {

        JPanel principal = new JPanel(new BorderLayout(12, 12));
        principal.setBackground(FONDO);
        principal.setBorder(new EmptyBorder(15, 20, 15, 20));

        setContentPane(principal);

        JPanel encabezado = new JPanel(new BorderLayout(0, 10));
        encabezado.setOpaque(false);

        JLabel titulo = new JLabel("Gestor de Tareas Universitarias");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
            "Organiza tus entregas por fecha, porcentaje de la nota y orden de registro"
        );
        subtitulo.setForeground(Color.DARK_GRAY);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 5));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        encabezado.add(textos, BorderLayout.NORTH);

        // Crear pestañas
        pestanas = new JTabbedPane();

        pestanas.addTab("Inicio y pendientes", crearPanelPendientes());
        pestanas.addTab("Registrar tarea", crearPanelRegistro());
        pestanas.addTab("Historial", crearPanelHistorial());

        // Botones principales
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        acciones.setOpaque(false);

        JButton btnNueva = crearBoton("Nueva tarea", AZUL);
        JButton btnConsultar = crearBoton("Consultar siguiente", AZUL);
        JButton btnCompletar = crearBoton("Completar siguiente", VERDE);
        JButton btnCompletarId = crearBoton("Completar por ID", VERDE);
        JButton btnPendientes = crearBoton("Ver pendientes", GRIS);
        JButton btnHistorial = crearBoton("Ver historial", GRIS);

        btnNueva.addActionListener(e -> pestanas.setSelectedIndex(1));
        btnConsultar.addActionListener(e -> consultarSiguiente());
        btnCompletar.addActionListener(e -> completarSiguiente());
        btnCompletarId.addActionListener(e -> completarPorId());

        btnPendientes.addActionListener(e -> {
            actualizarDatos();
            pestanas.setSelectedIndex(0);
        });

        btnHistorial.addActionListener(e -> {
            actualizarDatos();
            pestanas.setSelectedIndex(2);
        });

        acciones.add(btnNueva);
        acciones.add(btnConsultar);
        acciones.add(btnCompletar);
        acciones.add(btnCompletarId);
        acciones.add(btnPendientes);
        acciones.add(btnHistorial);

        JScrollPane scrollAcciones = new JScrollPane(acciones);
        scrollAcciones.setBorder(null);
        scrollAcciones.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollAcciones.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_NEVER
        );
        scrollAcciones.setPreferredSize(new Dimension(800, 55));

        encabezado.add(scrollAcciones, BorderLayout.SOUTH);

        principal.add(encabezado, BorderLayout.NORTH);
        principal.add(pestanas, BorderLayout.CENTER);
    }

    // Panel de tareas pendientes
    private JPanel crearPanelPendientes() {

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(FONDO);
        panel.setBorder(new EmptyBorder(15, 10, 10, 10));

        // Indicadores
        JPanel indicadores = new JPanel(new GridLayout(1, 3, 10, 0));
        indicadores.setOpaque(false);

        cantidadPendientes = new JLabel();
        cantidadCompletadas = new JLabel();
        proximaEntrega = new JLabel();

        indicadores.add(crearTarjeta(cantidadPendientes));
        indicadores.add(crearTarjeta(cantidadCompletadas));
        indicadores.add(crearTarjeta(proximaEntrega));

        panel.add(indicadores, BorderLayout.NORTH);

        // Tabla de pendientes
        String[] columnas = {
            "ID", "Título", "Asignatura", "Fecha",
            "Peso (%)", "Tiempo (min)", "Estado"
        };

        modeloPendientes = crearModelo(columnas);
        tablaPendientes = new JTable(modeloPendientes);
        configurarTabla(tablaPendientes);

        panel.add(new JScrollPane(tablaPendientes), BorderLayout.CENTER);

        // No agregamos botones inferiores
        return panel;
    }

    // Panel de registro
    private JPanel crearPanelRegistro() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(FONDO);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel formulario = new JPanel(new GridLayout(8, 2, 15, 10));
        formulario.setBackground(BLANCO);
        formulario.setBorder(new EmptyBorder(20, 25, 20, 25));

        campoId = new JTextField();
        campoTitulo = new JTextField();
        campoDescripcion = new JTextArea(2, 20);
        campoFecha = new JTextField();
        campoPeso = new JTextField();
        campoTiempo = new JTextField();
        campoCodigoAsignatura = new JTextField();
        campoNombreAsignatura = new JTextField();

        campoDescripcion.setLineWrap(true);
        campoDescripcion.setWrapStyleWord(true);

        agregarCampo(
            formulario,
            "ID (entero positivo, ej. 1):",
            campoId
        );

        agregarCampo(
            formulario,
            "Título (texto, ej. Parcial de cálculo):",
            campoTitulo
        );

        agregarCampo(
            formulario,
            "Descripción (texto libre):",
            new JScrollPane(campoDescripcion)
        );

        agregarCampo(
            formulario,
            "Fecha (AAAA-MM-DD, ej. 2026-11-10):",
            campoFecha
        );

        agregarCampo(
            formulario,
            "Peso de nota (0 a 100, ej. 25):",
            campoPeso
        );

        agregarCampo(
            formulario,
            "Tiempo estimado (minutos, ej. 120):",
            campoTiempo
        );

        agregarCampo(
            formulario,
            "Código asignatura (texto, ej. EDA):",
            campoCodigoAsignatura
        );

        agregarCampo(
            formulario,
            "Nombre asignatura (ej. Estructuras de Datos):",
            campoNombreAsignatura
        );

        // Formulario con desplazamiento
        JScrollPane scrollFormulario = new JScrollPane(formulario);
        scrollFormulario.setBorder(null);
        scrollFormulario.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollFormulario, BorderLayout.CENTER);

        // Botones de registro siempre visibles
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        botones.setBackground(FONDO);

        JButton btnLimpiar = crearBoton("Limpiar formulario", GRIS);
        JButton btnRegistrar = crearBoton("REGISTRAR TAREA", VERDE);

        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRegistrar.addActionListener(e -> registrarTarea());

        botones.add(btnLimpiar);
        botones.add(btnRegistrar);

        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    // Panel del historial
    private JPanel crearPanelHistorial() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(FONDO);
        panel.setBorder(new EmptyBorder(15, 10, 10, 10));

        cantidadHistorial = new JLabel("Total completadas: 0");
        cantidadHistorial.setFont(new Font("SansSerif", Font.BOLD, 17));
        cantidadHistorial.setForeground(TEXTO);

        panel.add(cantidadHistorial, BorderLayout.NORTH);

        String[] columnas = {
            "ID", "Título", "Asignatura", "Fecha",
            "Peso (%)", "Estado"
        };

        modeloHistorial = crearModelo(columnas);
        tablaHistorial = new JTable(modeloHistorial);
        configurarTabla(tablaHistorial);

        panel.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        // Historial sin botones inferiores
        return panel;
    }

    // Registrar tarea
    private void registrarTarea() {

        try {

            int id = Integer.parseInt(campoId.getText().trim());
            String titulo = campoTitulo.getText().trim();
            String descripcion = campoDescripcion.getText().trim();
            LocalDate fecha = LocalDate.parse(campoFecha.getText().trim());
            double peso = Double.parseDouble(campoPeso.getText().trim());
            int tiempo = Integer.parseInt(campoTiempo.getText().trim());

            String codigo = campoCodigoAsignatura.getText().trim();
            String nombre = campoNombreAsignatura.getText().trim();

            // Validar datos
            if (id <= 0) {
                mostrarError("El ID debe ser un entero positivo.");
                return;
            }

            if (titulo.isEmpty()) {
                mostrarError("Debes escribir el título.");
                return;
            }

            if (codigo.isEmpty() || nombre.isEmpty()) {
                mostrarError("Completa los datos de la asignatura.");
                return;
            }

            if (tiempo <= 0) {
                mostrarError("El tiempo debe ser mayor que cero.");
                return;
            }

            if (!Double.isFinite(peso) || peso < 0 || peso > 100) {
                mostrarError("El peso debe estar entre 0 y 100.");
                return;
            }

            // Evitar IDs repetidos
            List<Tarea> pendientes = gestor.obtenerPendientes();
            List<Tarea> historial = gestor.obtenerHistorial();

            for (Tarea pendiente : pendientes) {

                if (pendiente.getId() == id) {
                    mostrarError("Ya existe una tarea con ese ID.");
                    return;
                }
            }

            for (Tarea tarea : historial) {

                if (tarea.getId() == id) {
                    mostrarError("Ya existe una tarea con ese ID.");
                    return;
                }
            }

            gestor.registrarTarea(
                id, titulo, descripcion, fecha,
                peso, tiempo, codigo, nombre
            );

            JOptionPane.showMessageDialog(
                this,
                "Tarea registrada correctamente.",
                "Registro exitoso",
                JOptionPane.INFORMATION_MESSAGE
            );

            limpiarFormulario();
            actualizarDatos();
            pestanas.setSelectedIndex(0);

        } catch (NumberFormatException e) {

            mostrarError(
                "Verifica los números:\n"
                + "ID: entero positivo.\n"
                + "Peso: número entre 0 y 100.\n"
                + "Tiempo: entero positivo."
            );

        } catch (DateTimeParseException e) {

            mostrarError(
                "Fecha incorrecta.\n"
                + "Formato: AAAA-MM-DD.\n"
                + "Ejemplo: 2026-11-10."
            );

        } catch (EFueraRango | EEntradaInvalida e) {

            mostrarError(e.getMessage());
        }
    }

    // Consultar siguiente sin eliminarla
    private void consultarSiguiente() {

        try {

            Tarea tarea = gestor.consultarSiguiente();

            JOptionPane.showMessageDialog(
                this,
                informacionTarea(tarea),
                "Siguiente tarea por prioridad",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (EColaVacia e) {

            mostrarError("No hay tareas pendientes para consultar.");
        }
    }

    // Completar la siguiente tarea
    private void completarSiguiente() {

        try {

            Tarea tarea = gestor.consultarSiguiente();

            int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas completar \"" + tarea.getTitulo() + "\"?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {

                gestor.completarSiguiente();

                actualizarDatos();

                JOptionPane.showMessageDialog(
                    this,
                    "Tarea completada correctamente."
                );
            }

        } catch (EColaVacia e) {

            mostrarError(e.getMessage());
        }
    }

    // Completar cualquier tarea por ID
    private void completarPorId() {

        String texto = JOptionPane.showInputDialog(
            this,
            "Escribe el ID de la tarea.\n"
            + "Formato: número entero positivo. Ejemplo: 3"
        );

        if (texto == null) {
            return;
        }

        try {

            int id = Integer.parseInt(texto.trim());

            if (id <= 0) {
                mostrarError("El ID debe ser un entero positivo.");
                return;
            }

            // Verificar que exista
            Tarea tarea = buscarPendiente(id);

            if (tarea == null) {
                mostrarError("No existe una tarea pendiente con ese ID.");
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas completar \"" + tarea.getTitulo() + "\"?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {

                gestor.completarPorId(id);

                actualizarDatos();

                JOptionPane.showMessageDialog(
                    this,
                    "Tarea completada correctamente."
                );
            }

        } catch (NumberFormatException e) {

            mostrarError("El ID debe ser un número entero.");

        } catch (EEntradaInvalida e) {

            mostrarError(e.getMessage());
        }
    }

    // Buscar tarea por ID
    private Tarea buscarPendiente(int id) {

        List<Tarea> pendientes = gestor.obtenerPendientes();

        for (Tarea tarea : pendientes) {

            if (tarea.getId() == id) {
                return tarea;
            }
        }

        return null;
    }

    // Actualizar tablas y contadores
    private void actualizarDatos() {

        modeloPendientes.setRowCount(0);
        modeloHistorial.setRowCount(0);

        List<Tarea> pendientes = gestor.obtenerPendientes();
        List<Tarea> historial = gestor.obtenerHistorial();

        // Mostrar pendientes
        for (Tarea tarea : pendientes) {

            modeloPendientes.addRow(new Object[]{
                    tarea.getId(),
                    tarea.getTitulo(),
                    tarea.getAsignatura().getNombre(),
                    tarea.getFechaEntrega(),
                    tarea.getPesoNota(),
                    tarea.getTiempoEstimadoMin(),
                    tarea.getEstado()
            });
        }

        // Mostrar historial
        for (Tarea tarea : historial) {

            modeloHistorial.addRow(new Object[]{
                    tarea.getId(),
                    tarea.getTitulo(),
                    tarea.getAsignatura().getNombre(),
                    tarea.getFechaEntrega(),
                    tarea.getPesoNota(),
                    tarea.getEstado()
            });
        }

        cantidadPendientes.setText(
            "Tareas pendientes: " + pendientes.size()
        );

        cantidadCompletadas.setText(
            "Tareas completadas: " + historial.size()
        );

        cantidadHistorial.setText(
            "Total completadas: " + historial.size()
        );

        if (!pendientes.isEmpty()) {

            Tarea siguiente = pendientes.getFirst();

            proximaEntrega.setText(
                "Próxima entrega: " + siguiente.getFechaEntrega()
            );

        } else {

            proximaEntrega.setText("Próxima entrega: ninguna");
        }
    }

    // Mostrar información completa de una tarea
    private String informacionTarea(Tarea tarea) {

        return "ID: " + tarea.getId()
            + "\nTítulo: " + tarea.getTitulo()
            + "\nDescripción: " + tarea.getDescripcion()
            + "\nAsignatura: " + tarea.getAsignatura().getNombre()
            + "\nFecha de entrega: " + tarea.getFechaEntrega()
            + "\nPeso de la nota: " + tarea.getPesoNota() + "%"
            + "\nTiempo estimado: "
            + tarea.getTiempoEstimadoMin() + " minutos"
            + "\nEstado: " + tarea.getEstado();
    }

    // Limpiar formulario
    private void limpiarFormulario() {

        campoId.setText("");
        campoTitulo.setText("");
        campoDescripcion.setText("");
        campoFecha.setText("");
        campoPeso.setText("");
        campoTiempo.setText("");
        campoCodigoAsignatura.setText("");
        campoNombreAsignatura.setText("");
    }

    // Mostrar errores
    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Aviso",
            JOptionPane.WARNING_MESSAGE
        );
    }

    // Agregar campos al formulario
    private void agregarCampo(
        JPanel panel, String etiqueta, Component campo
    ) {

        JLabel texto = new JLabel(etiqueta);
        texto.setForeground(TEXTO);

        panel.add(texto);
        panel.add(campo);
    }

    // Crear tarjetas de información
    private JPanel crearTarjeta(JLabel etiqueta) {

        JPanel panel = new JPanel(new BorderLayout());

        panel.setBackground(BLANCO);
        panel.setBorder(new EmptyBorder(18, 15, 18, 15));

        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 14));
        etiqueta.setForeground(TEXTO);

        panel.add(etiqueta, BorderLayout.CENTER);

        return panel;
    }

    // Crear botones compatibles con macOS
    private JButton crearBoton(String texto, Color fondo) {

        JButton boton = new JButton(texto);

        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setForeground(Color.BLACK);
        boton.setBackground(fondo);

        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBorderPainted(true);
        boton.setFocusPainted(false);

        boton.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 195, 205)),
                new EmptyBorder(9, 13, 9, 13)
            )
        );

        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return boton;
    }

    // Crear tabla no editable
    private DefaultTableModel crearModelo(String[] columnas) {

        return new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    // Configurar tablas
    private void configurarTabla(JTable tabla) {

        tabla.setRowHeight(32);

        tabla.setFont(
            new Font("SansSerif", Font.PLAIN, 13)
        );

        tabla.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 13)
        );

        tabla.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setFillsViewportHeight(true);
        tabla.setShowVerticalLines(false);
    }

    // Ejecutar la aplicación directamente
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            GestorTareas gestor = new GestorTareas();

            InterfazGestor interfaz = new InterfazGestor(gestor);

            interfaz.setVisible(true);
        });
    }
}


