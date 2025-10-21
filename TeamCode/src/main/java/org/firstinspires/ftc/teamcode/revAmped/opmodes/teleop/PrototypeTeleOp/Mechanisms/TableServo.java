package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
/*
Eric Zhao
10/20/2025
Turntable Servo class for Prototyping Teleop
Uses a normal (NOT CR) servo
 */
public class TableServo extends HwServo2<TableServo.TableState> {
    //Constructors
    public TableServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "table", setPosition, TableState.COMPARTMENT_1, ServoBrand.AXON_MAX);
    }

    public TableServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "table", initialPosition, TableState.COMPARTMENT_1, ServoBrand.AXON_MAX);
    }

    //State enum (has 2 value cause servo flips positions for some reason)
    public enum TableState implements ServoState{
        //COMPARTMENT_1(0.2471f),
        COMPARTMENT_1(0.0941f),
        //COMPARTMENT_2(0.549f),
        COMPARTMENT_2(0.4039f),
        //COMPARTMENT_3(0.8588f);
        COMPARTMENT_3(0.698f);
        private float pos;
        TableState(float pos){
            this.pos = pos;
        }

        @Override
        public float getPosition() {
            return pos;
        }
    }
}
