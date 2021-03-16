/*
 * This is sample code generated by rpcgen.
 * These are only templates and you can use them
 * as a guideline for developing your own functions.
 */

#include "calc.h"

double * add_1_svc(double a, double b,  struct svc_req *rqstp)
{
	static double  result;

	result = a + b;

	return &result;
}

double * sub_1_svc(double a, double b,  struct svc_req *rqstp)
{
	static double  result;

	result = a - b;

	return &result;
}

double * mul_1_svc(double a, double b,  struct svc_req *rqstp)
{
	static double  result;

	result = a * b;

	return &result;
}

double * div_1_svc(double a, double b,  struct svc_req *rqstp)
{
	static double  result;

	result = a / b;

	return &result;
}
