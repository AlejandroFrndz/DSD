import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class GestorCliente extends UnicastRemoteObject implements GestorClienteI{
    private ArrayList<Entidad> entidades;
    private double subtotal;

    GestorCliente() throws RemoteException{
        entidades = new ArrayList<>();
        subtotal = 0;
    }

    private Entidad buscarEntidad(String nombre){
        for(Entidad e : entidades){
            if(e.getNombre().equals(nombre)){
                return e;
            }
        }

        return null;
    }

    public boolean registrarEntidad(String nombre, String pass){
        System.out.println("Se ha solicitado un registro con nombre: " + nombre + " y contraseña " + pass);
        if(buscarEntidad(nombre) == null){
            Entidad e = new Entidad(nombre,pass);
            entidades.add(e);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean inicarSesion(String nombre, String pass){
        Entidad e = buscarEntidad(nombre);

        if(e == null){
            return false;
        }
        else if (e.getPass().equals(pass)){
            return true;
        }
        else{
            return false;
        }
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
        
        return subtotal;
    }
}
