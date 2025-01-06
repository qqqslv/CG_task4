package com.cgvsu.math;

import static com.cgvsu.math.Global.EPS;

public class Vector2f {
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float x, y;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Vector2f add(final Vector2f v) {
        this.x += v.x;
        this.y += v.y;

        return this;
    }

    public Vector2f sub(final Vector2f v) {
        this.x -= v.x;
        this.y -= v.y;

        return this;
    }

    public Vector2f mult(float c) {
        this.x *= c;
        this.y *= c;

        return this;
    }

    public Vector2f div(float c) {
        if (c < EPS) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }

        this.x /= c;
        this.y /= c;

        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public boolean equals(final Vector2f other) {
        return Math.abs(x - other.x) < EPS
                && Math.abs(y - other.y) < EPS;
    }
}