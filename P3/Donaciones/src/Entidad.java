public class Entidad {
    private String nombre;
    private String pass;
    private double totalDonado;

    Entidad(String nombre, String pass){
        this.nombre = nombre;
        this.pass = pass;
        this.totalDonado = 0;
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
}
