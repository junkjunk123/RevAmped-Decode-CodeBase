package revAmped.util;


import revAmped.util.Range;

/**
 * Math operations for Move to Point functions
 */
public class MovementMath {

    private static final float kPT = -0.018f, kIT = -0.03f, kDT = -0.0016f;
    private static final float kP = 0.036f, kI = 0.015f, kD = 0.003f;

    private static final float MAX_I = 0.09f;
    private static final float MAX_IH = 0.02f;
    private static final float MAX_D = 0.2f;
    private static final float MAX_DH = 0.1f;

    public static float getP(float error, boolean drive) {
        return error * (drive? kP : kPT);
    }

    public static float getI(float timeChange, float errorX, float errorY, boolean drive) {
        float i;
        if (drive) {
            float currentErrorX = errorX * timeChange;
            float currentErrorY = errorY * timeChange;
            float currentError = (float) Math.sqrt(Math.pow(currentErrorX, 2) + Math.pow(currentErrorY, 2));
            i = currentError * kI;
            if (Math.signum(i) == 1) {
                i = Math.min(i, MAX_I);
            } else {
                i = Math.max(i, -MAX_I);
            }
        } else {
            i = errorX * kIT;
            if (Math.signum(i) == 1) {
                i = Math.min(i, MAX_IH);
            } else {
                i = Math.max(i, -MAX_IH);
            }
        }

        return i;
    }

    public static float getD(float error, float pastError, float timeChange, boolean drive) {
        float d = -((drive? kD : kDT) * (error - pastError) / timeChange);
        if (Math.signum(d) == 1) {
            d = Math.min(d, (drive? MAX_D: MAX_DH));
        } else {
            d = Math.max(d, (drive? -MAX_D : -MAX_DH));
        }

        return d;
    }

    public static float getPID(float p, float i, float d, float maxPower) {
        return Range.clip(p+i+d, -maxPower, maxPower);
    }

    public static float getScaleFactor(float errorX, float errorY) {
        float sF;
        if (Math.signum(errorY / errorX) == 1) {
            sF = (float) Math.tan((Math.PI / 4) - Math.atan(errorX / errorY));
        } else {
            sF = (float) Math.tan((Math.PI / 4) + Math.atan(errorX / errorY));
        }

        return sF;
    }

}
