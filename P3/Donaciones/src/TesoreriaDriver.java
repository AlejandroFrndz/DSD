import java.rmi.RemoteException;
import java.rmi.Naming;

public class TesoreriaDriver {
    public static void main(String[] args) throws RemoteException{
        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }
        
        try {
            Tesoreria tesoreria = new Tesoreria();
            Naming.rebind("tesoreria",tesoreria);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Tesorería Activa");
    }
}
