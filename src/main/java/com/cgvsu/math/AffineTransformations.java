package com.cgvsu.math;

import com.cgvsu.math.vector.*;
import com.cgvsu.math.matrix.*;

public class AffineTransformations {
    public static Matrix4f translation(Vector3f translationVector) {
        Matrix4f matrix = Matrix4f.identity();
        matrix.setAt(0, 3, translationVector.getX());
        matrix.setAt(1, 3, translationVector.getY());
        matrix.setAt(2, 3, translationVector.getZ());
        return matrix;
    }

    public static Matrix4f scaling(Vector3f scaleFactors) {
        Matrix4f matrix = Matrix4f.identity();
        matrix.setAt(0, 0, scaleFactors.getX());
        matrix.setAt(1, 1, scaleFactors.getY());
        matrix.setAt(2, 2, scaleFactors.getZ());
        return matrix;
    }

    public static Matrix4f rotationX(Matrix4f matrix, double angleRadians) {
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);
        matrix.setAt(1, 1, (float) cos);
        matrix.setAt(1, 2, (float) -sin);
        matrix.setAt(2, 1, (float) sin);
        matrix.setAt(2, 2, (float) cos);
        return matrix;
    }

    public static Matrix4f rotationY(Matrix4f matrix, double angleRadians) {
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);
        matrix.setAt(0, 0, (float) cos);
        matrix.setAt(0, 2, (float) sin);
        matrix.setAt(2, 0, (float) -sin);
        matrix.setAt(2, 2, (float) cos);
        return matrix;
    }

    public static Matrix4f rotationZ(Matrix4f matrix, double angleRadians) {
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);
        matrix.setAt(0, 0, (float) cos);
        matrix.setAt(0, 1, (float) -sin);
        matrix.setAt(1, 0, (float) sin);
        matrix.setAt(1, 1, (float) cos);
        return matrix;
    }
}
