package jugadoruno;



import Interfaces.Interfaz;
import cartas.Carta;
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


public class JugadorUNO extends Thread{

    private int id_player;
    private String name;
    int cartaActual;
    
    Socket socket;
    private PrintWriter mensajeCliente;
    private BufferedReader mensajeServidor;
    List<cartas.Carta> mano = new ArrayList<>();// ATRIBUTO A AÑADIR: una mano de cartas con la que se va a jugar*/
    CartaMazo aux = new CartaMazo();
    Carta uax2 = new Carta("", "", -1);
    String color;
    Interfaz UI ;
    
    
    
    
    
    public JugadorUNO() {
        
        this.name = "";
        aux.CrearMazo();
  
    }
    
    public  boolean Conectar(String host, int IP){
        
        try{
        
            socket = new Socket(host, IP);
            System.out.println("Se inicio la conexion con el servidor");
            mensajeCliente = new PrintWriter(socket.getOutputStream(), true);
            mensajeServidor = new BufferedReader( new InputStreamReader( socket.getInputStream() )  );
            
            this.start();
            return true;
        
        }catch( IOException e  ){
            System.out.println("error: " + e.getMessage());
        
        }
        
        return false;
    
    }
    
    public void robarCarta(){
       /*mano.add(carta): *//* robar carta y añadirla a la mano del jugador*/
        System.out.println(name + " robo una carta");
    }
    
    /*tiene que ser de tipo carta*/
    public void jugarCarta(){
        System.out.println(name + " jugó una carta");
    }
    
    @Override
    public void run(){
        
        String mensaje = "";
        try{
        
            while(  ( mensaje = mensajeServidor.readLine()  ) != null ){
                
                this.procesarMsj(mensaje);
            }
        
        
        }catch(IOException e){
            
            System.out.println("Ocurrio un error");
        
        }
        
    
    }
    
    public void enviarMensaje(String msg) {
        mensajeCliente.println(msg);
    }
    
    public void cerrarConexion(){
        
        try{
            
            socket.close();
            System.out.println("se cerro la conexion con el servidor");
        }catch(IOException e){
            
            System.out.println("Ourrio un error"); 
                  
            
        
        }
    }

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
    
    
    public void setInterfaz(Interfaz UI){
        
        this.UI = UI;
    
    }

    public int getCartaActual() {
        return cartaActual;
    }
    
    
    public void procesarMsj(String msj){
        
        JsonObject json = JsonParser.parseString(msj).getAsJsonObject();
        String tipoMsj = json.get("Tipo").getAsString();
        
        switch(tipoMsj){
            case "InicioJuego":
                
                System.out.println("El juego ha iniciado");
                
                int turno = json.get("Turno").getAsInt();
                id_player = turno;
                JsonArray id_cartas = json.get("Cartas").getAsJsonArray();
                this.cartaActual = json.get("CartaActual").getAsInt();
                
                for(int i = 0; i < id_cartas.size(); i++){
                    int j = id_cartas.get(i).getAsInt();
                    this.mano.add(this.aux.getCartas().get(j));
                    
                    System.out.println("Carta " + this.mano.getLast());
                    
                }
                
                //UI.ListaCartasComboBox(mano);
                UI.iniciarJuego();
                
                
            break;
            
            case "TuTurno":
                UI.TuTurno();
            break;    
            
            case "NuevaCarta": 
                
                int idActual = json.get("Carta").getAsInt();
                this.mano.add(this.aux.getCartas().get(idActual));
                this.UI.ListaCartasComboBox(mano);
             
            break; 
            
            case "CambioCarta":
                int idCartaMesa = json.get("Carta").getAsInt();

                //obtener la carta jugada desde el mazo auxiliar
                Carta cartaMesa = this.aux.getCartas().get(idCartaMesa);

                //actualizar la carta actual del juego
                this.uax2 = cartaMesa;         
                this.cartaActual = idCartaMesa; 

                //aualizar la interfaz del jugador
                UI.actualizarCartaMesa(cartaMesa);
            break;    
            
            case "CambioColor":
                color = json.get("Color").getAsString();
            break;
            
            case "CambioDireccion":
                UI.notificacion("Cambio de direccion");
            break;
            
            case"Sumar4":
                JsonArray cartas4 = json.get("Cartas").getAsJsonArray();
                
                
                for(int i = 0; i < cartas4.size(); i++){
                    int j = cartas4.get(i).getAsInt();
                    this.mano.add(this.aux.getCartas().get(j));             
                }
                
                UI.notificacion("Te pusieron un +4 pipipipipi");
                UI.ListaCartasComboBox(mano);
                
            break;
            
            case"Sumar2":
                
                 JsonArray cartas2 = json.get("Cartas").getAsJsonArray();
                
                
                for(int i = 0; i < cartas2.size(); i++){
                    int j = cartas2.get(i).getAsInt();
                    this.mano.add(this.aux.getCartas().get(j));             
                }
                
                UI.notificacion("Te pusieron un +2 pipipipipi");
                UI.ListaCartasComboBox(mano);
                
            break;
            
            case"Bloqueo":
                
                UI.notificacion("Te bloquearon");
                
            break;
            
            case "UNO":
                
                String name = json.get("Jugador").getAsString();        
                UI.notificacion(name + " dijo UNO");
            break;
            
            case "Ganador":
                
                String nameG = json.get("Ganador").getAsString();
                
                UI.notificacion(nameG + " ha ganado");
                
            break;
            
            case "CambioTurno":
                
                String turno1 = json.get("Turno").getAsString();
                UI.CambioTurno(turno1);
                
            break;    
        
            default:
            break;
        }
        
    }
}    
    
