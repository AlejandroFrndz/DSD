from calculadora import Calculadora

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

transport = TSocket.TSocket("localhost", 9090)
transport = TTransport.TBufferedTransport(transport)
protocol = TBinaryProtocol.TBinaryProtocol(transport)

client = Calculadora.Client(protocol)

transport.open()

print("hacemos ping al server")
client.ping()

resultado = client.suma(2.5, 2.5)
print("2.5 + 2.5 = " + str(resultado))
resultado = client.resta(2.5, 2.5)
print("2.5 - 2.5 = " + str(resultado))
resultado = client.multiplica(2.5, 2.5)
print("2.5 * 2.5 = " + str(resultado))

try:
    resultado = client.divide(2.5, 0)
    print("2.5 / 2.5 = " + str(resultado))
except Calculadora.OperacionInvalida as e:
    print(e.mensaje)

resultado = client.divide(2.5, 2.5)
print("2.5 / 2.5 = " + str(resultado))

transport.close()