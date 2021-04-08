require 'thrift'
require_relative 'calculadora'

#Se encapsula toda la ejecución en un begin-rescue (try-catch de ruby) para detectar cualquier excepción relacionado con la comunicación de Thrift
begin 
    #Se crean los objetos necesarios para la comunicación como cliente
    transport = Thrift::BufferedTransport.new(Thrift::Socket.new('localhost', 9090))
    protocol = Thrift::BinaryProtocol.new(transport)
    client = Calculadora::Client.new(protocol)
 
    transport.open()
    
    #Se realiza primeramente un ping a ambos servidores
    puts "Probando conexión a los servidores: "
    mensaje = client.ping(Server::RUBY)
    puts mensaje

    mensaje = client.ping(Server::PYTHON)
    puts mensaje

    #Se crea la estructura necesaria para la comunicación con el gateway
    paquete = Paquete.new()
    
    begin
        puts "Introduzca la operación a realizar. Las disponibles son: ", "  -Sumar (s)", "  -Restar (r)", "  -Multiplicar (m)", "  -Dividir (d)", "Escriba 'Quit', 'quit' o 'q' para salir"

        #Se procesa la elección de las opciones disponibles
        begin
            valida = true
            salir = false
            operacion = gets.chomp
            case operacion
            when "Sumar", "sumar", "s"
                paquete.op = Operacion::SUMA
                simbolo = "+"
            when "Restar", "restar", "r"
                paquete.op = Operacion::RESTA
                simbolo = "-"
            when "Multiplicar", "multiplicar", "m"
                paquete.op = Operacion::MULTIPLICA
                simbolo = "*"
            when "Dividir", "dividir", "d"
                paquete.op = Operacion::DIVIDE
                simbolo = "/"
            when "Quit", "quit", "q"
                salir = true
            else
                valida = false
                puts "Introduzca una opción válida"
            end
        end while !valida

        #Si se va a realizar una operación, se solicitan ambos operandos comprobando que sean correctos (números)
        if !salir
            puts "Introduzca el primer operando"

            begin
                paquete.a = gets.chomp
                paquete.a = Float(paquete.a)
            rescue ArgumentError
                puts "El operando introducido no es un número"
                retry
            end

            puts "Introduzca el segundo operando"

            begin
                paquete.b = gets.chomp
                paquete.b = Float(paquete.b)
            rescue ArgumentError
                puts "El operando introducido no es un número"
                retry
            end

            #Se envía la operación al gateway para su procesamiento. Se encapsula en un bloque begin-rescue por si la división genera una excepción
            begin
                puts ""
                result = client.operar(paquete)
                print paquete.a , " " , simbolo, " ", paquete.b, " = ", result, "\n\n"
            rescue OperacionInvalida => e
                puts e.mensaje, ""
            end
        end
    end while !salir

    #Cuando se sale, para finalizar se cierra la conexión
    transport.close()
 
rescue Thrift::Exception => tx
    print 'Thrift::Exception: ', tx.message, "\n"
end