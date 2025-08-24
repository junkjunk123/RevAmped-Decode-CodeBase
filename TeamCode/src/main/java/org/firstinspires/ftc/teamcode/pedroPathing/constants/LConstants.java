package org.firstinspires.ftc.teamcode.pedroPathing.constants;

import com.pedropathing.localization.GoBildaPinpointDriver;
import com.pedropathing.localization.constants.PinpointConstants;

public class LConstants {
    static {

        PinpointConstants.forwardY = 0.75; //forward pod
        PinpointConstants.strafeX = -6.6; //strafe pod
        PinpointConstants.forwardEncoderDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;
        PinpointConstants.strafeEncoderDirection = GoBildaPinpointDriver.EncoderDirection.REVERSED;
        //TwoWheelConstants.forwardEncoderDirection = 1;
        //TwoWheelConstants.strafeEncoderDirection = 1;
        //TwoWheelConstants.forwardY = 0.75;
        //TwoWheelConstants.strafeX = -6.6;
    }
}




