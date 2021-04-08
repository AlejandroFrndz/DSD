from calculadora import Calculadora

from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

class CalculadoraHandler:
    def __init__(self):
        self.log = {}

    def pingServer(self):
        print("Me han pingeado")
        return "Python Server OK"

    def multiplica(self, n1, n2):
        print("Multiplicando: " + str(n1) + " * " + str(n2))
        result = n1 * n2
        return result

    def divide(self, n1, n2):
        if n2 == 0:
            raise Calculadora.OperacionInvalida("No puedes dividir por 0") #Si se intenta dividir por 0, se levanta una excepcion antes de realizar ningun procesamiento en el servidor

        print("Dividiendo: " + str(n1) + " / " + str(n2))
        result = n1 / n2
        return result

if __name__ == '__main__':
    handler = CalculadoraHandler()
    processor = Calculadora.Processor(handler)
    transport = TSocket.TServerSocket(host='127.0.0.1', port=9092)
    tfactory = TTransport.TBufferedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()

    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

    print('Starting the server...')
    server.serve()
    print('done.')