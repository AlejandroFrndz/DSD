import java.net.MalformedURLException;
import java.rmi.*;

public class Server implements Runnable{
    private String nombre, nombreReplica, nombreReplica2;
    public static void main(String[] args){
        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }
        
        String nombre, nombreReplica, nombreReplica2;

        if(args.length < 1){
            System.out.println("Uso: <id>, siendo id 1, 2 o 3");
            System.exit(-1);
        }

        if((!args[0].equals("1") && !args[0].equals("2")) && !args[0].equals("3")){
            System.out.println("Uso: <id>, siendo id 1, 2 o 3");
            System.exit(-1);
        }

        if(args[0].equals("1")){
            nombre = "gestor1";
            nombreReplica = "gestor2";
            nombreReplica2 = "gestor3";
        }
        else if (args[0].equals("2")){
            nombre = "gestor2";
            nombreReplica = "gestor1";
            nombreReplica2 = "gestor3";
        }
        else{
            nombre = "gestor3";
            nombreReplica = "gestor1";
            nombreReplica2 = "gestor2";
        }

        new Thread(new Server(nombre,nombreReplica,nombreReplica2)).start();
    }

    Server(String nombre, String nombreReplica, String nombreReplica2){
        this.nombre = nombre;
        this.nombreReplica = nombreReplica;
        this.nombreReplica2 = nombreReplica2;
    }

    @Override
    public void run(){
        
        try {
            Gestor gestor = new Gestor(nombre,nombreReplica,nombreReplica2);
            Naming.rebind(nombre,gestor);
            System.out.println("Servidor preparado");
        } catch (RemoteException | MalformedURLException e) {
            System.err.println("Remote Exception:");
            e.printStackTrace();
        }
    }
}
