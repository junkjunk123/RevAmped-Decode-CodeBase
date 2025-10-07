package org.firstinspires.ftc.teamcode.revAmped.util;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.RobotLog;

import revAmped.components.gamepad.Button;

/**
 * For selection using game pad before waitForStart()
 */
public class SelectLinear
{
    private LinearOpMode op;

    public SelectLinear(LinearOpMode op) {
        this.op = op;
    }

    /**
     * Adjust delay in milliseconds
     * @return delay in milliseconds
     * @throws InterruptedException
     */
    public int adjustDelay ()
        throws InterruptedException {
        // 30 second max
        return adjust("DelayOld", 30) * 1000;
    }

    /**
     * Adjust a number
     * @return a number
     * @throws InterruptedException
     */
    public int adjust (String title,
                       int max)
        throws InterruptedException
    {
        op.gamepad1.reset();
        int number = 0;
        while (!Thread.currentThread().isInterrupted()) {
            long timeStamp = System.currentTimeMillis();

            //choosing
            if (op.gamepad1.b && Button.BTN_PLUS.canPressShort(timeStamp)) {
                number += 1;
                if (number > max) {
                    number = max;
                }
            } else if (op.gamepad1.x && Button.BTN_MINUS.canPressShort(timeStamp)) {
                number -= 1;
                if (number < 0) {
                    number = 0;
                }
            }
            else if (op.gamepad1.start && Button.BTN_START.canPress(timeStamp)) {
                break;
            }

            op.telemetry.addData(title + " adjust", "+:B  -:X");
            op.telemetry.addData("Confirm", "Start");
            op.telemetry.addData(title, number);
            op.telemetry.update();

            op.idle();
        }

        RobotLog.i("Adjust: " + title + "=" + number);
        op.idle();
        return number;
    }

    /**
     * Select alliance
     * @return true for red, false for blue
     * @throws InterruptedException
     */
    public boolean selectAlliance ()
        throws InterruptedException {

        String[] alliances = new String[] {"Red", "Blue"};
        int index = select(alliances,
                           "Alliance");
        return index == 0;
    }

    public boolean move()
        throws InterruptedException {
        String[] options = {"Move", "Manual"};
        int index = select(options, "Move");

        return index == 0;
    }

    /*=========================================Skystone========================================================*/

    /**
     * Select position
     * @return true for foundation, false for depot
     * @throws InterruptedException
     */
    public boolean selectStartingPosition ()
            throws InterruptedException {

        String[] positions = new String[] {"Middle", "Wall"};
        int index = select(positions,
                "Position");
        return index == 0;
    }

    /**
     * Select parking position
     * @return true for park near to wall, false for far
     * @throws InterruptedException
     */
    public String selectParking()
            throws InterruptedException {

        String[] park = new String[] {"Middle", "Far", "Near"};
        int index = select(park,
                "Parking");
        return park[index];
    }

    /**
     * Into the Deep
     * @return Basket Scoring Position
     * @throws InterruptedException
     */
    public String selectScoring() throws InterruptedException {
        String[] score = new String[] {"Parallel", "Perpendicular", "Diagonal"};
        int index = select(score,
                "Basket Depositing Position");
        return score[index];
    }

    public boolean selectSkipCycle()
            throws InterruptedException {
        String[] detect = new String[] {"Yes", "No"};
        int index = select(detect, "Skip cycle? ");
        return index == 0;
    }

    /**
     * Select if the robot should detect skystones
     * @returns true for detect, false for not
     * @throws InterruptedException
     */
    public boolean selectDetection()
        throws InterruptedException {
        String[] detect = new String[] {"Yes", "No"};
        int index = select(detect, "Skystone Detection");
        return index == 0;
    }

    /**
     * If just Park = Yes then not to unload purple pixle
     * @returns true for right, false for left
     * @throws InterruptedException
     */
    public boolean selectPath()
        throws InterruptedException {
        String[] path = new String[] {"Yes", "NO"};
        int index = select(path, "Go to Park?");
        return index == 0;
    }

    /**
     * Select how long time to wait to start program
     * @returns double in seconds
     * @throws InterruptedException
     */
    public double waitToStartTimerFunction()
        throws InterruptedException {
        Double[] waitToStart = new Double[] {0.0,1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0};
        int index = select(waitToStart, "Wait to Start Seconds");
        return waitToStart[index];
    }

    /**
     * Select how long time to wait to start program
     * @returns double in seconds
     * @throws InterruptedException
     */
    public double waitToBackDropTimerFunction()
            throws InterruptedException {
        Double[] waitToStart = new Double[] {0.0,1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0};
        int index = select(waitToStart, "Wait to Backdrop Seconds");
        return waitToStart[index];
    }

    /**
     * Select how long time to wait to start program
     * @returns double in seconds
     * @throws InterruptedException
     */
    public double waitToParkTimerFunction()
            throws InterruptedException {
        Double[] waitToStart = new Double[] {0.0, 1.0, 2.0, 3.0,4.0,5.0,
                6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,
                15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0};
        int index = select(waitToStart, "Wait To BackDrop at ");
        return waitToStart[index];
    }

    public double waitToDropTimerFunction()
            throws InterruptedException {
        Double[] waitToStart = new Double[] {0.0, 1.0,2.0,3.0, 4.0,5.0,
                6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,
                15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0,
                23.0,24.0,25.0,26.0,27.0};
        int index = select(waitToStart, "Wait To Drop at ");
        return waitToStart[index];
    }


    /**
     * Select whether to move foundation to building site
     * @returns true for ma3, false for e4t
     * @throws InterruptedException
     */
    public boolean ma3Test()
            throws InterruptedException {
        String[] ma3 = new String[] {"Yes", "No"};
        int index = select(ma3, "Use MA3");
        return index == 0;
    }


    /*=========================================Rover Ruckus========================================================*/
    /**
     * Select Hang
     * @throws InterruptedException
     */
    public boolean selectHang ()
            throws InterruptedException {

        String[] hang = new String[] {"Yes", "No"};
        int index = select(hang,
                "Hanging");
        return index == 0;
    }

    /**
     * Select TM
     * @throws InterruptedException
     */
    public boolean selectTM ()
            throws InterruptedException {

        String[] intake = new String[] {"Yes", "No"};
        int index = select(intake,
                "Score Team Mineral");
        return index == 0;
    }

    /**
     * Select SampleDetection
     * @throws InterruptedException
     */
    public boolean selectSample ()
            throws InterruptedException {

        String[] sample = new String[] {"Yes", "No"};
        int index = select(sample,
                "Sampling");
        return index == 0;
    }

    /**
     * Select Double SampleDetection
     * @throws InterruptedException
     */
    public boolean selectDoubleSample ()
            throws InterruptedException {

        String[] doublesample = new String[] {"Yes", "No"};
        int index = select(doublesample,
                "Double Sampling");
        return index == 0;
    }


    /**
     * Select Scoring
     * @throws InterruptedException
     */
    public boolean selectScore ()
        throws InterruptedException{

        String[] score = new String [] {"Yes", "No"};
        int index = select(score,
                "Scoring");
        return index == 0;
    }
    /**
     * Select position
     * @return true for Crater, false for Depot
     * @throws InterruptedException
     */
    public boolean selectPosition ()
            throws InterruptedException {

        String[] positions = new String[] {"Crater", "Depot"};
        int index = select(positions,
                "Position");
        return index == 0;
    }

    /**
     * Select extra glyphs in auto
     * @throws InterruptedException
     */
    public boolean extraGlyph ()
            throws InterruptedException {

        String[] glyph = new String[] {"Yes", "No"};
        int index = select(glyph,
                "four glyph");
        return index == 0;
    }
    /**
     * Select extra glyphs in auto
     * @throws InterruptedException
     */
    public boolean extraGlyph2 ()
            throws InterruptedException {

        String[] glyph = new String[] {"Yes", "No"};
        int index = select(glyph,
                "six glyph");
        return index == 0;
    }
    /**
     * Select extra glyphs in auto
     * @throws InterruptedException
     */
    public boolean jewel ()
            throws InterruptedException {

        String[] glyph = new String[] {"Yes", "No"};
        int index = select(glyph,
                "opencv");
        return index == 0;
    }
    /**
     * Select backoff distance
     * @return true for near, false for far
     * @throws InterruptedException
     */
    public boolean selectBackoff ()
            throws InterruptedException {

        String[] backoffNear = new String[] {"Near", "Far"};
        int index = select(backoffNear,
                "Backoff Distance");
        return index == 0;
    }

    /**
     * Select from the given array
     * @return index from the given array starting 0
     * @throws InterruptedException
     */
    public int select (Object[] list,
                       String title)
        throws InterruptedException {
        int index = 0;

        op.gamepad1.reset();
        while (!Thread.currentThread().isInterrupted()) {
            long timeStamp = System.currentTimeMillis();
            if(op.gamepad1.b && Button.BTN_NEXT.canPress(timeStamp)) {
                index++;
                if(index >= list.length){
                    index = 0;
                }
            } else if(op.gamepad1.x && Button.BTN_PREV.canPress(timeStamp)) {
                index--;
                if(index < 0){
                    index = list.length - 1;
                }
            }
            else if (op.gamepad1.start && Button.BTN_START.canPress(timeStamp)) {
                break;
            }

            op.telemetry.addData(title, list[index]);
            op.telemetry.addData("Select", "Next: B  Prev: X");
            op.telemetry.addData("Confirm", "Start");
            op.telemetry.update();

            op.idle();
        }

        RobotLog.i("Select: " + title + "=" + list[index]);
        op.idle();
        return index;
    }

    /**
     * Confirm selection
     * @param title item to be confirmed
     * @param def default value
     * @return true to confirm
     */
    public boolean confirm (String title,
                            boolean def)
        throws InterruptedException {

        String[] options;
        if (def) {
            options = new String[]{"Yes", "No"};
        } else {
            options = new String[]{"No", "Yes"};
        }

        int index = select(options,
                           title);
        return index == (def ? 0 : 1);
    }

    /**
     * Adjust a number
     * @return a number
     * @throws InterruptedException
     */
    public float adjustFloat (String title,
                       float max,
                       float interval)
            throws InterruptedException
    {
        op.gamepad1.reset();
        float number = 0;
        while (!Thread.currentThread().isInterrupted()) {
            long timeStamp = System.currentTimeMillis();

            //choosing
            if (op.gamepad1.b && Button.BTN_PLUS.canPressShort(timeStamp)) {
                number += interval;
                if (number > max) {
                    number = max;
                }
            } else if (op.gamepad1.x && Button.BTN_MINUS.canPressShort(timeStamp)) {
                number -= interval;
                if (number < 0) {
                    number = 0;
                }
            }
            else if (op.gamepad1.start && Button.BTN_START.canPress(timeStamp)) {
                break;
            }

            op.telemetry.addData(title + " adjust", "+:B  -:X");
            op.telemetry.addData("Confirm", "Start");
            op.telemetry.addData(title, number);
            op.telemetry.update();

            op.idle();
        }

        RobotLog.i("Adjust: " + title + "=" + number);
        op.idle();
        return number;
    }
}
