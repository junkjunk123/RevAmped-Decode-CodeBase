package revAmped.components.MathUtil;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.pedropathing.localization.Matrix;

import revAmped.util.Range;

public class Vector3 implements Cloneable {
    public double x;
    public double y;
    public double z;
    private double magcache;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3() {
        x=y=z=0;
    }

    public Pose2d toPose() {
        return new Pose2d(x, y, z);
    }

    public void add(Vector3 a) {
        x += a.x;
        y += a.y;
        z += a.z;
        magcache = 0;
    }
    public void subtract(Vector3 a) {
        x -= a.x;
        y -= a.y;
        z -= a.z;
        magcache = 0;
    }
    public double getMag() {
        if (magcache == 0 && ( x != 0 || y != 0 || z != 0)) {
            magcache = Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
        }
        return magcache;
    }

    public void mul(double a) {
        x *=a;
        y *= a;
        z *= a;
        magcache *= a;
    }

    public void norm() {
        double mag = getMag();
        x /= mag;
        y /= mag;
        z /= mag;
        magcache = 1;
    }


    public static Vector3 add(Vector3 a, Vector3 b) {
        return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
    }
    public static Vector3 subtract(Vector3 a, Vector3 b) {
        return new Vector3(a.x-b.x,a.y-b.y,a.z-b.z);
    }
    public static Vector3 mul(Vector3 a, double b) {
        return new Vector3(a.x*b, a.y*b, a.z*b);
    }

    public static double dot(Vector3 a, Vector3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }
    public static Vector3 project(Vector3 v, Vector3 u) { // projects v onto u
        double component = Vector3.dot(v,u)/Vector3.dot(u,u);
        Vector3 temp = new Vector3(u.x,u.y,u.z);
        temp.mul(component);
        return temp;
    }

    public String toString() {
        return String.format("(%f, %f, %f)", x, y, z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3 multiplyByMatrix(Matrix matrix) {
        Vector3 result = new Vector3();
        double[][] transformation = matrix.getMatrix();

        for (int i = 0; i < transformation.length; i++) {
            double sum = 0;
            for (int j = 0; j < 3; j++) {
                sum += transformation[i][j]*get(j);
            }
            result.set(i, sum);
        }

        return result;
    }

    public double get(int j) {
        if (j == 0) {
            return getX();
        } else if (j == 1) {
            return getY();
        } else {
            return getZ();
        }
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void set(int j, double set) {
        if (j == 0) {
            setX(set);
        } else if (j == 1) {
            setY(set);
        } else {
            setZ(set);
        }
    }

    @Override
    public Vector3 clone() {
        return new Vector3(x, y, z);
    }
}