from calculadora import Calculadora

from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

class CalculadoraHandler:
    def __init__(self):
        self.log = {}

    def ping(self):
        transport = TSocket.TSocket("localhost", 9091)
        transport = TTransport.TBufferedTransport(transport)
        protocol = TBinaryProtocol.TBinaryProtocol(transport)

        client = Calculadora.Client(protocol)

        transport.open()

        print("Retransmitiendo peticion")
        client.ping()
        transport.close()

    def suma(self, n1, n2):
        transport = TSocket.TSocket("localhost", 9091)
        transport = TTransport.TBufferedTransport(transport)
        protocol = TBinaryProtocol.TBinaryProtocol(transport)

        client = Calculadora.Client(protocol)

        transport.open()

        print("Retransmitiendo peticion")
        result = client.suma(n1,n2)
        transport.close()
        return result

    def resta(self, n1, n2):
        transport = TSocket.TSocket("localhost", 9091)
        transport = TTransport.TBufferedTransport(transport)
        protocol = TBinaryProtocol.TBinaryProtocol(transport)

        client = Calculadora.Client(protocol)

        transport.open()

        print("Retransmitiendo peticion")
        result = client.resta(n1,n2)
        transport.close()
        return result

    def multiplica(self, n1, n2):
        transport = TSocket.TSocket("localhost", 9091)
        transport = TTransport.TBufferedTransport(transport)
        protocol = TBinaryProtocol.TBinaryProtocol(transport)

        client = Calculadora.Client(protocol)

        transport.open()

        print("Retransmitiendo peticion")
        result = client.multiplica(n1,n2)
        transport.close()
        return result

    def divide(self, n1, n2):
        if n2 == 0:
            raise Calculadora.OperacionInvalida("No puedes dividir por 0")

        transport = TSocket.TSocket("localhost", 9091)
        transport = TTransport.TBufferedTransport(transport)
        protocol = TBinaryProtocol.TBinaryProtocol(transport)

        client = Calculadora.Client(protocol)

        transport.open()

        print("Retransmitiendo peticion")
        result = client.divide(n1,n2)
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