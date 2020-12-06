package net.thumbtack.school.figures.v1;

import java.util.Objects;

public class Circle {
    private Point center;
    private int radius;

    public Circle(Point center, int radius) {
        this.center = new Point(center.getX(), center.getY());
        this.radius = radius;
    }

    public Circle(int xCenter, int yCenter, int radius) {
        this(new Point(xCenter, yCenter), radius);
    }

    public Circle(int radius) {
        this(0, 0, radius);
    }

    public Circle() {
        this(0, 0, 1);
    }

    public Point getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void moveTo(int x, int y) {
        center.moveTo(x, y);
    }

    public void moveTo(Point point) {
        moveTo(point.getX(), point.getY());
    }

    public void moveRel(int dx, int dy) {
        center.moveRel(dx, dy);
    }

    public void resize(double ratio) {
        this.radius *= ratio;
    }

    public double getArea() {
        return Math.PI * Math.pow(radius, 2d);
    }

    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }

    public boolean isInside(int x, int y) {
        double d = Math.sqrt(Math.pow((double) center.getX() - x, 2d) + Math.pow((double) center.getY() - y, 2d));
        return d <= radius;
    }

    public boolean isInside(Point point) {
        int x = point.getX();
        int y = point.getY();
        double d = Math.sqrt(Math.pow((double) center.getX() - x, 2d) + Math.pow((double) center.getY() - y, 2d));
        return d <= radius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Circle circle = (Circle) o;
        return radius == circle.radius &&
                center.equals(circle.center);
    }

    public int hashCode() {
        return Objects.hash(center, radius);
    }
}
