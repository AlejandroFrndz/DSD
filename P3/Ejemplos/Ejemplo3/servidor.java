import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;

public class servidor {
    public static void main(String[] args){
        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }

        try {
            Registry reg = LocateRegistry.createRegistry(1099);
            contador micontador = new contador();
            Naming.rebind("mmicontador",micontador);

            System.out.println("Servidor preparado");
        } catch (RemoteException | MalformedURLException e) {
            System.err.println("Remote Exception:");
            e.printStackTrace();
        }
    }
}
