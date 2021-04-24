import java.rmi.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        String usuario = "";
        String nombre;
        String pass;

        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }

        try {
            GestorClienteI gestor = (GestorClienteI)Naming.lookup("gestor");

            boolean sesion = false;

            System.out.println("Bienvenido al Gestor de Donaciones. Seleccione que desea hacer");

            while(!sesion){
                System.out.println("1- Registrarse");
                System.out.println("2- Inciar Sesión");
                System.out.println("3- Salir");

                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch(opcion){
                    case 1:
                        nombre = "";
                        pass = "";
                        System.out.println("Su registro va a comenzar");

                        while(nombre.equals("")){
                            System.out.print("Introduzca su nombre de usuario: ");
                            nombre = scanner.nextLine();
                        }

                        while(pass.equals("")){
                            System.out.print("Introduzca su contraseña: ");
                            pass = scanner.nextLine();
                        }

                        if(gestor.registrarEntidad(nombre, pass)){
                            usuario = nombre;
                            sesion = true;
                            System.out.println("Su registro se ha completado. Bienvenido " + nombre);
                        }
                        else{
                            System.out.println("Lo sentimos, ya existe un usuario con ese nombre registrado en el sistema. Pruebe a inicar sesión o elija otro nombre");
                        }

                    break;

                    case 2:
                        nombre = "";
                        pass = "";
                        System.out.println("Ha solicitado iniciar sesión");

                        while(nombre.equals("")){
                            System.out.print("Introduzca su nombre de usuario: ");
                            nombre = scanner.nextLine();
                        }

                        while(pass.equals("")){
                            System.out.print("Introduzca su contraseña: ");
                            pass = scanner.nextLine();
                        }

                        if(gestor.inicarSesion(nombre, pass)){
                            usuario = nombre;
                            sesion = true;
                            System.out.println("Bienvenido de nuevo " + nombre);
                        }
                        else{
                            System.out.println("Lo sentimos, este usuario no existe o la contraseña no es correcta. Intentelo de nuevo");
                        }
                    break;

                    case 3:
                        System.out.println("Esperamos verle de nuevo pronto");
                        scanner.close();
                        System.exit(0);
                    break;

                    default:
                        System.out.println("Opción inválida");
                    break;
                }
            }

            boolean seguir = true;

            while(seguir){
                System.out.println("Hola " + usuario + " Qué desea hacer");
                System.out.println("1- Donar");
                System.out.println("2- Consultar Total");
                System.out.println("3- Salir");

                int opcion = scanner.nextInt();

                switch(opcion){
                    case 1:
                        System.out.println("Va usted a realizar una donación.");

                        double cantidad = scanner.nextDouble();
                        scanner.nextLine();


                        while(cantidad <= 0.0){
                            System.out.println("La cantidad donada debe ser mayor que 0");
                            cantidad = scanner.nextDouble();
                            scanner.nextLine();
                        }

                        if(gestor.donar(usuario, cantidad)){
                            System.out.println("Donación realizada con éxito. Muchas gracias");
                        }
                        else{
                            System.out.println("Hubo algún error durante la donación. Por favor inténtelo de nuevo");
                        }
                    break;

                    case 2:
                        double total = gestor.consultarTotal(usuario);

                        if(total == -1){
                            System.out.println("Lo sentimos, pero usted debe realizar al menos 1 donación antes de poder consultar el total");
                        }
                        else{
                            System.out.println("Hasta ahora hemos recaudado: " + total + " Gracias por su ayuda");
                        }
                    break;

                    case 3:
                        System.out.println("Esperamos verle de nuevo pronto");
                        seguir = false;
                    break;
                    
                    default:
                        System.out.println("Opción inválida");
                    break;
                }
            }
            scanner.close();
        }
        catch (Exception e) {
            System.err.println("Excepcion del sistema:");
            e.printStackTrace();
        }
    }
}