/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Juego;

import cartas.Carta;
import cartas.CartaAccion;
import cartas.CartaComodin;
import cartas.CartaMazo;
import com.Servidor.ClienteSocket;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alumno
 */
public class Juego {

    List<ClienteSocket> jugadores = new ArrayList<>();
    CartaMazo mazo = new CartaMazo();
    CartaMazo auxiliar = new CartaMazo();

    int Cartaactual;

    int direccion;
    int pocicionActual;

    boolean hayganador = false;
    
    public Juego() {
        this.direccion = 1;
        this.pocicionActual = -1;
        auxiliar.CrearMazo();
    }

    public void iniciarjuego(List<ClienteSocket> jug) {

        mazo.CrearMazo();
        this.jugadores = jug;
        int posicion = 0;

        this.Cartaactual = mazo.robar();
        System.out.println("Id: carta actual " + this.Cartaactual);

        synchronized (jugadores) {

            for (ClienteSocket c : this.jugadores) {

                enviarmazo(c, posicion + 1);
                posicion++;

            }

        }

        System.out.println("El juego ya ha iniciado");

        cambioTurno();

    }

    public void enviarmazo(ClienteSocket c, int turno) {

        JsonObject msg = new JsonObject();
        msg.addProperty("Tipo", "InicioJuego");
        msg.addProperty("Turno", turno);
        msg.addProperty("CartaActual", this.Cartaactual);

        JsonArray array = new JsonArray();

        for (int i = 0; i < 8; i++) {
            
            int id = mazo.robar();
            
            System.out.println("Carta " + auxiliar.getCartas().get(id));
            
            array.add(id);
            
            
            

        }

        msg.add("Cartas", array);

        String mensage = msg.toString();

        c.enviarMensajeJugador(mensage);

    }

    public void cambioTurno() {
        
        if(hayganador){
            return;
        }
        
        this.pocicionActual = (this.pocicionActual + this.direccion) % (this.jugadores.size());

        JsonObject tuTurno = new JsonObject();
        tuTurno.addProperty("Tipo", "TuTurno");

        enviarMensajePrivado(tuTurno.toString(), jugadores.get(pocicionActual));

        JsonObject cambioTurno = new JsonObject();
        cambioTurno.addProperty("Tipo", "CambioTurno");
        cambioTurno.addProperty("Turno", jugadores.get(pocicionActual).getNombre());
        enviarMensajeMenos(cambioTurno.toString(), jugadores.get(pocicionActual));

    }

    public void procesarmensaje(JsonObject json, ClienteSocket c) {

        String tipoMensaje = "";
        tipoMensaje = json.get("Tipo").getAsString();

        switch (tipoMensaje) {

            case "Robar":

                JsonObject msg = new JsonObject();
                msg.addProperty("Tipo", "NuevaCarta");
                msg.addProperty("Carta", mazo.robar());
                String mensaje = msg.toString();
                c.enviarMensajeJugador(mensaje);

                break;

            case "Jugada":

                JsonObject msj = new JsonObject();

                msj.addProperty("Tipo", "CambioCarta");
                int idCarta = json.get("Carta").getAsInt();
                msj.addProperty("Carta", idCarta);

                if ( idCarta >= 104 ) {

                    JsonObject cambioColor = new JsonObject();
                    cambioColor.addProperty("Tipo", "CambioColor");
                    String newCOlor = json.get("Color").getAsString();
                    cambioColor.addProperty("NuevoCOlor", newCOlor);
                    String cambioCOlormsg = cambioColor.toString();
                    enviarMensajeTodos(cambioCOlormsg);

                } else if (
                        auxiliar.getCartas().get(idCarta).getAccion() != null && 
                        auxiliar.getCartas().get(idCarta).getAccion().equals("reversa")
                        
                        ) {

                    JsonObject cambioDireccion = new JsonObject();
                    cambioDireccion.addProperty("Tipo", "CambioDireccion");
                    String cambioDireccrmsg = cambioDireccion.toString();
                    enviarMensajeTodos(cambioDireccrmsg);

                    this.direccion *= -1;

                } else if (
                        auxiliar.getCartas().get(idCarta).getAccion() != null &&                        
                        auxiliar.getCartas().get(idCarta).getAccion().equals("+4")) {

                    JsonObject sumar4 = new JsonObject();
                    sumar4.addProperty("Tipo", "Sumar4");

                    JsonArray idCartas = new JsonArray();
                    int temporal = (pocicionActual + direccion) % jugadores.size();

                    for (int i = 0; i < 4; i++) {
                        idCartas.add(mazo.robar());
                    }

                    sumar4.add("Cartas", idCartas);

                    enviarMensajePrivado(sumar4.toString(), jugadores.get(temporal));

                    this.pocicionActual = (this.pocicionActual + this.direccion) % jugadores.size();

                } else if (
                        auxiliar.getCartas().get(idCarta).getAccion() != null &&
                        auxiliar.getCartas().get(idCarta).getAccion().equals("+2")) {
                    JsonObject sumar2 = new JsonObject();
                    sumar2.addProperty("Tipo", "Sumar2");

                    JsonArray idCartas = new JsonArray();
                    int temporal = (pocicionActual + direccion) % jugadores.size();

                    for (int i = 0; i < 2; i++) {
                        idCartas.add(mazo.robar());
                    }

                    sumar2.add("Cartas", idCartas);

                    enviarMensajePrivado(sumar2.toString(), jugadores.get(temporal));

                    this.pocicionActual = (this.pocicionActual + this.direccion) % jugadores.size();

                } else if (
                        auxiliar.getCartas().get(idCarta).getAccion() != null &&
                        auxiliar.getCartas().get(idCarta).getAccion().equals("bloqueo")) {
                    JsonObject bloqueo = new JsonObject();
                    bloqueo.addProperty("Tipo", "Bloqueo");

                    int temporal = (pocicionActual + direccion) % jugadores.size();
                    enviarMensajePrivado(bloqueo.toString(), jugadores.get(temporal));
                    this.pocicionActual = (this.pocicionActual + this.direccion) % jugadores.size();

                }

                mensaje = msj.toString();

                this.enviarMensajeTodos(mensaje);

                
                this.mazo.agregarCarta(      this.auxiliar.getCartas().get(idCarta)      );
                
                cambioTurno();
                
                break;

            case "UNO":

                JsonObject uno = new JsonObject();
                uno.addProperty("Tipo", "UNO");
                uno.addProperty("Jugador", c.getName());
                enviarMensajeTodos(uno.toString());

                break;

            case "YaGane":
                
                JsonObject ganador = new JsonObject();
                ganador.addProperty("Tipo","Ganador");
                ganador.addProperty("Ganador", c.getNombre());
                
                System.out.println("Hay ganador "+c.getName());
                
                enviarMensajeTodos(ganador.toString());
                break;

            default:
                
                System.out.println("Mensaje no reconocido");
                break;

        }

    }

    public void enviarMensajeTodos(String msg) {

        synchronized (jugadores) {

            for (ClienteSocket c : jugadores) {

                c.enviarMensajeJugador(msg);

            }

        }

    }

    public void enviarMensajePrivado(String msg, ClienteSocket cs) {

        synchronized (jugadores) {

            for (ClienteSocket c : jugadores) {

                if (c == cs) {

                    c.enviarMensajeJugador(msg);

                    break;
                }
            }

        }

    }

    public void enviarMensajeMenos(String msg, ClienteSocket cs) {

        synchronized (jugadores) {

            for (ClienteSocket c : jugadores) {

                if (c != cs) {

                    c.enviarMensajeJugador(msg);

                }
            }

        }

    }

}
