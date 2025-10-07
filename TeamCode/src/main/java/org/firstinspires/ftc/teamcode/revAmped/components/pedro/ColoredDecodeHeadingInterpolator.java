package org.firstinspires.ftc.teamcode.revAmped.components.pedro;

import com.pedropathing.paths.HeadingInterpolator;

import revAmped.components.misc.Globals;

public class ColoredDecodeHeadingInterpolator {
    private final HeadingInterpolator interpolator;
    private HeadingInterpolator redInterpolator;
    private HeadingInterpolator blueInterpolator;

    public ColoredDecodeHeadingInterpolator(HeadingInterpolator interpolator, Globals.AllianceColor allianceColor) {
        this.interpolator = interpolator;
        if (allianceColor == Globals.AllianceColor.None) throw new RuntimeException("Uncolored ColoredDecodePose");
        if (allianceColor == Globals.AllianceColor.Red) redInterpolator = interpolator;
        else if (allianceColor == Globals.AllianceColor.Blue) blueInterpolator = interpolator;
    }

    public HeadingInterpolator get() {
        if (Globals.allianceColor.equals(Globals.AllianceColor.Red)) {
            if (redInterpolator == null) redInterpolator = t -> Math.PI - interpolator.interpolate(t);
            return redInterpolator;
        }

        if (blueInterpolator == null) blueInterpolator = t -> Math.PI - interpolator.interpolate(t);
        return blueInterpolator;
    }
}
