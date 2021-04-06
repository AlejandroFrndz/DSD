from calculadora import Calculadora

from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

class CalculadoraHandler:
    def __init__(self):
        self.log = {}

    def ping(self):
        print("Me han pingeado")

    def suma(self, n1, n2):
        print('suma(%.2f,%.2f)' % (n1, n2))
        return n1 + n2

    def resta(self, n1, n2):
        print('resta(%.2f,%.2f)' % (n1, n2))
        return n1 - n2

    def multiplica(self, n1, n2):
        print('multiplica(%.2f,%.2f)' % (n1, n2))
        return n1 * n2

    def divide(self, n1, n2):
        print('divide(%.2f,%.2f)' % (n1, n2))

        if n2 == 0:
            raise Calculadora.OperacionInvalida("No puedes dividir por 0")

        return n1 / n2

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