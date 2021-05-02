import java.rmi.*;
import java.util.Scanner;

public class Client implements Runnable{
    
    public static void main(String[] args){
        new Thread(new Client()).start();
    }

    @Override
    public void run(){
        Scanner scanner = new Scanner(System.in);
        String usuario = "";
        String nombre;
        String pass;
        String nombreServer = "gestor1";

        if(System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }

        try {
            GestorClienteI gestor = (GestorClienteI)Naming.lookup(nombreServer);

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

                        nombreServer = gestor.registrarEntidad(nombre, pass);

                        if(!nombreServer.equals("")){
                            usuario = nombre;
                            gestor = (GestorClienteI)Naming.lookup(nombreServer);
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

                        nombreServer = gestor.inicarSesion(nombre, pass);
                        if(!nombreServer.equals("")){
                            usuario = nombre;
                            gestor = (GestorClienteI)Naming.lookup(nombreServer);
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

            System.out.println("Hola " + usuario + " Qué desea hacer");
            while(seguir){
                System.out.println("1- Donar");
                System.out.println("2- Consultar Cuentas");
                System.out.println("3- Ejecutar Proyecto");
                System.out.println("4- Consultar Proyectos");
                System.out.println("5- Salir");

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
                        double balance = gestor.consultarBalance(usuario);

                        if(total == -1){
                            System.out.println("Lo sentimos, pero usted debe realizar al menos 1 donación antes de poder consultar el total");
                        }
                        else{
                            System.out.println("Hasta ahora hemos recaudado: " + total + "€");
                            System.out.println("Actualmente disponemos de " + balance + "€");
                            System.out.println("Gracias por su ayuda");
                        }
                    break;

                    case 3:

                        double disponible = gestor.consultarBalance(usuario);

                        if(disponible == -1){
                            System.out.println("Lo sentimos, pero usted debe realizar al menos 1 donación antes de poder ejecutar proyectos");
                        }
                        else{

                            System.out.println("Los proyectos realizables son");
                            System.out.println("1- Vivienda -- 20.000€");
                            System.out.println("2- Escuela -- 100.000€");
                            System.out.println("3- Hospital -- 500.000€");

                            opcion = scanner.nextInt();
                            scanner.nextLine();

                            ProyectosEnum proyecto = null;
                            double coste = 0;

                            switch(opcion){
                                case 1:
                                    proyecto = ProyectosEnum.VIVIENDA;
                                    coste = 20000;
                                break;

                                case 2:
                                    proyecto = ProyectosEnum.ESCUELA;
                                    coste = 100000;
                                break;

                                case 3:
                                    proyecto = ProyectosEnum.HOSPITAL;
                                    coste = 500000;
                                break;

                                default:
                                    proyecto = null;
                                break;
                            }

                            if(proyecto != null){
                                if(disponible < coste){
                                    System.out.println("Lo sentimos, pero actualmente solo disponemos de " + disponible + " €. No podemos permitirnos ese proyecto");
                                }
                                else{
                                    gestor.ejecutarProyecto(proyecto, coste, usuario);
                                }
                            }
                        }
                    break;

                    case 4:
                        int[] proyectos = gestor.consultarProyectos();

                        System.out.println("Hasta el momento hemos construido");
                        System.out.println(proyectos[0] + " Viviendas");
                        System.out.println(proyectos[1] + " Escuelas");
                        System.out.println(proyectos[2] + " Hospitales");
                        System.out.println("Gracias por su cooperación");
                    break;

                    case 5:
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