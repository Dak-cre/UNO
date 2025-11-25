/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cartas;
//import Jugadores.Jugador;
/**
 *
 * @author Alumno
 */
public class Cartas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       CartaMazo mazo = new CartaMazo();
       //Jugador jg= new Jugador();
       mazo.CrearMazo();
       mazo.MostrarMazo();
       System.out.println(" Tu mano es :");
       //jg.robarCarta(mazo);
       //jg.mostrarMano();
       System.out.println("       ------         ");
       mazo.MostrarMazo();
    }
    
}
