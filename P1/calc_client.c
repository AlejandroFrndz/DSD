/*
 * This is sample code generated by rpcgen.
 * These are only templates and you can use them
 * as a guideline for developing your own functions.
 */

#include "calc.h"


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
			else{
				printf("El resultado de la suma es: %f\n", *result);
			}
		break;

		case '-':
			*result = *sub_1(a,b,clnt);
			if(result == (double *) NULL){
				clnt_perror (clnt, "call failed");
			}
			else{
				printf("El resultado de la resta es: %f\n", *result);
			}
		break;

		case 'x':
			*result = *mul_1(a,b,clnt);
			if(result == (double *) NULL){
				clnt_perror (clnt, "call failed");
			}
			else{
				printf("El resultado de la multiplicación es: %f\n", *result);
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
			else{
				printf("El resultado de la división es: %f\n", *result);
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

void numberMode(int argc, char *argv[]){
	char *host;
	host = argv[1];

	int size = (argc-3)/2;
	char * end;

	double operands[size+1];
	char operators[size];
	double result;

	int j = 0;

	for(int i = 3; i < argc-1; i+=2){
		operands[j] = strtod(argv[i], &end);
		if(*end != '\0'){
			printf("Alguno de los operandos introducidos no es un número\n");
			exit(-1);
		}
		operators[j] = argv[i+1][0];
		j++;
	}
	operands[size] = strtod(argv[argc-1], &end);
	if(*end != '\0'){
		printf("Alguno de los operandos introducidos no es un número\n");
		exit(-1);
	}

	calcprog_1(host,operands[0],operators[0],operands[1],&result);

	for(int i = 1; i < size; i++){
		calcprog_1(host,result,operators[i],operands[i+1],&result);
	}

	printf("%f\n", result);
}

int main (int argc, char *argv[])
{
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
		printf("Stay tuned for vectorial operations\n");
	}
	else{
		printf("Uso: <host> <modo> <parametros>\n");
	}

	exit (0);
}

