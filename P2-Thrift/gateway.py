from calculadora import Calculadora

from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

class CalculadoraHandler:
    def __init__(self):
        self.log = {}

    def ping(self,s):
        server = Calculadora.Server()
        if s == server.RUBY:
            transport = TSocket.TSocket("localhost", 9091)
        else:
            transport = TSocket.TSocket("localhost", 9092)
        transport = TTransport.TBufferedTransport(transport)
        protocol = TBinaryProtocol.TBinaryProtocol(transport)

        client = Calculadora.Client(protocol)

        transport.open()

        print("Retransmitiendo peticion")
        mensaje = client.pingServer()
        mensaje += "--Python Gateway OK"
        transport.close()
        return mensaje

    def operar(self,paquete):
        operacion = Calculadora.Operacion()
        if paquete.op == operacion.SUMA or paquete.op == operacion.RESTA:
            transport = TSocket.TSocket("localhost", 9091)
            transport = TTransport.TBufferedTransport(transport)
            protocol = TBinaryProtocol.TBinaryProtocol(transport)

            client = Calculadora.Client(protocol)

            transport.open()

            print("Retransmitiendo peticion")
            if paquete.op == operacion.SUMA:
                result = client.suma(paquete.a,paquete.b)
            else:
                result = client.resta(paquete.a,paquete.b)
            transport.close()
            return result
        else:
            transport = TSocket.TSocket("localhost", 9092)
            transport = TTransport.TBufferedTransport(transport)
            protocol = TBinaryProtocol.TBinaryProtocol(transport)

            client = Calculadora.Client(protocol)

            transport.open()

            print("Retransmitiendo peticion")
            if paquete.op == operacion.MULTIPLICA:
                result = client.multiplica(paquete.a,paquete.b)
            else:
                try:
                    result = client.divide(paquete.a, paquete.b)
                except Calculadora.OperacionInvalida as e:
                    raise Calculadora.OperacionInvalida(e.mensaje)
            transport.close()
            return result
    

if __name__ == '__main__':
    handler = CalculadoraHandler()
    processor = Calculadora.Processor(handler)
    transport = TSocket.TServerSocket(host='127.0.0.1', port=9090)
    tfactory = TTransport.TBufferedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()

    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

    print('Starting the server...')
    server.serve()
    print('done.')