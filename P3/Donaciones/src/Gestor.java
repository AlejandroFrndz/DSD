import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Gestor extends UnicastRemoteObject implements GestorClienteI, GestorReplicaI{
    private ArrayList<Entidad> entidades;
    private GestorReplicaI replica;
    private GestorReplicaI replica2;
    private GestorClienteI gestorReplica;
    private GestorClienteI gestorReplica2;
    private String nombreReplica;
    private String nombreReplica2;
    private String nombrePropio;
    private TesoreriaI tesoreria;

    Gestor(String nombre, String replica, String replica2) throws RemoteException{
        this.entidades = new ArrayList<>();
        this.nombrePropio = nombre;
        this.nombreReplica = replica;
        this.nombreReplica2 = replica2;
        try {
            this.tesoreria = (TesoreriaI) Naming.lookup("tesoreria");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean setReplica(){
        try {
            replica = (GestorReplicaI)Naming.lookup(nombreReplica);
            gestorReplica = (GestorClienteI) replica;
            replica2 = (GestorReplicaI)Naming.lookup(nombreReplica2);
            gestorReplica2 = (GestorClienteI) replica2;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(replica != null && replica2 != null){
            return true;
        }
        else{
            return false;
        }
    }

    private Entidad buscarEntidad(String nombre){
        for(Entidad e : entidades){
            if(e.getNombre().equals(nombre)){
                return e;
            }
        }

        return null;
    }

    //GestorReplica
    @Override
    public boolean existeEntidad(String nombre){
        Entidad e = buscarEntidad(nombre);

        if(e == null){
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public int getNumEntidades(){
        return entidades.size();
    }

    //GestorCliente
    @Override
    public String registrarEntidad(String nombre, String pass){
        setReplica();

        String result = "";
        try {
            if(buscarEntidad(nombre) == null && replica.existeEntidad(nombre) == false && replica2.existeEntidad(nombre) == false){
                int esta, rep1, rep2;
                esta = entidades.size();
                rep1 = replica.getNumEntidades();
                rep2 = replica2.getNumEntidades();

                int min = Math.min(esta, rep1);
                min = Math.min(min,rep2);

                if(min == esta){
                    Entidad e = new Entidad(nombre,pass);
                    entidades.add(e);
                    result = nombrePropio;
                }
                else if (min == rep1){
                    gestorReplica.registrarEntidad(nombre, pass);
                    result = nombreReplica;
                }
                else{
                    gestorReplica2.registrarEntidad(nombre, pass);
                    result = nombreReplica2;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String inicarSesion(String nombre, String pass){
        setReplica();

        Entidad e = buscarEntidad(nombre);
        String result = "";

        try {
            if(e == null){
                if(replica.existeEntidad(nombre)){
                    result = gestorReplica.inicarSesion(nombre, pass);
                }
                else if (replica2.existeEntidad(nombre)){
                    result = gestorReplica2.inicarSesion(nombre, pass);
                }
            }
            else if (e.getPass().equals(pass)){
                result = nombrePropio;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean donar(String nombre, double cantidad){
        Entidad e = buscarEntidad(nombre);
        if(e != null){
            e.donar(cantidad);
            try {
                tesoreria.aumentar(cantidad);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public double consultarTotal(String nombre){
        Entidad e = buscarEntidad(nombre);

        if(e == null){
            return -1;
        }

        if(e.getTotalDonado() <= 0){
            return -1;
        }
        
        double total = -1;

        try {
            total = tesoreria.consultarTotal();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return total;
    }

    @Override
    public double consultarBalance(String nombre){
        Entidad e = buscarEntidad(nombre);

        if(e == null){
            return -1;
        }

        if(e.getTotalDonado() <= 0){
            return -1;
        }
        
        double balance = -1;

        try {
            balance = tesoreria.consultarBalance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return balance;
    }

    @Override
    public boolean ejecutarProyecto(ProyectosEnum proyecto, double coste, String usuario){
        boolean result = false;
        try {
            result = tesoreria.decrementar(coste);
            if(result){
                Entidad e = buscarEntidad(usuario);
                e.addProyecto(proyecto);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    @Override
    public int[] consultarProyectos(){
        int[] result = getProyectos();
        
        int[] resultReplica = {0,0,0};
        int[] resultReplica2 = {0,0,0};
        try {
            resultReplica = replica.getProyectos();
            resultReplica2 = replica2.getProyectos();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 3; i++){
            result[i] += (resultReplica[i] + resultReplica2[i]);
        }

        return result;
    }

    @Override
    public int[] getProyectos(){
        int[] result = {0,0,0};
        ArrayList<ProyectosEnum> proyectos;


        for(Entidad e: entidades){
            proyectos = e.getProyectos();

            for(ProyectosEnum p: proyectos){
                if(p == ProyectosEnum.VIVIENDA){
                    result[0]++;
                }
                else if(p == ProyectosEnum.ESCUELA){
                    result[1]++;
                }
                else{
                    result[2]++;
                }
            }
        }

        return result;
    }

}
