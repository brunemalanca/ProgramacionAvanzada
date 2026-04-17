import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
public class Servidor {
    private static final int PUERTO = 5000;
    // Lista que guarda todos los clientes conectados en este momento.
    // CopyOnWriteArrayList es thread-safe: varios hilos pueden leerla y modificarla
    // al mismo tiempo sin que se rompa nada (a diferencia de un ArrayList normal).
    
    private static final List<ManejadorCliente> clientes =
            new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR INICIADO EN PUERTO " + PUERTO + " ===");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                System.out.println("\n[SERVIDOR] Esperando conexión...");

                // accept() se PAUSA aquí hasta que alguien se conecte.
                // Cuando llega un cliente, devuelve un Socket con esa conexión.
                Socket socket = serverSocket.accept();
                System.out.println("[SERVIDOR] Cliente conectado desde: " + socket.getInetAddress());
                // Se crea un ManejadorCliente (que es Runnable) con el socket de ese cliente.
                // Se lo envuelve en un Thread y se arranca.
                // El main() NO se bloquea: vuelve al while y queda listo para el próximo cliente.
                ManejadorCliente manejador = new ManejadorCliente(socket);
                new Thread(manejador).start();
            }
        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error: " + e.getMessage());
        } 
    }
    // Manda un mensaje a TODOS los clientes de la lista, excepto al que lo envió.
    // Es "static" porque lo llaman desde la clase interna ManejadorCliente.
    static void broadcast(String mensaje, ManejadorCliente remitente) {
        for (ManejadorCliente c : clientes) {
            if (c != remitente) {                            // no se manda a sí mismo
                c.enviar("[" + remitente.getNombre() + "]: " + mensaje);
            }
        }
    }
    
    // Manda un mensaje solo a un cliente específico
    static boolean mensajePrivado(String destinatario, String mensaje, ManejadorCliente remitente) {
        for (ManejadorCliente c : clientes) {
            if (c.getNombre().equalsIgnoreCase(destinatario)) {
                c.enviar("[PRIVADO de " + remitente.getNombre() + "]: " + mensaje);
                return true; // Encontrado y enviado
            }
        }
        return false; // No se encontró al usuario
    }
    // Recorre la lista y arma un String con todos los nombres conectados.
    static String listarClientes() {
        if (clientes.isEmpty()) return "No hay clientes conectados.";
        StringBuilder sb = new StringBuilder("Clientes conectados:\n");
        for (ManejadorCliente c : clientes) {
            sb.append("  - ").append(c.getNombre()).append("\n");
        }
        return sb.toString().trim();
    }
    // Clase interna estática: representa a UN cliente conectado.
    // Cada vez que alguien se conecta, se crea una instancia de esta clase
    // y se ejecuta en su propio hilo.
    static class ManejadorCliente implements Runnable {
        private final Socket socket;   // conexión con este cliente

        private PrintWriter salida;    // para escribirle al cliente

        private String nombre;         // nombre que eligió al entrar

        ManejadorCliente(Socket socket) {
            this.socket = socket;
        }
        // Getter para que broadcast() pueda mostrar el nombre del remitente.

        String getNombre() { return nombre; }
        // Método público para que cualquier hilo (broadcast) pueda

        // mandarle un mensaje a ESTE cliente en particular.

        void enviar(String mensaje) {

            if (salida != null) salida.println(mensaje);
        }
        // run() es lo que ejecuta el hilo. Todo lo que pasa acá
        // ocurre en paralelo con los demás clientes.
        @Override
        
        public void run() {
            try (
                // entrada: leer lo que manda el cliente
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                // salida: escribirle al cliente (autoFlush=true para que no quede en buffer)
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                this.salida = out;
                // Lo primero que hace el servidor es pedirle el nombre.

                salida.println("Ingresá tu nombre:");

                nombre = entrada.readLine();   // espera hasta que el cliente escrib
                // Recién acá se agrega a la lista, ya que ya tenemos el nombre.

                clientes.add(this);

                System.out.println("[+] " + nombre + " se conectó. Total: " + clientes.size());
                // Avisa a los demás que alguien llegó.

                broadcast("*** " + nombre + " se unió al chat ***", this);
                salida.println("Bienvenido, " + nombre + "!");
                salida.println("Comandos: CONSONANTES <palabra> | LISTA | SALIR");
                salida.println("Cualquier otro texto se envía a todos.");
                salida.println("--------------------------------------------------");
                String mensajeRecibido;
                // Bucle principal: lee mensajes del cliente hasta que se desconecte.
                // readLine() devuelve null si el cliente cerró la conexión abruptamente.

                while ((mensajeRecibido = entrada.readLine()) != null) {
                    // ELIMINA el System.out.println general de aquí arriba

                    if (mensajeRecibido.equalsIgnoreCase("SALIR")) {
                        System.out.println("[SERVIDOR] " + nombre + " solicitó salir."); // Log del servidor
                        salida.println("Hasta luego!");
                        break;
                    }

                    if (mensajeRecibido.equalsIgnoreCase("LISTA")) {
                        // No imprimimos nada en el servidor para no llenar la consola de basura
                        salida.println(listarClientes());
                        continue;
                    }

                    // LÓGICA PRIVADA
                    if (mensajeRecibido.startsWith("@")) {
                        String[] partesPrivado = mensajeRecibido.split("\\s+", 2);
                        if (partesPrivado.length >= 2) {
                            String destino = partesPrivado[0].substring(1);
                            String texto = partesPrivado[1];
                            boolean enviado = mensajePrivado(destino, texto, this);

                            if (enviado) {
                                // Log discreto en el servidor: solo quién a quién, sin el texto
                                System.out.println("[PRIVADO] " + nombre + " -> " + destino);
                                salida.println("[Privado enviado a " + destino + "]: " + texto);
                            } else {
                                salida.println("Error: El usuario '" + destino + "' no está conectado.");
                            }
                        }
                        continue;
                    }

                    if (mensajeRecibido.toUpperCase().startsWith("CONSONANTES")) {
                        salida.println(procesarMensaje(mensajeRecibido));
                        continue;
                    }

                    // SI LLEGA AQUÍ, ES UN MENSAJE PÚBLICO
                    // Solo aquí imprimimos el contenido en la consola del servidor
                    System.out.println("[CHAT] " + nombre + ": " + mensajeRecibido);
                    broadcast(mensajeRecibido, this);
                }
            } catch (IOException e) {

                // Puede pasar si el cliente cierra la ventana sin escribir SALIR.
                System.err.println("[ERROR] " + nombre + ": " + e.getMessage());
            } finally {

                // finally se ejecuta SIEMPRE: tanto si salió del while normalmente
                // como si hubo una excepción o el cliente cerró la conexión.
                clientes.remove(this);   // se borra de la lista global
                System.out.println("[-] " + nombre + " se desconectó. Total: " + clientes.size());
                if (nombre != null) {
                    broadcast("*** " + nombre + " salió del chat ***", this);
                }
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        // Interpreta el comando CONSONANTES y delega en obtenerConsonantes().

        private String procesarMensaje(String mensaje) {

            String[] partes = mensaje.trim().split("\\s+", 2);  // divide en máximo 2 partes
            String comando = partes[0].toUpperCase();
            if (comando.equals("CONSONANTES")) {
                if (partes.length < 2 || partes[1].trim().isEmpty()) {
                    return "ERROR: Debes escribir una palabra. Ej: CONSONANTES programacion";
                }
                return obtenerConsonantes(partes[1].trim());
            }
            if (partes.length == 1) return obtenerConsonantes(mensaje.trim());
            return "Comando no reconocido.";
        }
        // Recorre letra por letra y separa las consonantes de las vocales.
        private String obtenerConsonantes(String palabra) {
            String vocales = "aeiouáéíóúüAEIOUÁÉÍÓÚÜ";
            StringBuilder todasLasConsonantes = new StringBuilder();
            LinkedHashSet<Character> unicas = new LinkedHashSet<>();  // sin repetidos, en orden
            int cantidad = 0;

            for (char c : palabra.toCharArray()) {

                // isLetter() filtra números y símbolos.

                // indexOf(c) == -1 significa que NO está en la cadena de vocales → es consonante.

                if (Character.isLetter(c) && vocales.indexOf(c) == -1) {
                    todasLasConsonantes.append(c).append(" ");
                    unicas.add(Character.toLowerCase(c));  // en minúscula para no duplicar R/r
                    cantidad++;
                }
            }

            if (cantidad == 0) return "La palabra '" + palabra + "' no tiene consonantes.";
            return "Consonantes de '" + palabra + "': " + todasLasConsonantes.toString().trim()
                    + "  |  únicas: " + unicas + "  |  total: " + cantidad;
        }
    }
}
//CopyOnWriteArrayList permite que un hilo lea la lista 
//mientras otros la modifican de forma segura