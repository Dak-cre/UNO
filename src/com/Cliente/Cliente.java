/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import javax.swing.JOptionPane;

/**
 *
 * @author dakis
 */
public class Cliente extends Thread{
    
    Socket socket;
    String nombre;
    
    
    
    private PrintWriter mensajeCliente;
    private BufferedReader mensajeServidor;
    
    
    public Cliente(String host, int IP){
        
        try{
        
            socket = new Socket(host, IP);
            System.out.println("Se inicio la conexion con el servidor");
            mensajeCliente = new PrintWriter(socket.getOutputStream(), true);
            mensajeServidor = new BufferedReader( new InputStreamReader( socket.getInputStream() )  );
            
            
            
        
        }catch( IOException e  ){
        
        
        }
        
    
    }
    
    
    
    @Override
    public void run(){
        
        String mensaje = "";
        try{
        
            while(  ( mensaje = mensajeServidor.readLine()  ) != null ){
                
                System.out.println("El servidor dice "+mensaje);
            
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
            
    
    public static void main(String[] args) {
        
        
        Cliente cliente = new Cliente("localhost", 6000);
        cliente.start();
        
        
         while (true) {

            String msg = JOptionPane.showInputDialog(null, "Escribe un mensaje:");

            if (msg == null || msg.equalsIgnoreCase("salir")) {
                break;
            }

            cliente.enviarMensaje(msg);
        }

        //cliente.cerrarConexion();
        
    }
    
    
    
    
    
    
}
