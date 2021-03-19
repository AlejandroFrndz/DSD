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

t_vec * addv_1_svc(t_vec a, t_vec b,  struct svc_req *rqstp)
{
	static t_vec  result;

	result.t_vec_len = 3;
	result.t_vec_val = (double *)malloc(3*sizeof(double));
	for(int i = 0; i < 3; i++){
		result.t_vec_val[i] = a.t_vec_val[i] + b.t_vec_val[i];
	}

	return &result;
}

t_vec * subv_1_svc(t_vec a, t_vec b,  struct svc_req *rqstp)
{
	static t_vec  result;

	result.t_vec_len = 3;
	result.t_vec_val = (double *)malloc(3*sizeof(double));
	for(int i = 0; i < 3; i++){
		result.t_vec_val[i] = a.t_vec_val[i] - b.t_vec_val[i];
	}

	return &result;
}

double * dot_1_svc(t_vec a, t_vec b,  struct svc_req *rqstp)
{
	static double  result;

	for(int i = 0; i < 3; i++){
		result += a.t_vec_val[i] * b.t_vec_val[i];
	}

	return &result;
}

t_vec * cross_1_svc(t_vec a, t_vec b,  struct svc_req *rqstp)
{
	static t_vec  result;

	result.t_vec_len = 3;
	result.t_vec_val = (double *)malloc(3*sizeof(double));
	
	result.t_vec_val[0] = a.t_vec_val[1] * b.t_vec_val[2] - a.t_vec_val[2] * b.t_vec_val[1];
	result.t_vec_val[1] = a.t_vec_val[2] * b.t_vec_val[0] - a.t_vec_val[0] * b.t_vec_val[2]; 
	result.t_vec_val[2] = a.t_vec_val[0] * b.t_vec_val[1] - a.t_vec_val[1] * b.t_vec_val[0];

	return &result;
}

t_vec * mulv_1_svc(t_vec a, double b,  struct svc_req *rqstp)
{
	static t_vec  result;
	
	result.t_vec_len = 3;
	result.t_vec_val = (double *)malloc(3*sizeof(double));
	for(int i = 0; i < 3; i++){
		result.t_vec_val[i] = a.t_vec_val[i] * b;
	}

	return &result;
}
