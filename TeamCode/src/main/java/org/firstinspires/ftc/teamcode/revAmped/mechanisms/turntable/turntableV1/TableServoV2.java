package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.Encoder;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.EncoderImpl;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@Config
public class TableServoV2 extends HwServo2<Types.TableState2> {
    public static float BALL1;
    public static float BALL2;
    public static float BALL3;
    public static float BALL1_END;
    public static float BALL2_END;
    public static float BALL3_END;
    private final EncoderImpl encoder;
    private boolean finishedAccelerating = false;

    // Second servo as HwServo2
    private final HwServo table2;

    public TableServoV2(HardwareMap hardwareMap, boolean setPosition, Types.TableState2 initialState, Encoder encoder) throws IllegalArgumentException {
        super(hardwareMap, "table", setPosition, initialState, ServoBrand.GOBILDA_SUPERSPEED);
        this.encoder = new EncoderImpl(encoder);

        // Initialize the second servo as HwServo2
        this.table2 = new HwServo(hardwareMap, "table_right", Types.TableState2.BALL2.getPosition());
    }

    @Override
    public boolean reached() {
        return finishedAccelerating && encoder.getVelocity() < 5;
    }

    public Types.TableState2[] getShootStates() {
        switch (currentState) {
            case BALL1 -> {
                return new Types.TableState2[] {Types.TableState2.BALL2, Types.TableState2.BALL3, Types.TableState2.BALL1_END};
            }
            case BALL2 -> {
                return new Types.TableState2[] {Types.TableState2.BALL3, Types.TableState2.BALL1_REV2, Types.TableState2.BALL2_END};
            }
            case BALL3 -> {
                return new Types.TableState2[] {Types.TableState2.BALL1_REV2, Types.TableState2.BALL2_REV2, Types.TableState2.BALL3_END};
            }
        }

        return null;
    }

    @Override
    public void update() {
        encoder.update();
        super.update();
        // Update the second servo as well
        this.table2.update();
    }

    @Override
    public void setCurrentState(Types.TableState2 state, float position) {
        if (state != currentState) {
            encoder.reset();
            finishedAccelerating = false;
            Scheduler.getInstance().add(new Delay(250, () -> finishedAccelerating = true));
        }

        super.setCurrentState(state, position);
    }

    @Override
    public void setCurrentState(Types.TableState2 state) {
        if (state != currentState) {
            encoder.reset();
            finishedAccelerating = false;
            Scheduler.getInstance().add(new Delay(250, () -> finishedAccelerating = true));
        }
        super.setCurrentState(state);
    }

    public void resetToNeutral() {
        setCurrentState(Types.TableState2.BALL2);
    }

    public void fullRotation() {
        switch (currentState) {
            case BALL1 -> setCurrentState(Types.TableState2.BALL1_END);
            case BALL2 -> setCurrentState(Types.TableState2.BALL2_END);
            case BALL3 -> setCurrentState(Types.TableState2.BALL3_END);
        }
    }

    @Override
    public void setPosition(float position) {
        super.setPosition(position);

        if (table2 != null) {
            this.table2.setPosition(position);
        }
    }
}
