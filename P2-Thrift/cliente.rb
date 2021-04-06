require 'thrift'

require_relative 'calculadora'
 
begin 
    transport = Thrift::BufferedTransport.new(Thrift::Socket.new('localhost', 9090))
    protocol = Thrift::BinaryProtocol.new(transport)
    client = Calculadora::Client.new(protocol)
 
    transport.open()
 
    client.ping()

    result = client.suma(2.5,2.5)

    puts result

    transport.close()
 
rescue Thrift::Exception => tx
    print 'Thrift::Exception: ', tx.message, "\n"
end