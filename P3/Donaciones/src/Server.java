import java.net.MalformedURLException;
import java.rmi.*;

public class Server {
    public static void main(String[] args){
        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }

        String nombre, nombreReplica;

        if(args.length < 1 || (!args[0].equals("1") && !args[0].equals("2"))){
            System.out.println("Uso: <id>, siendo id 1 o 2");
        }

        if(args[0].equals("1")){
            nombre = "gestor1";
            nombreReplica = "gestor2";
        }
        else{
            nombre = "gestor2";
            nombreReplica = "gestor1";
        }

        
        try {
            Gestor gestor = new Gestor(nombre,nombreReplica);
            Naming.rebind(nombre,gestor);
            System.out.println("Servidor preparado");
        } catch (RemoteException | MalformedURLException e) {
            System.err.println("Remote Exception:");
            e.printStackTrace();
        }
    }
}
