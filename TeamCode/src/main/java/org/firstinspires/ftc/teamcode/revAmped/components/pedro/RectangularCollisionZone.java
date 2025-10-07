package org.firstinspires.ftc.teamcode.revAmped.components.pedro;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;

public interface RectangularCollisionZone extends CollisionZone {
    @Override
    default boolean in(Pose pose) {
        Pose center = center();
        double minXDist = minXDist();
        double minYDist = minYDist();
        return Math.abs(center.getX() - pose.getX()) < minXDist || Math.abs(center.getY() - pose.getY()) < minYDist;
    }

    @Override
    default boolean intersection(Pose current, Pose target) {
        if (in(current)) return true;
        if (in(target)) return true;

        Pose center = center();
        double minXDist = minXDist();
        double minYDist = minYDist();

        BezierLine line = new BezierLine(current, target);
        BezierLine top = new BezierLine(
                new Pose(center.getX() - minXDist, center.getY() + minYDist),
                new Pose(center.getX() + minXDist, center.getY() + minYDist)
        );
        BezierLine bottom = new BezierLine(
                new Pose(center.getX() - minXDist, center.getY() - minYDist),
                new Pose(center.getX() + minXDist, center.getY() - minYDist)
        );
        BezierLine left = new BezierLine(
                new Pose(center.getX() - minXDist, center.getY() + minYDist),
                new Pose(center.getX() - minXDist, center.getY() - minYDist)
        );
        BezierLine right = new BezierLine(
                new Pose(center.getX() + minXDist, center.getY() + minYDist),
                new Pose(center.getX() + minXDist, center.getY() - minYDist)
        );

        return linesIntersect(line, top) || linesIntersect(line, bottom) ||
               linesIntersect(line, left) || linesIntersect(line, right);
    }

    static boolean linesIntersect(BezierLine line1, BezierLine line2) {
        double x1 = line1.getFirstControlPoint().getX();
        double y1 = line1.getFirstControlPoint().getY();
        double x2 = line1.getLastControlPoint().getX();
        double y2 = line1.getLastControlPoint().getY();
        double x3 = line2.getFirstControlPoint().getX();
        double y3 = line2.getFirstControlPoint().getY();
        double x4 = line2.getLastControlPoint().getX();
        double y4 = line2.getLastControlPoint().getY();

        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (Math.abs(denom) < 1e-6) return false; // Lines are parallel

        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;

        return ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1;
    }
}
