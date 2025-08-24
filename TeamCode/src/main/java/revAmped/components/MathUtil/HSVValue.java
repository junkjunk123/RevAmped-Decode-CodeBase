package revAmped.components.MathUtil;

import androidx.annotation.NonNull;

public class HSVValue {
    private double H;
    private double S;
    private double V;

    public HSVValue(double H, double S, double V) {
        this.H = H;
        this.S = S;
        this.V = V;
    }

    public void setHSV(double h, double s, double v) {
        H = h;
        S = s;
        V = v;
    }

    public void setH(double h) {
        H = h;
    }

    public void setS(double s) {
        S = s;
    }

    public void setV(double v) {
        V = v;
    }

    public double getH() {
        return H;
    }

    public double getS() {
        return S;
    }

    public double getV() {
        return V;
    }

    @NonNull
    @Override
    public String toString() {
        return "H: " + H + " S: " + S + " V: " + V;
    }

    public static float calculateDistance(float h1, float s1, float v1, float h2, float s2, float v2) {
        return (h1 - h2) * (h1 - h2) / 1296 + (s1 - s2) * (s1 - s2) + (v1 - v2) * (v1 - v2);
    }

    public static float findDistanceHue(float h1, float h2) {
        return Math.abs(h1-h2);
    }
}
