import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GestorReplicaI extends Remote{
    public int getNumEntidades() throws RemoteException;
    public boolean existeEntidad(String nombre) throws RemoteException;
    public int[] getProyectos() throws RemoteException;
}
