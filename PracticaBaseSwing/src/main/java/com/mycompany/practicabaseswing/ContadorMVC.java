package com.mycompany.practicabaseswing;

import javax.swing.*;
import java.awt.*;

/**
 * Ejemplo de Arquitectura MVC (Modelo-Vista-Controlador).
 * Basado en: "Swing Completo - Arquitectura MVC"
 */

// EL MODELO (Lógica y Datos)
class ModeloDatos {
    private int contador = 0;
    public void sumar() { contador++; }
    public int getContador() { return contador; }
}

// LA VISTA Y CONTROLADOR (Interfaz)
public class ContadorMVC extends JFrame {
    private ModeloDatos modelo;
    private JLabel lblDisplay;
    private JButton btnClick;

    public ContadorMVC() {
        super("Ejemplo MVC");
        modelo = new ModeloDatos(); // Instancia del modelo
        
        lblDisplay = new JLabel("Clicks: 0", SwingConstants.CENTER);
        lblDisplay.setFont(new Font("Monospaced", Font.BOLD, 20));
        
        btnClick = new JButton("Presionar");
        
        // El "Controlador" (Listener) modifica el modelo y actualiza la vista
        btnClick.addActionListener(e -> {
            modelo.sumar();
            lblDisplay.setText("Clicks: " + modelo.getContador());
        });

        add(lblDisplay, BorderLayout.CENTER);
        add(btnClick, BorderLayout.SOUTH);
        
        setSize(250, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new ContadorMVC().setVisible(true);
    }
}