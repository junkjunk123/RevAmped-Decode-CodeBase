package revAmped.linear.util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.RobotLog;

import revAmped.components.GamepadDevices.Button;

/**
 * For selection using game pad before waitForStart()
 */
public class Select
{
    private OpMode op;

    public Select(OpMode op) {
        this.op = op;
    }

    public boolean selectAlliance ()
            throws InterruptedException {

        String[] alliances = new String[] {"Red", "Blue"};
        int index = select(alliances,
                "Alliance");
        return index == 0;
    }

    public int select (Object[] list,
                       String title)
            throws InterruptedException {
        int index = 0;

        op.gamepad1.reset();
        while (!Thread.currentThread().isInterrupted()) {
            long timeStamp = System.currentTimeMillis();
            if (op.gamepad1.b && Button.BTN_NEXT.canPress(timeStamp)) {
                index++;
                if (index >= list.length) {
                    index = 0;
                }
            } else if (op.gamepad1.x && Button.BTN_PREV.canPress(timeStamp)) {
                index--;
                if (index < 0) {
                    index = list.length - 1;
                }
            } else if (op.gamepad1.start && Button.BTN_START.canPress(timeStamp)) {
                break;
            }

            op.telemetry.addData(title, list[index]);
            op.telemetry.addData("Select", "Next: B  Prev: X");
            op.telemetry.addData("Confirm", "Start");
            op.telemetry.update();
        }

        RobotLog.i("Select: " + title + "=" + list[index]);
        return index;
    }
}
