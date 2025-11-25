/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cartas;

import com.google.gson.JsonObject;

/**
 *
 * @author Alumno
 */
public class CartaComodin extends Carta{
    
    public CartaComodin(String color, String accion, int id) {
        super(color, accion,id);
    }
    
    @Override
    public String toString() {
        return color +" " + accion;
    }
    
    @Override
    public JsonObject generarGson(){
        
        JsonObject json = new JsonObject();
        json.addProperty("Tipo", "Carta comodin");
        json.addProperty("Accion", accion);
        
        
        return json;
    
    }
    
    
    
}
