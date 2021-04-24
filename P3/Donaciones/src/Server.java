import java.net.MalformedURLException;
import java.rmi.*;

public class Server {
    public static void main(String[] args){
        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }

        try {
            GestorCliente gestorCliente = new GestorCliente();
            Naming.rebind("gestor",gestorCliente);
            System.out.println("Servidor preparado");
        } catch (RemoteException | MalformedURLException e) {
            System.err.println("Remote Exception:");
            e.printStackTrace();
        }
    }
}
