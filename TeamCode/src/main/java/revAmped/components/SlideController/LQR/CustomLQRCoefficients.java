package revAmped.components.SlideController.LQR;

import java.util.Objects;

import revAmped.components.SlideController.SlideController;

public class CustomLQRCoefficients implements SlideController.SlideConstants {
    public double posTol;
    public double velTol;
    public double RMult;

    public CustomLQRCoefficients(double posTol, double velTol, double RMult) {
        this.posTol = posTol;
        this.velTol = velTol;
        this.RMult = RMult;
    }

    public void setPosTol(double posTol) {
        this.posTol = posTol;
    }

    public void setVelTol(double velTol) {
        this.velTol = velTol;
    }

    public void setRMult(double RMult) {
        this.RMult = RMult;
    }

    public double getPosTol() {
        return posTol;
    }

    public double getVelTol() {
        return velTol;
    }

    public double getRMult() {
        return RMult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomLQRCoefficients that)) return false;
        return Double.compare(getPosTol(), that.getPosTol()) == 0 && Double.compare(getVelTol(), that.getVelTol()) == 0 && Double.compare(getRMult(), that.getRMult()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPosTol(), getVelTol(), getRMult());
    }
}
