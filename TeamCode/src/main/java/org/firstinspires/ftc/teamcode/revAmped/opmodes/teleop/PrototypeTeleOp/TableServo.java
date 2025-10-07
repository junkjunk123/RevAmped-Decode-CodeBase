package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;

public class TableServo extends HwServo2<TableServo.TableState> {
    public TableServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "table", setPosition, TableState.COMPARTMENT_1, ServoBrand.AXON_MAX);
    }

    public TableServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "table", initialPosition, TableState.COMPARTMENT_1, ServoBrand.AXON_MAX);
    }

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
