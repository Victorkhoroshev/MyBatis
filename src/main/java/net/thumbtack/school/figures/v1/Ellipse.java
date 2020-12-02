package net.thumbtack.school.figures.v1;

import java.util.Objects;

public class Ellipse {
    private Point center = new Point();
    private int xAxis;
    private int yAxis;

    public Ellipse(Point point, int xAxis, int yAxis) {
        center = point;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public Ellipse(int xCenter, int yCenter, int xAxis, int yAxis) {
        center.setX(xCenter);
        center.setY(yCenter);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public Ellipse(int xAxis,int yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        center.setX(0);
        center.setY(0);
    }

    public Ellipse() {
        xAxis = 1;
        yAxis = 1;
        center.setX(0);
        center.setY(0);
    }

    public Point getCenter() {
        return center;
    }

    public int getXAxis() {
        return xAxis;
    }

    public int getYAxis() {
        return yAxis;
    }

    public void setXAxis(int xAxis) {
        this.xAxis = xAxis;
    }

    public void setYAxis(int yAxis) {
        this.yAxis = yAxis;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public void moveTo(int x, int y) {
        center.setX(x);
        center.setY(y);
    }

    public void moveTo(Point point) {
        center.setX(point.getX());
        center.setY(point.getY());
    }

    public void moveRel(int dx, int dy) {
        center.setX(center.getX() + dx);
        center.setY(center.getY() + dy);
    }

    public void resize(double ratio) {
        xAxis *= ratio;
        yAxis *= ratio;
    }

    public void stretch(double xRatio, double yRatio) {
        xAxis *= xRatio;
        yAxis *= yRatio;
    }

    public double getArea() {
        return Math.PI * (xAxis * yAxis) / 4;
    }

    public double getPerimeter() {
        return 2 * Math.PI * Math.sqrt((Math.pow(xAxis, 2d) + Math.pow(yAxis, 2d)) / 8);
    }

    public boolean isInside(int x, int y) {
        double d = Math.pow((double) x - center.getX(), 2d) / Math.pow(xAxis / 2d, 2d) +
                Math.pow((double) y - center.getY(), 2d) / Math.pow(yAxis / 2d, 2d);
        return d <= 1;
    }

    public boolean isInside(Point point) {
        int x = point.getX();
        int y = point.getY();
        double d = Math.pow((double) x - center.getX(), 2d) / Math.pow(xAxis / 2d, 2d) +
                Math.pow((double) y - center.getY(), 2d) / Math.pow(yAxis / 2d, 2d);
        return d <= 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ellipse ellipse = (Ellipse) o;
        return xAxis == ellipse.xAxis &&
                yAxis == ellipse.yAxis &&
                center.equals(ellipse.center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(center, xAxis, yAxis);
    }
}