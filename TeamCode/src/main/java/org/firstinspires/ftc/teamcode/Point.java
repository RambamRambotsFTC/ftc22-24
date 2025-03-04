package org.firstinspires.ftc.teamcode;

public class Point {
    public double x, y, heading;

    public Point(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

    public double distanceTo(Point other) { return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2)); }
}
