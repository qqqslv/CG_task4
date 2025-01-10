package com.cgvsu.render_engine;

import com.cgvsu.math.*;
import com.cgvsu.math.matrix.Matrix4f;
import com.cgvsu.math.vector.Vector3f;

public class GraphicConveyor {
    public static Matrix4f rotateScaleTranslate() {
        return Matrix4f.identity();
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultX = new Vector3f();
        Vector3f resultY = new Vector3f();
        Vector3f resultZ = new Vector3f();

        resultZ.sub(target, eye);
        resultX.cross(up, resultZ);
        resultY.cross(resultZ, resultX);

        resultX.normalize();
        resultY.normalize();
        resultZ.normalize();

        float[] matrix = new float[]{
                resultX.x, resultY.x, resultZ.x, 0,
                resultX.y, resultY.y, resultZ.y, 0,
                resultX.z, resultY.z, resultZ.z, 0,
                -resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1};
        return new Matrix4f(matrix);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4f result = new Matrix4f();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.setAt(0, 0, tangentMinusOnDegree / aspectRatio);
        result.setAt(1, 1, tangentMinusOnDegree);
        result.setAt(2, 2, (farPlane + nearPlane) / (farPlane - nearPlane));
        result.setAt(2, 3, 1.0F);
        result.setAt(3, 2, 2 * (nearPlane * farPlane) / (nearPlane - farPlane));
        return result;
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        final float x = (vertex.getX() * matrix.getAt(0, 0)) + (vertex.getY() * matrix.getAt(1, 0)) + (vertex.getZ() * matrix.getAt(2, 0)) + matrix.getAt(3, 0);
        final float y = (vertex.getX() * matrix.getAt(0, 1)) + (vertex.getY() * matrix.getAt(1, 1)) + (vertex.getZ() * matrix.getAt(2, 1)) + matrix.getAt(3, 1);
        final float z = (vertex.getX() * matrix.getAt(0, 2)) + (vertex.getY() * matrix.getAt(1, 2)) + (vertex.getZ() * matrix.getAt(2, 2)) + matrix.getAt(3, 2);
        final float w = (vertex.getX() * matrix.getAt(0, 3)) + (vertex.getY() * matrix.getAt(1, 3)) + (vertex.getZ() * matrix.getAt(2, 3)) + matrix.getAt(3, 3);
        return new Vector3f(x / w, y / w, z / w);
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.x * width + width / 2.0F, -vertex.y * height + height / 2.0F);
    }
}
