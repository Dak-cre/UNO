/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Servidor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;
/**
 *
 * @author dakis
 * 
 */


public class ClienteSocket extends Thread {
    
    Socket socket;
    String  nombre;
    
    private PrintWriter mensajesServidor;
    private BufferedReader mensajesCliente;
    private Servidor s;
    

    public ClienteSocket(Socket socket, Servidor se) {
        this.socket = socket;
        this.nombre = "Desconocido";
        this.s = se;
        
         try {
            mensajesServidor = new PrintWriter(socket.getOutputStream(), true);
            mensajesCliente  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
         
    }
    
    
    // Esta funcion siempre esta corriendo, pero nos da la ventaja, de que 
    // la pantalla no se puede bloquear, ya que de lo contrario nos quedariamos esperando siemrpe a que el
    // cliente mande un mensaje
     @Override
    public void run() {
        try {
            String linea;

            while ((linea = mensajesCliente.readLine()) != null) {
                
                EnviarMensajeServidor(linea);
                
            }

        } catch (IOException e) {
            System.out.println("Cliente desconectado");
        }
    }
    
    public void EnviarMensajeServidor(String mensaje){

        JsonObject msg = JsonParser.parseString(mensaje).getAsJsonObject();
        
        
        
        if(   msg.get("Tipo").getAsString().equals( "NuevoJugador" )  ){
            
            String nombreJugador = msg.get("Nombre").getAsString();
            this.nombre = nombreJugador; 
            
            System.out.println(""+this.nombre);
            return;
        
        }
        
        
        s.leerMesnajeCliente(this, msg);
        
    }
    
    public void enviarMensajeJugador(String msg){
    
        mensajesServidor.println(msg);
    
    }

    public String getNombre() {
        return nombre;
    }
    
    
    
    
}
