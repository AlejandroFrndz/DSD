exception OperacionInvalida{
   1: string mensaje
}

enum Operacion{
    SUMA = 1,
    RESTA = 2,
    MULTIPLICA = 3,
    DIVIDE = 4
}

enum Server{
    RUBY = 1,
    PYTHON = 2
}

struct Paquete{
    1: double a,
    2: double b,
    3: Operacion op
}

service Calculadora{
   string ping(1:Server s),
   string pingServer(),
   double suma(1:double a, 2:double b),
   double resta(1:double a, 2:double b),
   double multiplica(1:double a, 2:double b),
   double divide(1:double a, 2:double b) throws (1:OperacionInvalida exc),
   double operar(1:Paquete p) throws (1:OperacionInvalida exc)
}