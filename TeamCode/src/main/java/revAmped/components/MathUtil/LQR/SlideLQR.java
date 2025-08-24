package revAmped.components.MathUtil.LQR;

import com.pedropathing.localization.Matrix;

public class SlideLQR {
    private Matrix A_d, B_d, Q, Rmat, K;

    /**
     * posTol, velTol: state tolerances; Rmult: control tolerance; dt: loop time (s).
     */
    public SlideLQR(double posTol, double velTol, double Rmult, double dt) {
        // State weighting Q by Bryson's rule
        Q = new Matrix(2, 2);
        Q.set(0, 0, 1.0/(posTol*posTol));  // weight for position
        Q.set(0, 1, 0);
        Q.set(1, 0, 0);
        Q.set(1, 1, 1.0/(velTol*velTol));  // weight for velocity

        // Control weight R (1x1) by Bryson's rule (max power = Rmult)
        double Rval = 1.0/(Rmult*Rmult);
        Rmat = new Matrix(1, 1);
        Rmat.set(0, 0, Rval);

        // Continuous-time A, B for [pos; vel] with u = acceleration
        Matrix A = new Matrix(2, 2);
        A.set(0, 0, 0); A.set(0, 1, 1);
        A.set(1, 0, 0); A.set(1, 1, 0);
        Matrix B = new Matrix(2, 1);
        B.set(0, 0, 0);
        B.set(1, 0, 5600);

        // Discretize A,B: A_d = I + A*dt, B_d = [0.5*dt^2; dt]
        A_d = new Matrix(2, 2);
        A_d.set(0, 0, 1); A_d.set(0, 1, dt);
        A_d.set(1, 0, 0); A_d.set(1, 1, 1);
        B_d = new Matrix(2, 1);
        B_d.set(0, 0, 0.5*dt*dt);
        B_d.set(1, 0, dt);

        // Solve discrete Algebraic Riccati Equation (DARE) by iteration:
        Matrix P = new Matrix(Q);  // initial P = Q
        Matrix At = A_d.transposed();
        for (int i = 0; i < 100; i++) {
            // Compute A_d^T P A_d and B_d^T P B_d
            Matrix AtP = Matrix.multiply(At, P);
            Matrix AtPA = Matrix.multiply(AtP, A_d);
            Matrix P_B = Matrix.multiply(P, B_d);
            Matrix BTP = Matrix.multiply(B_d.transposed(), P);
            double BTPB = Matrix.multiply(BTP, B_d).get(0, 0);
            double S = Rval + BTPB;
            double invS = 1.0 / S;

            // Compute the gain term (A^T P B * invS * B^T P A)
            Matrix AtPB = Matrix.multiply(At, P_B);      // 2x1 = A^T * P * B
            Matrix BTPA = Matrix.multiply(BTP, A_d);     // 1x2 = B^T * P * A
            Matrix term = Matrix.multiply(AtPB, BTPA);   // 2x2
            term.multiply(invS);

            // Riccati update: P_next = A^T P A - term + Q
            Matrix Pnext = new Matrix(AtPA);
            Pnext.minus(term);
            Pnext.plus(Q);

            // Check for convergence (optional)
            double diff = 0;
            for (int r = 0; r < 2; r++)
                for (int c = 0; c < 2; c++)
                    diff += Math.abs(Pnext.get(r,c) - P.get(r,c));
            P = Pnext;
            if (diff < 1e-9) break;
        }
        // Compute final gain K = (R + B^T P B)^{-1} B^T P A
        Matrix PAd = Matrix.multiply(P, A_d);
        Matrix BTPA = Matrix.multiply(Matrix.multiply(B_d.transposed(), P), A_d);  // 1x2
        double denom = Rval + Matrix.multiply(Matrix.multiply(B_d.transposed(), P), B_d).get(0, 0);
        double invDen = 1.0/denom;
        K = new Matrix(BTPA);
        K.multiply(invDen);  // 1x2 matrix

        // (Optional) verify K is 1x2
    }

    /** Compute control u for current state (pos, vel) and target pos (r). */
    public double update(double error, double vel) {
        // Form error state = [pos - targetPos; vel - 0]
        Matrix xerr = new Matrix(2, 1);
        xerr.set(0, 0, -error);
        xerr.set(1, 0, vel);
        // u = -K * xerr  (K is 1x2, xerr is 2x1)
        Matrix raw = Matrix.multiply(K, xerr);
        double u = - raw.get(0, 0);
        // Clamp to [-1,1]
        if (u > 1.0) u = 1.0;
        if (u < -1.0) u = -1.0;
        return u;
    }
}