import java.util.ArrayList;
public class Entidad {
    private String nombre;
    private String pass;
    private double totalDonado;
    ArrayList<ProyectosEnum> proyectos;

    Entidad(String nombre, String pass){
        this.nombre = nombre;
        this.pass = pass;
        this.totalDonado = 0;
        this.proyectos = new ArrayList<>();
    }

    public String getNombre(){
        return this.nombre;
    }

    public String getPass(){
        return this.pass;
    }

    public double getTotalDonado(){
        return this.totalDonado;
    }

    public void donar(double cantidad){
        this.totalDonado += cantidad;
    }

    public void addProyecto(ProyectosEnum proyecto){
        proyectos.add(proyecto);
    }

    public ArrayList<ProyectosEnum> getProyectos(){
        return proyectos;
    }
}
