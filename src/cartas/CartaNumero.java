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
public class CartaNumero extends Carta {
    
    public CartaNumero(String color, int valor,int id) {
        super(color, valor, id);
    }
    
    @Override
    public String toString() {
        return  color + " " + valor ;
    }
    
    @Override
    public JsonObject generarGson(){
        
        JsonObject json = new JsonObject();
        json.addProperty("Tipo", "Carta normal");
        json.addProperty("Color",color);
        json.addProperty("Valor", valor);
        
        
        return json;
    
    }
    
    
    
}
