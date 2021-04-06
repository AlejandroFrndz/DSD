require 'thrift'
require_relative 'calculadora'

class Handler
    def ping()
        print "Me han pingeado\n"
    end

    def suma(a,b)
        print "Sumando: ", a , " " , b , "\n"
        return a + b
    end

    def resta(a,b)
        print "Restando: ", a , " " , b , "\n"
        return a - b
    end

    def multiplica(a,b)
        print "Multiplicando: ", a , " " , b , "\n"
        return a * b
    end

    def divide(a,b)
        if( b == 0)
            e = OperacionInvalida.new()
            e.mensaje = "No se puede dividir por 0"
            raise e 
        end
        print "Dividiendo: ", a , " " , b , "\n"
        return a / b
    end
end

handler = Handler.new()
processor = Calculadora::Processor.new(handler)
transport = Thrift::ServerSocket.new(9091)
transportFactory = Thrift::BufferedTransportFactory.new()
server = Thrift::SimpleServer.new(processor, transport, transportFactory)

puts "Starting the server..."
server.serve()