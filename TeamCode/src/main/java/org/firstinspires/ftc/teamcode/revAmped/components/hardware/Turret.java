package org.firstinspires.ftc.teamcode.revAmped.components.hardware;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class Turret<T extends Enum<T> & HwMotor2.MotorState> extends HwMotor2<T> {
    protected Turret(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, T motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction, motorState);
    }

    protected Turret(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, DcMotor.RunMode runMode, T motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction, runMode, motorState);
    }

    protected Turret(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, double maxPower, T motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction, maxPower, motorState);
    }

    public abstract int ticksPerRotation();

    public double getError(int targetPosition) {
        return ((targetPosition - currentPos % ticksPerRotation()) + ticksPerRotation() * 1.5) % ticksPerRotation() - ticksPerRotation() / 2.0;
    }
}
