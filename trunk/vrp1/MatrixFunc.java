package ec.app.vrp1;


class MatrixFunc
{

    private static void matrixSizeError( float[][] a, float[][] b )
    {
	System.out.println("Matrix sizes are:");
	System.out.println("a[" + a.length + "][" + a[0].length + "]");
	System.out.println("b[" + b.length + "][" + b[0].length + "]");
	System.out.println("first dimension of 'a' must be equal to second dimension of 'b'");
    }


    static void printMatrix( float[][] mat )
    {
	int M = mat.length;
	int N = mat[0].length;

	for (int i = 0; i < M; i++) {
	    for (int j = 0; j < N; j++) {
		System.out.print( mat[i][j] + " ");
	    }
	    System.out.println();
	}
    } // printMatrix


    /**
       A basic matrix multiply

       C = A * B

       Notation: row x column

       Where A is an p x q  matrix
       Where B is a  q x r matrix
       and the result C is p x r

       So, the first dimension of matrix A must equal the second
       dimension of B.
     */
    static float[][] matrixMult( float[][] a, float[][] b )
    {
	int rowsA = a.length;        // p
	int columnsA = a[0].length;  // q
	int rowsB = b.length;        // q
	int columnsB = b[0].length;  // r

	float c[][] = null;
	
	if (columnsA == rowsB) {
	    c = new float[rowsA][columnsB];
	    for (int i = 0; i < rowsA; i++) {
		for (int j = 0; j < columnsB; j++) {
		    c[i][j] = 0;
		    for (int k = 0; k < columnsA; k++) {
			c[i][j] = c[i][j] + a[i][k] * b[k][j];
		    }
		}
	    }
	}
	else {
	    matrixSizeError( a, b );
	}

	return c;
    } // matrixMult

} // MatrixFunc
