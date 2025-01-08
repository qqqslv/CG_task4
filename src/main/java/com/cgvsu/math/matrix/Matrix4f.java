package com.cgvsu.math.matrix;

import com.cgvsu.math.vector.Vector4f;

import static com.cgvsu.math.Global.EPS;


public class Matrix4f {
    private float[][] mat;
    static final private int SIZE = 4;

    public Matrix4f(float[][] mat) {
        if (mat.length != SIZE || mat[0].length != SIZE) {
            throw new IllegalArgumentException("Matrix must be 4x4");
        }

        this.mat = mat;
    }

    public Matrix4f(float[] matrix) {
        this.mat = new float[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(matrix, i * 4, this.mat[i], 0, SIZE);
        }
    }

    public Matrix4f(Matrix4f matrix) {
        this.mat = new float[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(matrix.mat[i], 0, this.mat[i], 0, SIZE);
        }
    }

    public Matrix4f() {
        this.mat = new float[SIZE][SIZE];
    }

    public static Matrix4f identity() {
        Matrix4f matrix = new Matrix4f();
        for (int i = 0; i < 4; i++) {
            matrix.setAt(i, i, 1.0f);
        }

        return matrix;
    }

    public float getAt(int row, int col) {
        if (row < 0 || row >= mat.length || col < 0 || col >= mat[0].length) {
            throw new IndexOutOfBoundsException("Индекс вне границ матрицы.");
        }

        return mat[row][col];
    }

    public void setAt(int row, int col, float value) {
        if (row < 0 || row >= mat.length || col < 0 || col >= mat[0].length) {
            throw new IndexOutOfBoundsException("Индекс вне границ матрицы.");
        }
        mat[row][col] = value;
    }

    public Matrix4f add(final Matrix4f m2) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.mat[i][j] += m2.mat[i][j];
            }
        }

        return this;
    }

    public Matrix4f sub(final Matrix4f m2) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.mat[i][j] -= m2.mat[i][j];
            }
        }

        return this;
    }

    public Matrix4f mult(final Matrix4f m2) {
        float[][] result = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result[i][j] += this.mat[i][k] * m2.mat[k][j];
                }
            }
        }

        this.mat = result;
        return this;
    }

    public Vector4f mulVector4(final Vector4f v) {
        float[] arr = new float[]{v.x, v.y, v.z, v.w};
        float[] res = new float[SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                res[row] += this.mat[row][col] * arr[col];
            }
        }
        return new Vector4f(res[0], res[1], res[2], res[3]);
    }

    public void transpose() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = row + 1; col < SIZE; col++) {
                float a = this.mat[row][col];//swap
                this.mat[row][col] = this.mat[col][row];
                this.mat[col][row] = a;
            }
        }
    }

    public float determinant() {
        float det = 0;
        for (int i = 0; i < SIZE; i++) {
            float[][] minor = getMinor(0, i);
            Matrix3f minorMatrix = new Matrix3f(minor);
            det += (float) (Math.pow(-1, i) * mat[0][i] * minorMatrix.determinant());
        }
        return det;
    }

    private float[][] getMinor(int row, int col) {
        float[][] minor = new float[3][3];
        int minorRow = 0;
        for (int i = 0; i < SIZE; i++) {
            if (i == row) continue;
            int minorCol = 0;
            for (int j = 0; j < SIZE; j++) {
                if (j == col) continue;
                minor[minorRow][minorCol] = mat[i][j];
                minorCol++;
            }
            minorRow++;
        }
        return minor;
    }

    public boolean equals(final Matrix4f other) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (Math.abs(this.mat[row][col] - other.mat[row][col]) >= EPS) {
                    return false;
                }
            }
        }
        return true;
    }

    public void print() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(this.mat[i][j] + "\t");
            }
            System.out.println();
        }
    }
}