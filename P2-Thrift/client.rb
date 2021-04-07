require 'thrift'

require_relative 'calculadora'
 
begin 
    transport = Thrift::BufferedTransport.new(Thrift::Socket.new('localhost', 9090))
    protocol = Thrift::BinaryProtocol.new(transport)
    client = Calculadora::Client.new(protocol)
 
    transport.open()
    
    puts "Probando conexión a los servidores: "
    mensaje = client.ping(Server::RUBY)
    puts mensaje

    mensaje = client.ping(Server::PYTHON)
    puts mensaje

    paquete = Paquete.new()
    
    begin
        puts "Introduzca la operación a realizar. Las disponibles son: ", "  -Sumar (s)", "  -Restar (r)", "  -Multiplicar (m)", "  -Dividir (d)", "Escriba 'Quit', 'quit' o 'q' para salir"

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

            begin
                puts ""
                result = client.operar(paquete)
                print paquete.a , " " , simbolo, " ", paquete.b, " = ", result, "\n\n"
            rescue OperacionInvalida => e
                puts e.mensaje, ""
            end
        end
    end while !salir

    transport.close()
 
rescue Thrift::Exception => tx
    print 'Thrift::Exception: ', tx.message, "\n"
end