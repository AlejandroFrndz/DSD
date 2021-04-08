from calculadora import Calculadora

from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

class CalculadoraHandler:
    def __init__(self):
        self.log = {}

    def ping(self,s):
        server = Calculadora.Server() #Instanciamos la enumeracion para poder usar sus valores
        #En funcion del servidor al que se desee pingear se abre una conexion a uno u otro puerto
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
        #Al recoger la peticion, se le pone ademas el estado del gateway
        mensaje += "--Python Gateway OK"
        transport.close()
        return mensaje

    def operar(self,paquete):
        operacion = Calculadora.Operacion()

        #Si la operacion es suma o resta se abre una comunicacion con el servidor en ruby
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
            #Si no, con el servidor en python
            transport = TSocket.TSocket("localhost", 9092)
            transport = TTransport.TBufferedTransport(transport)
            protocol = TBinaryProtocol.TBinaryProtocol(transport)

            client = Calculadora.Client(protocol)

            transport.open()

            print("Retransmitiendo peticion")
            if paquete.op == operacion.MULTIPLICA:
                result = client.multiplica(paquete.a,paquete.b)
            else:
                #Si es una division lo solicitado, se debe manejar la posible excepcion al dividir por 0
                try:
                    result = client.divide(paquete.a, paquete.b)
                except Calculadora.OperacionInvalida as e:
                    raise Calculadora.OperacionInvalida(e.mensaje) #Si se produce una excepcion, se recoge y se levanta una nueva con el mismo mensaje para el cliente original
            transport.close()
            return result
    

if __name__ == '__main__':
    #Se crean los objetos necesarios para actuar como servidor y se comienza a escuchar el puerto. Cuando se reciba una peticion esta sera gestionada por el handler
    handler = CalculadoraHandler()
    processor = Calculadora.Processor(handler)
    transport = TSocket.TServerSocket(host='127.0.0.1', port=9090)
    tfactory = TTransport.TBufferedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()

    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

    print('Starting the server...')
    server.serve()
    print('done.')