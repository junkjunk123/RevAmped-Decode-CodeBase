package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.TwoMotorLift;
import revAmped.components.SlideController.STD.CustomSTDCoefficients;
import revAmped.components.SlideController.STD.STDController;

public class VslideMotor extends TwoMotorLift<VslideMotor.VslideMotorState> {
    private final STDController controller = new STDController(new CustomSTDCoefficients(1.2, 0.05, 0.0002, 1.5), Constants.VSLIDE_HIGH_BASKET_DEPO);
    private final STDController specIntake = new STDController(new CustomSTDCoefficients(0.5, 0.05, 0.0001, 0.5), Constants.VSLIDE_HIGH_BASKET_DEPO);
    public static final int VSLIDE_HIGH_BASKET_DEPO = Constants.VSLIDE_HIGH_BASKET_DEPO;
    public static final int VSLIDE_LOW_BASKET_DEPO = Constants.VSLIDE_LOW_BASKET_DEPO;
    public static final int VSLIDE_SPEC_DEPO = Constants.VSLIDE_SPEC_DEPO;
    public static final int VSLIDE_SPEC_INTAKE = Constants.VSLIDE_SPEC_INTAKE;

    @Override
    public void buildPowerMap() {
        powerMap.put(VslideMotorState.IN, () -> 0);
        powerMap.put(VslideMotorState.BASKET, () -> controller.run(getCurrentPosition()));
        powerMap.put(VslideMotorState.SPEC, () -> specIntake.run(getCurrentPosition()));
        powerMap.put(VslideMotorState.RETRACTION, () -> controller.run(getCurrentPosition()));
    }

    @Override
    public void buildInitMap() {
        initMap.put(VslideMotorState.IN, new InitActions());
        initMap.put(VslideMotorState.BASKET, new InitActions());
        initMap.put(VslideMotorState.RETRACTION, new InitActions());
        initMap.put(VslideMotorState.SPEC, new InitActions());
    }

    @Override
    public void updateController(int targetPosition) {
        controller.setTargetPosition(targetPosition);
        specIntake.setTargetPosition(targetPosition);
    }

    public enum VslideMotorState implements SlideState {
        RETRACTION(0),
        BASKET(VSLIDE_HIGH_BASKET_DEPO),
        SPEC(VSLIDE_SPEC_INTAKE),
        IN(0);

        private final int position;

        VslideMotorState(int position) {
            this.position = position;
        }

        @Override
        public int position() {
            return position;
        }

        @Override
        public boolean isRetraction() {
            return this == RETRACTION;
        }
    }

    public VslideMotor(HardwareMap hardwareMap) {
        super(hardwareMap, "vslides", "vslides2", Constants.VSLIDE_DIRECTION, Constants.VSLIDE2_DIRECTION, () -> false, VslideMotorState.IN, VslideMotorState.IN);
        retractionDetector = this::retracted;
        init();
    }

    public boolean retracted() {
        return getVelocity() < 0.75 && getCurrentPosition() < 5;
    }

    @Override
    public void init() {
        setTargetPosition(0);
        super.init();
    }

    @Override
    public int getTargetPosition() {
        return controller.getTargetPosition();
    }

    @Override
    public Class<VslideMotorState> getStateClass() {
        return VslideMotorState.class;
    }

    public void retraction() {
        setCurrentState(VslideMotorState.RETRACTION);
    }
}
