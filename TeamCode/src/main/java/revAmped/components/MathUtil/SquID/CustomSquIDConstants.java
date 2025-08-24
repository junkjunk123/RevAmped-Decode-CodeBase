package revAmped.components.MathUtil.SquID;

public class CustomSquIDConstants {
    private double kP, kL, kF;

    public CustomSquIDConstants(double kP, double kL, double kF) {
        this.kP = kP;
        this.kL = kL;
        this.kF = kF;
    }

    public double getkP() {
        return kP;
    }

    public double getkF() {
        return kF;
    }

    public double getkL() {
        return kL;
    }

    @Override
    public String toString() {
        return "CustomSquIDConstants{" +
                "kP=" + kP +
                ", kL=" + kL +
                ", kF=" + kF +
                '}';
    }

    public void setkF(double kF) {
        this.kF = kF;
    }

    public void setkL(double kL) {
        this.kL = kL;
    }

    public void setkP(double kP) {
        this.kP = kP;
    }

    public void setConstants(double kP, double kL, double kF) {
        this.kP = kP;
        this.kL = kL;
        this.kF = kF;
    }
}
