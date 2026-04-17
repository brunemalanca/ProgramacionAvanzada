import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {

 static final String HOST = "localhost";

    private static final int PUERTO = 5000;
    public static void main(String[] args) {

        System.out.println("=== CLIENTE INICIADO ===");
        try (

            Socket socket = new Socket(HOST, PUERTO);
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            Scanner teclado = new Scanner(System.in)
        ) {
            System.out.println("Conexión establecida!\n");
            // *** CAMBIO CLAVE: hilo dedicado a recibir mensajes del servidor ***

            Thread receptor = new Thread(() -> {
                try {
                    String linea;
                    while ((linea = entrada.readLine()) != null) {
                        System.out.println(linea);
                    }
                } catch (IOException e) {
                    System.out.println("[CLIENTE] Conexión cerrada.");
                }
            });
            receptor.setDaemon(true); // se cierra solo cuando el main termina
            receptor.start();
            
            // Hilo principal: solo envía lo que escribe el usuario
            while (true) {
                String mensaje = teclado.nextLine();
                if (mensaje.trim().isEmpty()) continue;
                salida.println(mensaje);
                if (mensaje.equalsIgnoreCase("SALIR")) {
                    System.out.println("[CLIENTE] Desconectado.");
                    break;
                }
            }

        } catch (ConnectException e) {
            System.err.println("[CLIENTE] No se pudo conectar. ¿Está corriendo el Servidor?");
        } catch (IOException e) {
            System.err.println("[CLIENTE] Error: " + e.getMessage());
        }
    }
}
//B escribe algo → hilo principal de B lo manda al servidor
//El servidor lo recibe en ManejadorCliente de B y llama a broadcast()
//broadcast() se lo envía al socket de A
//El hilo receptor de A lo estaba esperando, lo recibe y lo imprime en pantalla