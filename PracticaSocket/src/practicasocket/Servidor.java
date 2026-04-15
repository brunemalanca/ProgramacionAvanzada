package practicasocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

public class Servidor {

    // Puerto donde el servidor va a escuchar conexiones
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR INICIADO EN PUERTO " + PUERTO + " ===");

        // a+++bre el servidor en el puerto 5000 y lo cierra al terminar
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {

            // bucle infinito: el servidor siempre queda esperando nuevos clientes
            while (true) {
                System.out.println("\n[SERVIDOR] Esperando conexión de un cliente...");

                // se pausa hasta que un cliente se conecte
                Socket socket = serverSocket.accept();
                System.out.println("[SERVIDOR] Cliente conectado desde: " + socket.getInetAddress());

                // Una vez conectado pasa a manejar la comunicación con ese cliente
                manejarCliente(socket);
            }

        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error al iniciar el servidor: " + e.getMessage());
        }
    }

    // Maneja toda la comunicación con un cliente conectado
    private static void manejarCliente(Socket socket) {
        try (
            // entrada: para leer los mensajes que manda el cliente
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // salida: para enviar respuestas al cliente
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // mensaje de bienvenida e instrucciones al cliente
            salida.println("Bienvenido al Servidor de Consonantes!");
            salida.println("Comandos disponibles:");
            salida.println("  CONSONANTES <palabra>  -> lista las consonantes de la palabra");
            salida.println("  SALIR                  -> cierra la conexión");
            salida.println("--------------------------------------------------");

            String mensajeRecibido;

            // lee mensajes del cliente en un bucle hasta que se desconecte
            while ((mensajeRecibido = entrada.readLine()) != null) {

                // Muestra en la consola del servidor todo lo que manda el cliente (log)
                System.out.println("[CLIENTE DICE]: " + mensajeRecibido);

                // se corta la comunicacion si el cliente envia SALIR
                if (mensajeRecibido.equalsIgnoreCase("SALIR")) {
                    salida.println("Hasta luego! Cerrando conexión...");
                    System.out.println("[SERVIDOR] Cliente desconectado.");
                    break;
                }

                // procesa el mensaje y genera una respuesta
                String respuesta = procesarMensaje(mensajeRecibido);

                // envía la respuesta al cliente y la loguea en consola
                salida.println(respuesta);
                System.out.println("[SERVIDOR RESPONDE]: " + respuesta);
            }

        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error en la comunicación: " + e.getMessage());
        }
    }

    // interpreta el mensaje del cliente y decide qué hacer
    private static String procesarMensaje(String mensaje) {
        // divide el mensaje en dos partes: el comando y el resto
        String[] partes = mensaje.trim().split("\\s+", 2);
        String comando = partes[0].toUpperCase();

        // el cliente escirbe CONSONANTES y luego la palabra
        if (comando.equals("CONSONANTES")) {
            // Verifica que haya una palabra después del comando
            if (partes.length < 2 || partes[1].trim().isEmpty()) {
                return "ERROR: Debes escribir una palabra. Ej: CONSONANTES programacion";
            }
            return obtenerConsonantes(partes[1].trim());
        }

        // el cliente escribe una palabra sin el CONSONANTES adelante
        if (partes.length == 1) {
            return obtenerConsonantes(mensaje.trim());
        }
        
        // cualquer error sobre la palabra
        return "Comando no reconocido. Usá CONSONANTES <palabra>, o escribí solo la palabra.";
    }

    // Analiza una palabra y devuelve sus consonantes
    private static String obtenerConsonantes(String palabra) {
        // Define todas las vocales (con y sin tilde) para poder excluirlas
        String vocales = "aeiouáéíóúüAEIOUÁÉÍÓÚÜ";

        // Acumula todas las consonantes encontradas, incluyendo repetidas
        StringBuilder todasLasConsonantes = new StringBuilder();

        // Guarda las consonantes únicas respetando el orden de aparición
        // LinkedHashSet elimina duplicados pero mantiene el orden
        LinkedHashSet<Character> unicas = new LinkedHashSet<>();

        int cantidad = 0;

        // Recorre cada letra de la palabra
        for (char c : palabra.toCharArray()) {
            // Solo procesa letras que NO sean vocales
            if (Character.isLetter(c) && vocales.indexOf(c) == -1) {
                todasLasConsonantes.append(c).append(" ");
                unicas.add(Character.toLowerCase(c)); // guarda en minúscula para no repetir Ll, etc.
                cantidad++;
            }
        }

        // Si no encontró ninguna consonante, avisa
        if (cantidad == 0) {
            return "La palabra '" + palabra + "' no tiene consonantes.";
        }

        // Devuelve todas las consonantes, las únicas y el total
        return "Consonantes de '" + palabra + "': " + todasLasConsonantes.toString().trim()
                + "  |  únicas: " + unicas
                + "  |  total: " + cantidad;
    }
}