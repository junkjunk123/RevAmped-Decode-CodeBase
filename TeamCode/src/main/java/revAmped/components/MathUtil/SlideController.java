package revAmped.components.MathUtil;

import revAmped.components.MathUtil.STD.CustomSTDCoefficients;

public interface SlideController {
    void updateConstants(SlideConstants constants);

    void updateConstants(CustomSTDCoefficients constants);

    void runToPosition(int pos);
    default void runToPosition(int pos, double parameter) {runToPosition(pos);}
    default void setHomed() {}
    default void setTargetPosition(int pos) {runToPosition(pos);}

    interface SlideConstants {

    }
}
