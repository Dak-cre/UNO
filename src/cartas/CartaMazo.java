/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cartas;

import com.Servidor.ClienteSocket;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Alumno
 */
public class CartaMazo {

    int id = 0;
    
    //CartaMazo mazoOriginal = new cartas.CartaMazo();
    
    private ArrayList<Carta> cartas = new ArrayList();
    //
   private ArrayList<Carta> Auxiliar = new ArrayList<>();

    String colores[] = {"Rojo", "Azul", "verde", "Amarrillo"};
    String accion[] = {"+2", "reversa", "bloqueo"};

    public void CrearMazo() {
        //for para cartas numericas
        for (int i = 0; i < colores.length; i++) {
            for (int j = 0; j < 10; j++) {
                cartas.add(new CartaNumero(colores[i], j, id));
                id++;

            }
            for (int j = 0; j < 10; j++) {
                cartas.add(new CartaNumero(colores[i], j, id));
                id++;
            }
        }
        //for para cartas de accion
        for (int i = 0; i < colores.length; i++) {
            for (int j = 0; j < accion.length; j++) {
                cartas.add(new CartaAccion(colores[i], accion[j], id));
                id++;
            }
            for (int j = 0; j < accion.length; j++) {
                cartas.add(new CartaAccion(colores[i], accion[j], id));
                id++;
            }
        }

        cartas.add(new CartaComodin("", "+4", id));
        id++;
        cartas.add(new CartaComodin("", "+4", id));
        id++;
        cartas.add(new CartaComodin("", "+4", id));
        id++;
        cartas.add(new CartaComodin("", "+4", id));
        id++;

        cartas.add(new CartaComodin("", "Cambio color", id));
        id++;
        cartas.add(new CartaComodin("", "Cambio color", id));
        id++;
        cartas.add(new CartaComodin("", "Cambio color", id));
        id++;
        cartas.add(new CartaComodin("", "Cambio color", id));
        id++;
        
        //
        Auxiliar = cartas;
        
        Collections.shuffle(cartas);
    }

    public void MostrarMazo() {
        for (Carta c : cartas) {
            System.out.println(c);
            System.out.println("---------");
        }
    }

    public int robar() {

        int id = cartas.getLast().getId_carta() ;
        
        System.out.println(cartas.getLast().getId_carta());
        
        
        cartas.removeLast();
        

        return id;
    }

    public ArrayList<Carta> getCartas() {
        return cartas;
    }
    
    
    
    
    public void generarMazo(ClienteSocket c) {

        List<Carta> manoJugador = new ArrayList();

        JsonObject msg = new JsonObject();
        msg.addProperty("tipoMensaje", "Inicar Juego");

        JsonArray arreglo = new JsonArray();

        for (int i = 0; i < 8; i++) {

            arreglo.add( this.robar());

        }

        msg.add("Cartas", arreglo);

        // Convertir el gson a string
        String jString = msg.toString();
        //c.EnviarMensaje(jString);

        // metods para convertir el string a  gson y obtener todos los datos
        /*
            
            JsonObject msg = new JsonObject();
        
             
        JsonObject json = JsonParser.parseString(jString).getAsJsonObject();
        
        // obtener datos de acuerdo a su clave
        
        String tipoMensaje = json.get("tipoMensaje").getAsString();
        
        
         */
    }

    public void agregarCarta(Carta c) {

        cartas.addFirst(c);
        Collections.shuffle(cartas);

    }
    
    // esta funcion
    public Carta obtenerCartaId(int id){
        
        for(Carta c: Auxiliar) {
            
            if(  c.id_carta == id ) {
                return c;
            }
        }
        
        return null;
    }

}






