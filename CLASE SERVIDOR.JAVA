
package practicasocket;
//import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

public class Servidor {

    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR INICIADO EN PUERTO " + PUERTO + " ===");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {

            // El servidor queda esperando conexiones en un bucle
            while (true) {
                System.out.println("\n[SERVIDOR] Esperando conexión de un cliente...");
                Socket socket = serverSocket.accept();
                System.out.println("[SERVIDOR] Cliente conectado desde: " + socket.getInetAddress());

                // Maneja la comunicación con el cliente conectado
                manejarCliente(socket);
            }

        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error al iniciar el servidor: " + e.getMessage());
        }
    }

    private static void manejarCliente(Socket socket) {
        try (
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)
        ) {
            salida.println("Bienvenido al Servidor de Consonantes!");
            salida.println("Comandos disponibles:");
            salida.println("  CONSONANTES <palabra>  -> lista las consonantes de la palabra");
            salida.println("  SALIR                  -> cierra la conexión");
            salida.println("--------------------------------------------------");

            String mensajeRecibido;

            while ((mensajeRecibido = entrada.readLine()) != null) {

                // Log en la consola del servidor
                System.out.println("[CLIENTE DICE]: " + mensajeRecibido);

                if (mensajeRecibido.equalsIgnoreCase("SALIR")) {
                    salida.println("Hasta luego! Cerrando conexión...");
                    System.out.println("[SERVIDOR] Cliente desconectado.");
                    break;
                }

                String respuesta = procesarMensaje(mensajeRecibido);
                salida.println(respuesta);
                System.out.println("[SERVIDOR RESPONDE]: " + respuesta);
            }

        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error en la comunicación: " + e.getMessage());
        }
    }

private static String procesarMensaje(String mensaje) {
    String[] partes = mensaje.trim().split("\\s+", 2);
    String comando = partes[0].toUpperCase();

    if (comando.equals("CONSONANTES")) {
        if (partes.length < 2 || partes[1].trim().isEmpty()) {
            return "ERROR: Debes escribir una palabra. Ej: CONSONANTES programacion";
        }
        return obtenerConsonantes(partes[1].trim());
    }

    // Si no empieza con CONSONANTES, trata el mensaje entero como una palabra
    if (partes.length == 1) {
        return obtenerConsonantes(mensaje.trim());
    }

    return "Comando no reconocido. Usá CONSONANTES <palabra>, o escribí solo la palabra.";
}

private static String obtenerConsonantes(String palabra) {
    String vocales = "aeiouáéíóúüAEIOUÁÉÍÓÚÜ";
    StringBuilder todasLasConsonantes = new StringBuilder();
    LinkedHashSet<Character> unicas = new LinkedHashSet<>();
    int cantidad = 0;

    for (char c : palabra.toCharArray()) {
        if (Character.isLetter(c) && vocales.indexOf(c) == -1) {
            todasLasConsonantes.append(c).append(" ");
            unicas.add(Character.toLowerCase(c));
            cantidad++;
        }
    }

    if (cantidad == 0) {
        return "La palabra '" + palabra + "' no tiene consonantes.";
    }

    return "Consonantes de '" + palabra + "': " + todasLasConsonantes.toString().trim()
            + "  |  únicas: " + unicas
            + "  |  total: " + cantidad;
}
}
