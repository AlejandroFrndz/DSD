/*
 * This is sample code generated by rpcgen.
 * These are only templates and you can use them
 * as a guideline for developing your own functions.
 */

#include "calc.h"
#include <ctype.h>

//Función para realizar las llamadas a procedimientos remotos en el modo de operación números
void calcprog_1(char *host, double a, char op, double b, double * result)
{
	CLIENT *clnt;

	#ifndef	DEBUG
		clnt = clnt_create (host, CALCPROG, CALCVER, "tcp");
		if (clnt == NULL) {
			clnt_pcreateerror (host);
			exit (1);
		}
	#endif	/* DEBUG */

	switch (op)
	{
		case '+':
			*result = *add_1(a,b,clnt);
			if(result == (double *) NULL){
				clnt_perror (clnt, "call failed");
			}
		break;

		case '-':
			*result = *sub_1(a,b,clnt);
			if(result == (double *) NULL){
				clnt_perror (clnt, "call failed");
			}
		break;

		case 'x':
			*result = *mul_1(a,b,clnt);
			if(result == (double *) NULL){
				clnt_perror (clnt, "call failed");
			}
		break;

		case '/':
			if(b == 0){
				printf("AVISO: Estas dividiendo por 0\n");
			}
			*result = *div_1(a,b,clnt);
			if(result == (double *) NULL){
				clnt_perror (clnt, "call failed");
			}
		break;
		
		default:
			printf("Operación errónea\n");
		break;
	}

	#ifndef	DEBUG
		clnt_destroy (clnt);
	#endif	 /* DEBUG */

}

//Función para realizar las llamadas a procedimientos remotos en el modo de operación vectores
void calcprog_2(char * host, t_vec a, t_vec b, double c, char op){
	CLIENT *clnt;
	double * e;
	t_vec * result;

	#ifndef	DEBUG
		clnt = clnt_create (host, CALCPROG, CALCVER, "tcp");
		if (clnt == NULL) {
			clnt_pcreateerror (host);
			exit (1);
		}
	#endif	/* DEBUG */

	switch (op)
	{
		case '1':
			result = addv_1(a,b,clnt);
			if(result == (t_vec *) NULL){
				clnt_perror (clnt, "call failed");
			}
		break;

		case '2':
			result = subv_1(a,b,clnt);
			if(result == (t_vec *) NULL){
				clnt_perror (clnt, "call failed");
			}
		break;
		
		case '3':
			e = dot_1(a,b,clnt);
			if(e == (double *) NULL){
				clnt_perror (clnt, "call failed");
			}
		break;

		case '4':
			result = cross_1(a,b,clnt);
			if(result == (t_vec *) NULL){
				clnt_perror (clnt, "call failed");
			}
		break;

		case '5':
			result = mulv_1(a,c,clnt);
			if(result == (t_vec *) NULL){
				clnt_perror (clnt, "call failed");
			}
		break;
	}

	if(op == '3'){
		printf("El resultado de la operación es: %f\n", *e);
	}
	else{
		printf("El vector resultado es: (");
		for(int i = 0; i < 2; i++){
			printf("%f,",result->t_vec_val[i]);
		}
		printf("%f)\n",result->t_vec_val[2]);
	}

	#ifndef	DEBUG
		clnt_destroy (clnt);
	#endif	 /* DEBUG */
}

//Lógica del cliente para el modo números. Recibe los argumentos de entrada al programa
void numberMode(int argc, char *argv[]){
	char *host;
	host = argv[1];

	//Para obtener el número de operaciones a realizar, debemos descontar del número de argumentos el propio nombre del programa, el host y el modo de operación
	int size = (argc-3)/2;
	char * end;

	//Puesto que cada operación consta de 2 operandos, al final hay 1 operando más que operaciones
	double operands[size+1];
	char operators[size];
	double result;

	int j = 0;

	//Por como se requiere el paso de los argumentos, detrás de cada operando está el operador correspondiente
	for(int i = 3; i < argc-1; i+=2){
		//strtod convierte el string pasado a double. En la variable end deposita el caracter de la cadena en el que ha terminado la conversión
		operands[j] = strtod(argv[i], &end);
		//Si este carácter es distinto al de fin de la cadena, significa que el string introducido no era un número, completa o parcialmente
		if(*end != '\0'){
			printf("Alguno de los operandos introducidos no es un número\n");
			exit(-1);
		}
		//La comprobación de la corrección de los operadores la realiza la propia función calcprog_1
		operators[j] = argv[i+1][0];
		j++;
	}
	operands[size] = strtod(argv[argc-1], &end);
	if(*end != '\0'){
		printf("Alguno de los operandos introducidos no es un número\n");
		exit(-1);
	}

	//Se pasan los operandos y la operación a la función para que realice la llamada remota correspondiente
	calcprog_1(host,operands[0],operators[0],operands[1],&result);
	//Si hay más operaciones en la cadena, se sigue operando, pasando como primer operando el resultado de la operación anterior
	for(int i = 1; i < size; i++){
		calcprog_1(host,result,operators[i],operands[i+1],&result);
	}
	//Finalmente se imprime el resultado
	printf("Resultado de la operación: %f\n", result);
}

//Función para leer del terminal las componentes de 1 vector
t_vec readVector(){
	//Se emplea el struct definido en el .x para poder posteriormente pasarlo por la llamada remota
	t_vec vector;
	vector.t_vec_len = 3;
	vector.t_vec_val = (double *)malloc(3*sizeof(double));

	char elements[40];
	double ele;
	int reads = 0;
	do{
		fgets(elements,40,stdin);
		if(sscanf(elements,"%lf",&ele) == 0){
			printf("El elemento introducido no es un número válido\n");
		}
		else{
			vector.t_vec_val[reads] = ele;
			reads++;
		}
	}while(reads < 3);

	return vector;
}

//Lógica del cliente para el modo de operación vectores
void vectorMode(char * host){
	char option[3];
	t_vec vecA, vecB;
	double e;
	int valid = 0;

	//Se presenta el menú de opciones al usuario y se procesa su entrada
	printf("Selecciona la operación a realizar:\n1-Sumar\n2-Restar\n3-Producto Escalar\n4-Producto Vectorial\n5-Producto por un escalar\n");

	while(!valid){
		valid = 1;
		fgets(option,3,stdin);
		switch (option[0])
		{
			case '1':

			case '2':

			case '3':

			case '4':
				//Cualquiera de las 4 primeras opciones require la lectura de 2 vectores
				printf("Introduce los elementos del primer vector\n");
				vecA = readVector();
				printf("Introduce los elementos del segundo vector\n");
				vecB = readVector();
			break;

			case '5':
				//El producto por un escalar requiere 1 vector y 1 escalar
				printf("Introduce los elementos del vector\n");
				vecA = readVector();
				printf("Introduce el escalar\n");
				char elements[10];
				while(1){
					fgets(elements,10,stdin);
					if(sscanf(elements,"%lf",&e) == 0){
						printf("El elemento introducido no es un número válido\n");
					}
					else{
						break;
					}
				}
			break;
			
			default:
				printf("Opción inválida\n");
				valid = 0;
			break;
		}
	}

	//Se llama a la función que realizará las llamadas remotas
	calcprog_2(host, vecA, vecB, e, option[0]);
}

int main (int argc, char *argv[])
{
	//Se procesan los argumentos recibidos, comprobandose su corrección. De no ser correctos, se informa de la forma de uso del programa y se finaliza la ejecución
	if(argc > 2 && argv[2][0] == 'n'){
		if (argc < 5){
			printf("Uso: <host> <modo> <número> <operación> <número> <operación> <número> ...\n");
			exit(-1);
		}
		else if (argc % 2 != 0){
			printf("Uso: <host> <modo> <número> <operación> <número> <operación> <número> ...\n");
			exit(-1);
		}
		numberMode(argc,argv);
	}
	else if(argc > 2 && argv[2][0] == 'v'){
		char * host;
		host = argv[1];
		vectorMode(host);
	}
	else{
		printf("Uso: <host> <modo> <parametros>\n");
	}

	exit (0);
}