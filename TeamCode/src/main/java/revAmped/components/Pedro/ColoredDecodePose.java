package revAmped.components.Pedro;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.FuturePose;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.MathFunctions;
import com.pedropathing.paths.HeadingInterpolator;

import java.util.Arrays;

import revAmped.components.MathUtil.MathUtil;
import revAmped.components.MathUtil.Vector2D;
import revAmped.components.Misc.Globals;

public class ColoredDecodePose implements FuturePose {
    private final Pose pose;
    private final Globals.AllianceColor color;
    private Pose blue;
    private Pose red;

    public ColoredDecodePose(double posX, double posY, double heading, Globals.AllianceColor allianceColor) {
        this(new Pose(posX, posY, heading), allianceColor);
    }

    public ColoredDecodePose(Pose pose, Globals.AllianceColor color) {
        if (color == Globals.AllianceColor.None) throw new RuntimeException("Uncolored ColoredDecodePose");
        this.pose = pose;
        this.color = color;
        if (color == Globals.AllianceColor.Red) red = pose;
        else if (color == Globals.AllianceColor.Blue) blue = pose;
    }

    public ColoredDecodePose(double x, double y, double heading) {
        this(new Pose(x, y, heading), Globals.AllianceColor.Blue);
    }

    public ColoredDecodePose(double x, double y) {
        this(new Pose(x, y), Globals.AllianceColor.Blue);
    }

    public Pose getPose(Globals.AllianceColor desiredColor) {
        if (desiredColor.equals(Globals.AllianceColor.Red)) {
            if (red == null) red = mirror(pose);
            return red;
        }

        if (blue == null) blue = mirror(pose);
        return blue;
    }

    public static Pose mirror(Pose pose) {
        return new Pose(144 - pose.getX(), pose.getY(), invertHeading(pose.getHeading()));
    }

    public static double invertHeading(double theta) {
        return MathUtil.normalizeAngleHalfPi(Math.PI - theta);
    }

    @Override
    public Pose getPose() {
        return getPose(Globals.allianceColor);
    }

    public ColoredDecodePose down(double inches) {
        return new ColoredDecodePose(this.pose.plus(new Pose(0, -inches)), color);
    }

    public ColoredDecodePose up(double inches) {
        return new ColoredDecodePose(this.pose.plus(new Pose(0, inches)), color);
    }

    public ColoredDecodePose towardsRedWall(double inches) {
        return new ColoredDecodePose(this.pose.plus(new Pose(color == Globals.AllianceColor.Blue ? inches : -inches, 0)), color);
    }

    public ColoredDecodePose towardsBlueWall(double inches) {
        return new ColoredDecodePose(this.pose.plus(new Pose(color == Globals.AllianceColor.Red ? inches : -inches, 0)), color);
    }

    public static BezierCurve makeBezier(ColoredDecodePose... poses) {
        return new BezierCurve(Arrays.stream(poses).map(ColoredDecodePose::getPose).toArray(Pose[]::new));
    }

    public static BezierLine makeBezier(ColoredDecodePose pose1, ColoredDecodePose pose2) {
        return new BezierLine(pose1.getPose(), pose2.getPose());
    }

    public static BezierPoint makeBezier(ColoredDecodePose pose) {
        return new BezierPoint(pose.getPose());
    }

    public static HeadingInterpolator mirror(HeadingInterpolator interpolation) {
        return t -> MathFunctions.normalizeAngle(Math.PI - interpolation.interpolate(t));
    }

    public Globals.AllianceColor getColor() {
        return color;
    }

    public Pose getUnmodifiedPose() {
        return pose;
    }

    public double heading() {
        return getPose().getHeading();
    }

    public ColoredDecodePose offsetOppositeColor(Pose offset) {
        if (blue == null || red == null) getPose();

        if (color == Globals.AllianceColor.Red) blue = blue.plus(offset);
        if (color == Globals.AllianceColor.Blue) red = red.plus(offset);
        return this;
    }
    
    @Override
    public boolean initialized() {
        return true;
    }
}
