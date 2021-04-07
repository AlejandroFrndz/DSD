require 'thrift'
require_relative 'calculadora'

class Handler
    def pingServer()
        print "Me han pingeado\n"
        return "Ruby Server OK"
    end

    def suma(a,b)
        print "Sumando: ", a , " + " , b , "\n"
        return a + b
    end

    def resta(a,b)
        print "Restando: ", a , " - " , b , "\n"
        return a - b
    end
end

handler = Handler.new()
processor = Calculadora::Processor.new(handler)
transport = Thrift::ServerSocket.new(9091)
transportFactory = Thrift::BufferedTransportFactory.new()
server = Thrift::SimpleServer.new(processor, transport, transportFactory)

puts "Starting the server..."
server.serve()