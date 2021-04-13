
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente_Ejemplo {
    public static void main(String args[]){
        if (System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }

        try {
            String nombre_objeto_remoto = "Ejemplo_I";
            System.out.println("Buscano el objeto remoto");
            Registry registry = LocateRegistry.getRegistry(args[0]);
            Ejemplo_I instancia_local = (Ejemplo_I) registry.lookup(nombre_objeto_remoto);
            instancia_local.escribir_mensaje(Integer.parseInt(args[1]));
        } catch (Exception e) {
            System.err.println("Ejemplo exception:");
            e.printStackTrace();
        }
    }
}
