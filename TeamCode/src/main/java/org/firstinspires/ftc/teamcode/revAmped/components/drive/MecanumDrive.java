package org.firstinspires.ftc.teamcode.revAmped.components.drive;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwMotor;

/**
 * Created by swang on 11/30/2017.
 */

public class MecanumDrive{
    public final HwMotor driveLeftFront;
    public final HwMotor driveLeftBack;
    public final HwMotor driveRightFront;
    public final HwMotor driveRightBack;
    public final HwMotor[] motorsArray;

    public MecanumDrive(HwMotor driveLeftFront,
                        HwMotor driveLeftBack,
                        HwMotor driveRightFront,
                        HwMotor driveRightBack)
    {
        this.driveLeftFront = driveLeftFront;
        this.driveLeftBack = driveLeftBack;
        this.driveRightFront = driveRightFront;
        this.driveRightBack = driveRightBack;
        this.motorsArray = new HwMotor[] {driveLeftFront, driveLeftBack, driveRightFront, driveRightBack};
    }

    /**
     * stop the drive motors
     */
    public void stop() {
        setPower(0f);
    }

    /**
     * set drive motor powers for floats
     */
    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        driveLeftFront.setZeroPowerBehavior(behavior);
        driveLeftBack.setZeroPowerBehavior(behavior);
        driveRightFront.setZeroPowerBehavior(behavior);
        driveRightBack.setZeroPowerBehavior(behavior);
    }

    /**
     * turn the robot at a specified power for each wheel of Mecanum wheels
     * @param pwr_lf motor power left
     * @param pwr_lb motor power right
     * @param pwr_rf motor power left
     * @param pwr_rb motor power right
     *
     *      FORWARD(+x)   SIDEWAYS RIGHT(+y)   TURN RIGHT(+r)
    front left      +                 +                  +
    front right     +                 -                  -
    back left       +                 -                  +
    back right      +                 +                  -
     */
    public void setPower(float pwr_lf,
                         float pwr_lb,
                         float pwr_rf,
                         float pwr_rb
    )
    {

        driveLeftFront.setPower(pwr_lf);
        driveLeftBack.setPower(pwr_lb);
        driveRightFront.setPower(pwr_rf);
        driveRightBack.setPower(pwr_rb);

    }

    /**
     * First draft function of omnidirectional control of mecanum drive teleop
     * @param pwrl desired left motor power
     * @param pwrr desired right motor power
     * @param hAvg desired angle of movement relative to straight forward
     * //@param hDiff true/false angles of joysticks same or not
     * @param hLeft angle of left joystick
     * @param hRight angle of right joystick
     * @param fieldCentric true/false field centric driving mode
     * @param heading if (fieldCentric) get heading of robot
     */
    public void omnidirectionalPower(double pwrl, double pwrr, double hAvg,
        double hLeft, double hRight, boolean fieldCentric, double heading)
    {
        if (!fieldCentric) heading = 0;

        //to make max speed 1
        float MULT = (float) Math.sqrt(2);

        double pwr_lf;
        double pwr_lb;
        double pwr_rf;
        double pwr_rb;
        //negatives changed 8/26 for left power
        //if (hDiff) {
            pwr_lf = pwrl * Math.sin(hLeft + Math.PI / 4 + heading);
            pwr_lb = pwrl * Math.cos(hLeft + Math.PI / 4 + heading);
            pwr_rf = -pwrr * Math.cos(hRight + Math.PI / 4 + heading);
            pwr_rb = -pwrr * Math.sin(hRight + Math.PI / 4 + heading);
        /*} else {
            pwr_lf = pwrl * Math.sin(hAvg + Math.PI / 4 + heading);
            pwr_lb = pwrl * Math.cos(hAvg + Math.PI / 4 + heading);
            pwr_rf = -pwrr * Math.cos(hAvg + Math.PI / 4 + heading);
            pwr_rb = -pwrr * Math.sin(hAvg + Math.PI / 4 + heading);
        }*/

        driveLeftFront.setPower((float) pwr_lf*MULT);
        driveLeftBack.setPower((float) pwr_lb*MULT);
        driveRightFront.setPower((float) pwr_rf*MULT);
        driveRightBack.setPower((float) pwr_rb*MULT);
    }


    public void setStrafePower(float pwrl,
                               float pwrr)
    {
        driveLeftFront.setPower(pwrl);
        driveLeftBack.setPower(-pwrl);
        driveRightFront.setPower(-pwrr);
        driveRightBack.setPower(pwrr);

    }

    public void setTurnPower(float pwr)
    {
        driveLeftFront.setPower(-pwr);
        driveLeftBack.setPower(-pwr);
        driveRightFront.setPower(pwr);
        driveRightBack.setPower(pwr);
    }

   /* public void turnWhileStrafe (float p, float at, float a) {
        driveLeftFront.setPower(p*(Math.cos(at + .7854f)));
        driveLeftBack.setPower(-p);
        driveRightFront.setPower(-p);
        driveRightBack.setPower(p);
    }*/
    //strafe right when positive, left when negative
    public void setStrafePower(float p)
    {
        driveLeftFront.setPower(p);
        driveLeftBack.setPower(-p);
        driveRightFront.setPower(-p);
        driveRightBack.setPower(p);
    }

    /**
     * init the drive motors forward or backward, depending on the sing of p
     * @param p motor power
     */
    public void setPower(float p) {
        driveLeftFront.setPower(p);
        driveLeftBack.setPower(p);
        driveRightFront.setPower(p);
        driveRightBack.setPower(p);
    }


    /**
     * get the 2nd largest encoder value of drives (2nd largest will most likely be accurate)
     * use bubble sort
     * @return the second largest encoder value of drives
     */
    public int getEncoder() {

        final int NUM_WHEELS = 4;
        // 2nd biggest
        final int RETURN_INDEX = 1;

        int enc[] = new int[NUM_WHEELS];
        enc[0] = Math.abs(driveLeftFront.getCurrentPosition());
        enc[1] = Math.abs(driveLeftBack.getCurrentPosition());
        enc[2] = Math.abs(driveRightFront.getCurrentPosition());
        enc[3] = Math.abs(driveRightBack.getCurrentPosition());

        // bubble sort
        for (int i = 0; i <= RETURN_INDEX; i++)
        {
            for (int j = i + 1; j < NUM_WHEELS; j++)
            {
                if (Math.abs(enc[i]) < Math.abs(enc[j]))
                {
                    // swap
                    int c = enc[i];
                    enc[i] = enc[j];
                    enc[j] = c;
                }
            }
        }

        return enc[RETURN_INDEX];
    }



    //=========================below are  API from Drive, not much useful for Mecanum drive.==================
    /**
     * turn the robot at a specified power, ,
     * if pwrl  = pwrr:  go straight
     *    pwrl > 0 && pwrl > pwrr :  turn right
     *    pwrl > 0 && pwrl < pwrr :  turn left
     *    pwrl < 0 && pwrl > pwrr :  turn right
     *    pwrl < 0 && pwrl < pwrr :  turn left
     * @param pwrl motor power left
     * @param pwrr motor power right
     */
    public void setPower(float pwrl,
                         float pwrr) {
        driveLeftFront.setPower(pwrl);
        driveLeftBack.setPower(pwrl);
        driveRightFront.setPower(pwrr);
        driveRightBack.setPower(pwrr);
    }
}
