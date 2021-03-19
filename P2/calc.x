typedef double t_vec<3>;

program CALCPROG{
    version CALCVER{
        double ADD(double a, double b) = 1;
        double SUB(double a, double b) = 2;
        double MUL(double a, double b) = 3;
        double DIV(double a, double b) = 4;
        t_vec ADDV(t_vec a, t_vec b) = 5;
        t_vec SUBV(t_vec a, t_vec b) = 6;
        double DOT(t_vec a, t_vec b) = 7;
        t_vec CROSS(t_vec a, t_vec b) = 8;
        t_vec MULV(t_vec a, double b) = 9;
    } = 1;
} = 0x2000000f;