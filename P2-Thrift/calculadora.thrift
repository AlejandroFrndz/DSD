exception OperacionInvalida{
   1: string mensaje
}

service Calculadora{
   void ping(),
   double suma(1:double a, 2:double b),
   double resta(1:double a, 2:double b),
   double multiplica(1:double a, 2:double b),
   double divide(1:double a, 2:double b) throws (1:OperacionInvalida exc)
}
