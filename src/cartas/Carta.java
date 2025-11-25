package cartas;

import com.google.gson.JsonObject;

/**
 *
 * @author Juan, Edwim & Audelia
 */
public class Carta {
    protected String color;
    protected int valor;
    protected String accion;
    protected int id_carta;

    public Carta(String color, int valor, int id_carta) {
        this.color = color;
        this.valor = valor;
        this.id_carta = id_carta;
    }

    public Carta(String color, String accion, int id_carta) {
        this.color = color;
        this.accion = accion;
        this.id_carta = id_carta;
    }

    public String getColor() {
        return color;
    }

    public String getAccion() {
        return accion;
    }
    
    
    public int getValor() {
        return valor;
    }
    
    public void mostrarCarta(){
        
    }

    public int getId_carta() {
        return id_carta;
    }
    
    

    @Override
    public String toString() {
        return "Carta{" + "color=" + color + ", valor=" + valor + ", accion=" + accion + '}';
    }
    
    
    public JsonObject generarGson(){
        
        JsonObject json = new JsonObject();
        
        return json;
    
    }
    
    
    
    
    
    
    
}
