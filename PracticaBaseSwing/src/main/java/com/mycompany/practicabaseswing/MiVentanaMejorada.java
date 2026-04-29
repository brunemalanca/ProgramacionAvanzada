package com.mycompany.practicabaseswing;

import javax.swing.*;
import java.awt.*;

/**
 * Ejemplo mejorado de JFrame y Herencia.
 * Basado en: "Swing Completo - Creación de ventanas"
 */
public class MiVentanaMejorada extends JFrame {

    public MiVentanaMejorada() {
        // Título de la ventana
        super("Mi Aplicación Swing - Versión Mejorada"); 
        
        // Configuración básica
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        // MEJORA: Centrar la ventana automáticamente
        setLocationRelativeTo(null);
        
        // MEJORA: Añadir un color de fondo al panel principal
        getContentPane().setBackground(new Color(230, 240, 250));
        
        // Etiqueta de bienvenida
        JLabel lbl = new JLabel("Ejemplo de Ventana con Herencia", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        add(lbl);
    }

    public static void main(String[] args) {
        MiVentanaMejorada v = new MiVentanaMejorada();
        v.setVisible(true);
    }
}