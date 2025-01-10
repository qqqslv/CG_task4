package com.cgvsu.math;

import com.cgvsu.math.vector.*;
import com.cgvsu.math.matrix.*;
import com.cgvsu.model.Model;
import java.util.ArrayList;

public class ATTransformator {
    private Matrix4f transformationMatrix;

    private ATTransformator(Matrix4f transformationMatrix) {
        this.transformationMatrix = new Matrix4f(transformationMatrix.copy());
    }

    public Vector3f applyTransformationToVector(Vector3f vector) {
        Vector4f fourVector = new Vector4f(vector.getX(), vector.getY(), vector.getZ(), 1.0f);
        Vector4f transformedVector = transformationMatrix.mulVector4(fourVector);
        if (transformedVector.getW() == 0) {
            return new Vector3f(transformedVector.getX(), transformedVector.getY(), transformedVector.getZ());
        } else {
            return new Vector3f(transformedVector.getX()/transformedVector.getW(), transformedVector.getY()/transformedVector.getW(), transformedVector.getZ()/transformedVector.getW());
        }
    }

    public ArrayList<Vector3f> applyTransformationToVectorList(ArrayList<Vector3f> vectorList) {
        ArrayList<Vector3f> transformedVectorList = new ArrayList<>();
        for (Vector3f vector : vectorList) {
            transformedVectorList.add(applyTransformationToVector(vector));
        }
        return transformedVectorList;
    }

    public Model applyTransformationToModel(Model originalModel) {
        Model transformedModel = new Model();

        for (Vector3f vertex : originalModel.vertices) {
            transformedModel.vertices.add(applyTransformationToVector(vertex));
        }

        transformedModel.textureVertices.addAll(originalModel.textureVertices);
        transformedModel.normals.addAll(originalModel.normals);
        transformedModel.polygons.addAll(originalModel.polygons);

        return transformedModel;
    }

    public static class ATBuilder {
        private Matrix4f currentMatrix;

        public ATBuilder() {
            this.currentMatrix = Matrix4f.identity();
        }

        private ATBuilder scale(float sX, float sY, float sZ) {
            Matrix4f scaleMatrix = new Matrix4f(new float[][]{
                    {sX, 0, 0, 0},
                    {0, sY, 0, 0},
                    {0, 0, sZ, 0},
                    {0, 0, 0, 1}
            });
            this.currentMatrix = this.currentMatrix.mult(scaleMatrix);
            return this;
        }

        private ATBuilder rotateX(float rX) {
            float cosX = (float) Math.cos(rX);
            float sinX = (float) Math.sin(rX);

            Matrix4f rotationMatrix = new Matrix4f(new float[][]{
                    {1, 0, 0, 0},
                    {0, cosX, sinX, 0},
                    {0, -sinX, cosX, 0},
                    {0, 0, 0, 1}
            });
            this.currentMatrix = this.currentMatrix.mult(rotationMatrix);
            return this;
        }

        private ATBuilder rotateY(float rY) {
            float cosY = (float) Math.cos(rY);
            float sinY = (float) Math.sin(rY);

            Matrix4f rotationMatrix = new Matrix4f(new float[][]{
                    {cosY, 0, sinY, 0},
                    {0, 1, 0, 0},
                    {-sinY, 0, cosY, 0},
                    {0, 0, 0, 1}
            });
            this.currentMatrix = this.currentMatrix.mult(rotationMatrix);
            return this;
        }

        private ATBuilder rotateZ(float rZ) {
            float cosZ = (float) Math.cos(rZ);
            float sinZ = (float) Math.sin(rZ);

            Matrix4f rotationMatrix = new Matrix4f(new float[][]{
                    {cosZ, sinZ, 0, 0},
                    {-sinZ, cosZ, 0, 0},
                    {0, 0, 1, 0},
                    {0, 0, 0, 1}
            });
            this.currentMatrix = this.currentMatrix.mult(rotationMatrix);
            return this;
        }

        private ATBuilder translate(float tX, float tY, float tZ) {
            Matrix4f translationMatrix = new Matrix4f(new float[][]{
                    {1, 0, 0, tX},
                    {0, 1, 0, tY},
                    {0, 0, 1, tZ},
                    {0, 0, 0, 1}
            });

            this.currentMatrix = this.currentMatrix.mult(translationMatrix);
            return this;
        }

        public ATBuilder scaleByX(float sX) {
            return scale(sX, 1, 1);
        }

        public ATBuilder scaleByY(float sY) {
            return scale(1, sY, 1);
        }

        public ATBuilder scaleByZ(float sZ) {
            return scale(1, 1, sZ);
        }

        public ATBuilder scaleByVertor(Vector3f vector) {
            return scale(vector.getX(), vector.getY(), vector.getZ());
        }

        public ATBuilder scaleByCoordinates(float sX, float sY, float sZ) {
            return scale(sX, sY, sZ);
        }

        public ATBuilder translateByX(float tX) {
            return translate(tX, 0, 0);
        }

        public ATBuilder translateByY(float tY) {
            return translate(0, tY, 0);
        }

        public ATBuilder translateByZ(float tZ) {
            return translate(0, 0, tZ);
        }

        public ATBuilder translateByVector(Vector3f vector) {
            return translate(vector.getX(), vector.getY(), vector.getZ());
        }

        public ATBuilder translateByCoordinates(float tX, float tY, float tZ) {
            return translate(tX, tY, tZ);
        }

        public ATBuilder rotateByX(float rX) {
            return rotateX(rX);
        }

        public ATBuilder rotateByY(float rY) {
            return rotateY(rY);
        }

        public ATBuilder rotateByZ(float rZ) {
            return rotateZ(rZ);
        }

        public ATTransformator build() {
            return new ATTransformator(currentMatrix);
        }
    }


    @Override
    public String toString() {
        return transformationMatrix.toString();
    }

    public void clean() {
        this.transformationMatrix = new Matrix4f(Matrix4f.identity().copy());
    }
}