package com.cgvsu.math;


public class Point2f {
    public float x;
    public float y;

    public Point2f() {
        this.x = 0;
        this.y = 0;
    }

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point2f(Point2f point) {
        this.x = point.x;
        this.y = point.y;
    }

    public Point2f add(Point2f point) {
        return new Point2f(this.x + point.x, this.y + point.y);
    }

    public Point2f sub(Point2f point) {
        return new Point2f(this.x - point.x, this.y - point.y);
    }

    public Point2f scale(float scalar) {
        return new Point2f(this.x * scalar, this.y * scalar);
    }

    public float distance(Point2f point) {
        float dx = this.x - point.x;
        float dy = this.y - point.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public boolean equals(Point2f point, float eps) {
        return Math.abs(this.x - point.x) < eps && Math.abs(this.y - point.y) < eps;
    }
}
