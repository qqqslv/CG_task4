package com.cgvsu.math;

import static com.cgvsu.math.Global.EPS;


public class Vector4f {
    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float x, y, z, w;

    public Vector4f add(final Vector4f v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        this.w += v.w;

        return this;
    }

    public Vector4f sub(final Vector4f v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        this.w -= v.w;

        return this;
    }

    public Vector4f mult(float c) {
        this.x *= c;
        this.y *= c;
        this.z *= c;
        this.w *= c;

        return this;
    }

    public Vector4f div(float c) {
        if (c < EPS) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }

        this.x /= c;
        this.y /= c;
        this.z /= c;
        this.w /= c;

        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public static float dot(final Vector4f v1, final Vector4f v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z + v1.w * v2.w;
    }

    public boolean equals(final Vector4f other) {
        return Math.abs(x - other.x) < EPS
                && Math.abs(y - other.y) < EPS
                && Math.abs(z - other.z) < EPS
                && Math.abs(w - other.w) < EPS;
    }
}