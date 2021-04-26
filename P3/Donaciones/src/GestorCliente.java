import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class GestorCliente extends UnicastRemoteObject implements GestorClienteI, GestorReplicaI{
    private ArrayList<Entidad> entidades;
    private double subtotal;
    private GestorReplicaI replica;
    private GestorClienteI gestorReplica;
    private String nombreReplica;
    private String nombrePropio;


    GestorCliente(String nombre, String replica) throws RemoteException{
        this.entidades = new ArrayList<>();
        this.subtotal = 0;
        this.nombrePropio = nombre;
        this.nombreReplica = replica;
    }

    public boolean setReplica(){
        try {
            replica = (GestorReplicaI)Naming.lookup(nombreReplica);
            gestorReplica = (GestorClienteI) replica;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(replica != null){
            return true;
        }
        else{
            return false;
        }
    }

    private Entidad buscarEntidad(String nombre){
        for(Entidad e : entidades){
            if(e.getNombre().equals(nombre)){
                return e;
            }
        }

        return null;
    }

    //GestorReplica
    public boolean existeEntidad(String nombre){
        Entidad e = buscarEntidad(nombre);

        if(e == null){
            return false;
        }
        else{
            return true;
        }
    }

    public int getNumEntidades(){
        return entidades.size();
    }

    public double getSubTotal(){
        return subtotal;
    }

    //GestorCliente
    public String registrarEntidad(String nombre, String pass){
        setReplica();
        
        System.out.println("Se ha solicitado un registro con nombre: " + nombre + " y contraseña " + pass);
        String result = "";
        try {
            if(buscarEntidad(nombre) == null && replica.existeEntidad(nombre) == false){
                if(replica.getNumEntidades() >= entidades.size()){
                    Entidad e = new Entidad(nombre,pass);
                    entidades.add(e);
                    result = nombrePropio;
                }
                else{
                    gestorReplica.registrarEntidad(nombre, pass);
                    result = nombreReplica;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String inicarSesion(String nombre, String pass){
        setReplica();

        Entidad e = buscarEntidad(nombre);
        String result = "";
        try {
            if(e == null){
                result = gestorReplica.inicarSesion(nombre, pass);
            }
            else if (e.getPass().equals(pass)){
                result = nombrePropio;
            }    
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public boolean donar(String nombre, double cantidad){
        Entidad e = buscarEntidad(nombre);
        if(e != null){
            e.donar(cantidad);
            subtotal += cantidad;
            return true;
        }
        else{
            return false;
        }
    }

    public double consultarTotal(String nombre){
        Entidad e = buscarEntidad(nombre);

        if(e == null){
            return -1;
        }

        if(e.getTotalDonado() <= 0){
            return -1;
        }
        
        double total = -1;

        try {
            total = subtotal;
            total += replica.getSubTotal();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return total;
    }
}
