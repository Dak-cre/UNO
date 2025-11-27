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
        System.out.println("Envio de cartas");
        for (int i = 0; i < 8; i++) {

            int id = mazo.robar();

            System.out.println("Carta " + auxiliar.getCartas().get(id));

            array.add(id);

        }

        System.out.println("------");

        msg.add("Cartas", array);

        String mensage = msg.toString();

        c.enviarMensajeJugador(mensage);

    }

    public void cambioTurno() {

        if (hayganador) {
            return;
        }

        this.pocicionActual = (this.pocicionActual + this.direccion + this.jugadores.size()) % (this.jugadores.size());

        JsonObject tuTurno = new JsonObject();
        tuTurno.addProperty("Tipo", "TuTurno");
        enviarMensajePrivado(tuTurno.toString(), jugadores.get(pocicionActual));

        JsonObject cambioTurno = new JsonObject();
        cambioTurno.addProperty("Tipo", "CambioTurno");
        cambioTurno.addProperty("Turno", jugadores.get(pocicionActual).getNombre());
        enviarMensajeMenos(cambioTurno.toString(), jugadores.get(pocicionActual));

    }

    public void procesarmensaje(JsonObject json, ClienteSocket c) {

        String tipoMensaje = json.get("Tipo").getAsString();

        switch (tipoMensaje) {

            case "Robar": {
                JsonObject msg = new JsonObject();
                
                msg.addProperty("Tipo", "NuevaCarta");
                msg.addProperty("Carta", mazo.robar());
                c.enviarMensajeJugador(msg.toString());
                
                break;
            }

            case "Jugada": {

                int idCarta = json.get("Carta").getAsInt();

                JsonObject msj = new JsonObject();
                msj.addProperty("Tipo", "CambioCarta");
                msj.addProperty("Carta", idCarta);

                String accion = auxiliar.obtenerCartaId(idCarta).getAccion();

                // --- CAMBIO DE COLOR ---
                if (accion != null) {

                    if ("Cambio color".equals(accion)) {

                        JsonObject cambioColor = new JsonObject();
                        cambioColor.addProperty("Tipo", "CambioColor");
                       // cambioColor.addProperty("NuevoColor", json.get("Color").getAsString());
                        enviarMensajeTodos(cambioColor.toString());

                    } // --- REVERSA ---
                    else if ("reversa".equals(accion)) {

                        JsonObject cambioDireccion = new JsonObject();
                        cambioDireccion.addProperty("Tipo", "CambioDireccion");
                        enviarMensajeTodos(cambioDireccion.toString());

                        this.direccion *= -1;
                    } // --- +4 ---
                    else if ("+4".equals(accion)) {

                        JsonObject sumar4 = new JsonObject();
                        sumar4.addProperty("Tipo", "Sumar4");

                        JsonArray idCartas = new JsonArray();
                        int temporal = moduloPositivo(pocicionActual + direccion, jugadores.size());

                        for (int i = 0; i < 4; i++) {
                            idCartas.add(mazo.robar());
                        }

                        sumar4.add("Cartas", idCartas);
                        enviarMensajePrivado(sumar4.toString(), jugadores.get(temporal));
                        this.pocicionActual = (this.pocicionActual + this.direccion + this.jugadores.size()) % (this.jugadores.size());

                    } // --- +2 ---
                    else if ("+2".equals(accion)) {

                        JsonObject sumar2 = new JsonObject();
                        sumar2.addProperty("Tipo", "Sumar2");

                        JsonArray idCartas = new JsonArray();
                        int temporal = moduloPositivo(pocicionActual + direccion, jugadores.size());

                        for (int i = 0; i < 2; i++) {
                            idCartas.add(mazo.robar());
                        }

                        sumar2.add("Cartas", idCartas);
                        enviarMensajePrivado(sumar2.toString(), jugadores.get(temporal));
                        this.pocicionActual = (this.pocicionActual + this.direccion + this.jugadores.size()) % (this.jugadores.size());

                    } // --- BLOQUEO ---
                    else if ("bloqueo".equals(accion)) {

                        JsonObject bloqueo = new JsonObject();
                        bloqueo.addProperty("Tipo", "Bloqueo");

                        int temporal = moduloPositivo(pocicionActual + direccion, jugadores.size());
                        enviarMensajePrivado(bloqueo.toString(), jugadores.get(temporal));
                        this.pocicionActual = (this.pocicionActual + this.direccion + this.jugadores.size()) % (this.jugadores.size());

                    }

                }

                // Enviar que carta se puso
                enviarMensajeTodos(msj.toString());

                // Poner la carta en el mazo de descarte
                this.mazo.agregarCarta(this.auxiliar.getCartas().get(idCarta));
                Cartaactual = this.mazo.getCartas().getLast().getId_carta();

                // Avanzar turno SOLO UNA VEZ
                cambioTurno();

                break;
            }

            case "UNO": {
                JsonObject uno = new JsonObject();
                uno.addProperty("Tipo", "UNO");
                uno.addProperty("Jugador", c.getName());
                enviarMensajeTodos(uno.toString());
                break;
            }

            case "YaGane": {

                JsonObject ganador = new JsonObject();
                ganador.addProperty("Tipo", "Ganador");
                ganador.addProperty("Ganador", c.getNombre());

                System.out.println("Hay ganador " + c.getName());
                enviarMensajeTodos(ganador.toString());
                break;
            }

            default:
                System.out.println("Mensaje no reconocido");
                break;
        }
    }

    private int moduloPositivo(int valor, int modulo) {
        int r = valor % modulo;
        return (r < 0) ? r + modulo : r;
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
