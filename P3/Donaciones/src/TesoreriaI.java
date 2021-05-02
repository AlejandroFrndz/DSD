import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TesoreriaI extends Remote{
    public void aumentar(double donacion) throws RemoteException;
    public double consultarTotal() throws RemoteException;
    public double consultarBalance() throws RemoteException;
    public boolean decrementar(double cantidad) throws RemoteException;
}
