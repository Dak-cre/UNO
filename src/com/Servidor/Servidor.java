/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Servidor;

import Interfaces.TestArea;
import Juego.Juego;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author dakis
 */
public class Servidor extends Thread {

    final int PUERTO;
    final int MIN_CLIENTES;
    final int MAXIMO_CLIENTES;
    ServerSocket servidor = null;
    List<ClienteSocket> JUGADORES;
    boolean INICIARJUEGO = false;

    Juego juego = new Juego();
    TestArea UI = new TestArea();
    

    public Servidor(int PUERTO, int MIN_CLIENTES, int MAXIMO_CLIENTES) {
        this.PUERTO = PUERTO;
        this.MIN_CLIENTES = MIN_CLIENTES;
        this.MAXIMO_CLIENTES = MAXIMO_CLIENTES;
        this.JUGADORES = Collections.synchronizedList(new ArrayList<>());;

    }

    public boolean iniciarServidor() {

        try {

            servidor = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado");

            Thread hiloServidor = new Thread(() -> aceptar());
            hiloServidor.start();
            
            return true;
        } catch (IOException e) {

            System.out.println("Ocurrio un error");

        }
        
        return false;

    }

    public void aceptar() {

        try {

            while (!INICIARJUEGO) {

                Socket temp = servidor.accept();

                this.JUGADORES.add(new ClienteSocket(temp, this));
                JUGADORES.getLast().start();

                if (UI != null) {
                    UI.nuevoJugador(  ( ""+temp.getRemoteSocketAddress()  )  );

                }

                if (JUGADORES.size() == MAXIMO_CLIENTES) {

                    this.IniciarJuego();
                    JOptionPane.showMessageDialog(null, "Ya alcanzo el limite de jugadores");
                    //juego.iniciarjuego();

                }

            }

        } catch (IOException e) {

            System.out.println("Ocurrio un error, dentro de aceptar");

        }

        JOptionPane.showMessageDialog(null, "EL juego ha comenzado");

    }

    public void setUI(TestArea UI) {
        this.UI = UI;
    }

    public void leerMesnajeCliente(ClienteSocket cs, JsonObject json) {

        juego.procesarmensaje(json,cs);

    }

    public void IniciarJuego() {

        this.INICIARJUEGO = true;

        try {

            this.servidor.close();
            //enviarMensaje();
            juego.iniciarjuego(this.JUGADORES);

        } catch (IOException e) {

            System.out.println("El servidor se ha cerrado");

        }

    }

    public void enviarMensaje(String  msg) {

        synchronized (JUGADORES) {

            for (ClienteSocket c : JUGADORES) {

                // c.EnviarMensaje("INICIO EL JUEGO");
                
                
                
                
            }

        }

    }
    
    public void enviarMensajePrivado(String  msg, ClienteSocket cs) {

        synchronized (JUGADORES) {

            for (ClienteSocket c : JUGADORES) {

                if(c == cs ){
                    
                    c.enviarMensajeJugador(msg);
                    break;
                 }
            }

        }

    }
    
    
        public void enviarMensajeMenos(String  msg, ClienteSocket cs) {

        synchronized (JUGADORES) {

            for (ClienteSocket c : JUGADORES) {

                if(c != cs ){
                    
                    c.enviarMensajeJugador(msg);
                    
                }
            }

        }

    }

    public int getPUERTO() {
        return PUERTO;
    }
    
    
    
    
    
    
    

}
