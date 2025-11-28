package jugadoruno;

/* Importación de clases necesarias para sockets, JSON, manejo de entrada y salida y la estructura de datos.*/
import Interfaces.Interfaz;
import cartas.Carta;
import cartas.CartaComodin;
import cartas.CartaMazo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

import javax.swing.JOptionPane;

import com.sun.management.HotSpotDiagnosticMXBean;

/* 
    Jugador es una extension de thread(hilo), que permite que 
    se puedan hacer multiples tareas al mismo tiempo sin afectar 
    el flujo del juego.

 */
public class JugadorUNO extends Thread {

    /// --- Atributos del jugador --- //
    private int id_player;
    private String name;
    int cartaActual;

    Socket socket;// socket que controla la conexion con el servidor
    private PrintWriter mensajeCliente;//los mensajes que manda el jugador al servidor
    private BufferedReader mensajeServidor;//para leer los mensajes que el servidor manda añ jugador
    List<cartas.Carta> mano = new ArrayList<>();// ArrayList para la maano de cartas del jugador
    CartaMazo aux = new CartaMazo();// ArrayList auxiliar para obtener las cartas por ID
    Carta uax2 = new Carta("", "", -1); //Carta sobre la mesa
    String color;// Color actual elegido 
    Interfaz UI;// referemcia a la interfaz del jugador 

    public Carta getCartaMesa() {
        return uax2;
    }

    /*Constructor del jugador, el nombre lo dejamos vacio y se el mazo auxiliar
    con todas las cartas*/
    public JugadorUNO() {

        this.name = "";
        aux.CrearMazo();

    }

    /* se intenta establecer la conexion con el servidor mediante sockets*/
    public boolean Conectar(String host, int IP) {

        try {
            // Se crea el socket y se intenta conectar al servidor
            // Canal para enviar mensajes
            // Canal para recibir mensajes
            // Inicia el hilo para escuchar los mensajes del servidor
            socket = new Socket(host, IP);
            System.out.println("Se inicio la conexion con el servidor");
            mensajeCliente = new PrintWriter(socket.getOutputStream(), true);
            mensajeServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.start();
            return true;

        } catch (IOException e) {
            /*error en caso de que no se logre establecer la conexion*/
            System.out.println("error: " + e.getMessage());

        }

        return false;

    }

    /*metodo run(): se ejecuta en un hilo independiente, 
    este esa en constante comunicacion con el servidor*/
    @Override
    public void run() {

        String mensaje = "";
        try {

            while (socket != null
                    // Ciclo que escucha mientras la conexión esté activa
                    // Procesa cada mensaje recibido interpretándolo como JSON
                    && !socket.isClosed()
                    && (mensaje = mensajeServidor.readLine()) != null) {

                this.procesarMsj(mensaje);
            }

        } catch (IOException e) {

            System.out.println("Ocurrio un error");

        } finally { // Cuando termina el hilo se cierra la conexion

            cerrarConexion();
        }

    }

    public void enviarMensaje(String msg) {//metodo para enviar mensajes al servidor
        mensajeCliente.println(msg);
    }

    /*
    Cierra el socket y finaliza la conexión con el servidor.
     */
    public void cerrarConexion() {

        try {

            socket.close();
            System.out.println("se cerro la conexion con el servidor");
        } catch (IOException e) {

            System.out.println("Ourrio un error");

        }
    }

    // Metodos getters y setters para acceder a los atributos del jugador
    public int getId_player() {
        return id_player;
    }

    public void setId_player(int id_player) {
        this.id_player = id_player;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PrintWriter getMensajeCliente() {
        return mensajeCliente;
    }

    public void setMensajeCliente(PrintWriter mensajeCliente) {
        this.mensajeCliente = mensajeCliente;
    }

    public BufferedReader getMensajeServidor() {
        return mensajeServidor;
    }

    public void setMensajeServidor(BufferedReader mensajeServidor) {
        this.mensajeServidor = mensajeServidor;
    }

    public List<Carta> getMano() {
        return mano;
    }

    public void setMano(List<Carta> mano) {
        this.mano = mano;
    }

    /*Asigna una interfaz gráfica al jugador*/
    public void setInterfaz(Interfaz UI) {
        this.UI = UI;
    }

    public int getCartaActual() {
        return cartaActual;
    }

    /*
    * Procesa y ejecuta acciones dependiendo del mensaje JSON recibido desde el servidor.
    * ccada "Tipo" de mensaje activa una parte distinta de la logica del juego.
     */
    public void procesarMsj(String msj) {

        JsonObject json = JsonParser.parseString(msj).getAsJsonObject();
        String tipoMsj = json.get("Tipo").getAsString();

        switch (tipoMsj) {
            case "InicioJuego":// El servidor envía las cartas iniciales del jugador y el turno asignado

                System.out.println("El juego ha iniciado");

                int turno = json.get("Turno").getAsInt();
                id_player = turno;
                JsonArray id_cartas = json.get("Cartas").getAsJsonArray();

                // Aqui se guarda el id de la carta actual
                // ahora buscarcala para obtener sus atributos para poder evaluar que la carta
                // que eligio el jugador concuerde con la carta que eligio el jugador
                this.cartaActual = json.get("CartaActual").getAsInt();

                for (int i = 0; i < id_cartas.size(); i++) {
                    int j = id_cartas.get(i).getAsInt();
                    Carta temp = aux.obtenerCartaId(j);
                    this.mano.add(temp);
                    temp = null;

                    System.out.println("Carta " + this.mano.getLast());
                    System.out.println("ID: " + this.mano.getLast().getId_carta());

                }

                //UI.ListaCartasComboBox(mano);
                uax2 = aux.obtenerCartaId(cartaActual);
                UI.actualizarCartaMesa(aux.obtenerCartaId(cartaActual));
                UI.iniciarJuego();

                break;

            case "TuTurno"://notifica cuando es turno del jugador
                UI.TuTurno();
                break;

            case "NuevaCarta"://El servidor envía una carta nueva para añadir a la mano del jugador

                int idActual = json.get("Carta").getAsInt();
                Carta temporal = aux.obtenerCartaId(idActual);
                this.mano.add(temporal);
                this.UI.ListaCartasComboBox(mano);
                break;

            case "CambioCarta":// Se actualiza la carta que está sobre la mesa
                int idCartaMesa = json.get("Carta").getAsInt();

                //obtener la carta jugada desde el mazo auxiliar
                Carta cartaMesa = aux.obtenerCartaId(idCartaMesa);

                //actualizar la carta actual del juego
                this.uax2 = cartaMesa;
                this.cartaActual = idCartaMesa;

                //aualizar la interfaz del jugador
                UI.actualizarCartaMesa(cartaMesa);

                System.out.println("Carta actual: " + uax2);

                break;

            case "CambioColor":// Se actualiza el color elegido por un jugador
                // Aqui reciben el color en caso de  un cambio de color por parte de otro jugador
                // actualziar la carta de ser necesario apra valirdar solo el color

                if (json.has("Color")) {
                    color = json.get("Color").getAsString();
                    System.out.println("Color nuevo " + color);
                } else {

                    System.out.println("No me lelgo un color");
                }

                Carta temp = new CartaComodin(color, "", -1);
                this.uax2 = temp;
                UI.actualizarCartaMesa(temp);

                System.out.println("Carta actual cambio color: " + uax2);

                break;

            case "CambioDireccion":// Se notifica del cambio de dirección del turno
                UI.notificacion("Cambio de direccion");
                break;

            case "Sumar4":// El jugador debe tomar 4 cartas se agregan a la mano
                JsonArray cartas4 = json.get("Cartas").getAsJsonArray();

                for (int i = 0; i < cartas4.size(); i++) {

                    JsonObject mensaje = new JsonObject();
                    mensaje.addProperty("Tipo", "Robar");
                    String mesj = mensaje.toString();
                    enviarMensaje(mesj);

                }

                UI.notificacion("Te pusieron un +4 pipipipipi");
                //UI.ListaCartasComboBox(mano);

                break;

            case "Sumar2": // El jugador debe tomar 2 cartas se agregan a la mano

                JsonArray cartas2 = json.get("Cartas").getAsJsonArray();

                for (int i = 0; i < cartas2.size(); i++) {

                    JsonObject mensaje = new JsonObject();
                    mensaje.addProperty("Tipo", "Robar");
                    String mesj = mensaje.toString();
                    enviarMensaje(mesj);
                }

                UI.notificacion("Te pusieron un +2 pipipipipi");
                // UI.ListaCartasComboBox(mano);

                break;

            case "Bloqueo":// El jugador pierde un turno

                UI.notificacion("Te bloquearon");

                break;

            case "UNO":// Un jugador ha dicho UNO, se notifica a la interfaz

                String name = json.get("Jugador").getAsString();
                UI.notificacion(name + " dijo UNO");
                break;

            case "Ganador":// se notifica quien gano la partida

                String nameG = json.get("Ganador").getAsString();

                UI.notificacion(nameG + " ha ganado");

                break;

            case "CambioTurno":// eel servidor envía el nombre del jugador que tiene el turno

                String turno1 = json.get("Turno").getAsString();
                UI.CambioTurno(turno1);

                break;

            default:
                UI.notificacion("Mensaje no reconocido");
                break;
        }

    }

    public void removerCarta(Carta c) {

        this.mano.remove(c);
    }

    public boolean eliminarCartaPorId(int id) {

        for (int i = 0; i < mano.size(); i++) {
            Carta c = mano.get(i);

            if (c.getId_carta() == id) {
                mano.remove(i);
                return true;     
            }
        }

        return false;            
    }

}
