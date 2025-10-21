package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Opmodes.OnePersonShooterCalibrator;
/*
Eric Zhao
10/20/2025
Hood Servo Hardware class for Prototyping Teleop
 */
public class HoodServo extends HwServo2<HoodServo.HoodState> {
    //Constructors
    public HoodServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "hood", setPosition, HoodState.CLOSE, ServoBrand.AXON_MAX);
    }

    public HoodServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "hood", initialPosition, HoodState.CLOSE, ServoBrand.AXON_MAX);
    }
    //State enum
    public enum HoodState implements ServoState{
        FAR,
        MEDIUM,
        CLOSE;


        @Override
        public float getPosition() {
            switch (this){
                case CLOSE -> {return OnePersonShooterCalibrator.HoodPositionClose/255;}
                case MEDIUM -> {return OnePersonShooterCalibrator.HoodPositionMedium/255;}
                case FAR -> {return OnePersonShooterCalibrator.HoodPositionFar/255;}
            }
            return 0.5f;
        }
    }
}
