import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Tesoreria extends UnicastRemoteObject implements TesoreriaI{
    private double balance;
    private double total;
    Tesoreria() throws RemoteException{
        total = 0;
        balance = 0;
    }

    @Override
    public synchronized void aumentar(double donacion){
        total += donacion;
        balance += donacion;
    }

    @Override
    public double consultarTotal(){
        return total;
    }

    @Override
    public double consultarBalance(){
        return balance;
    }

    @Override
    public synchronized boolean decrementar(double cantidad){
        if(balance >= cantidad){
            balance -= cantidad;
            return true;
        }
        else{
            return false;
        }
    }
}
