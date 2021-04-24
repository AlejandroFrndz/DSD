import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GestorClienteI extends Remote{
    public boolean registrarEntidad(String nombre, String pass) throws RemoteException;
    public boolean inicarSesion(String nombre, String pass) throws RemoteException;
    public boolean donar(String nombre, double cantidad) throws RemoteException;
    public double consultarTotal(String nombre) throws RemoteException;
}
