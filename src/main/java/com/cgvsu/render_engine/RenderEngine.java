package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cgvsu.math.matrix.Matrix4f;
import com.cgvsu.math.vector.Vector3f;
import com.cgvsu.model.Polygon;
import javafx.scene.canvas.GraphicsContext;
import com.cgvsu.math.*;
import com.cgvsu.model.Model;
import javafx.scene.paint.Color;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {
    public static ArrayList<Integer> selectedVertexIndices = new ArrayList<>();
    private static final double POINT_SIZE = 6.0;

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model model,
            final int width,
            final int height)
    {
        Matrix4f modelMatrix = rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = modelMatrix.mult(viewMatrix).mult(projectionMatrix);

        final int nPolygons = model.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = model.polygons.get(polygonInd).getVertexIndices().size();

            ArrayList<Point2f> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3f vertex = model.vertices.get(model.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

                Point2f resultPoint = vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertex), width, height);
                resultPoints.add(resultPoint);
            }

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine(
                        resultPoints.get(vertexInPolygonInd - 1).x,
                        resultPoints.get(vertexInPolygonInd - 1).y,
                        resultPoints.get(vertexInPolygonInd).x,
                        resultPoints.get(vertexInPolygonInd).y);
            }

            if (nVerticesInPolygon > 0)
                graphicsContext.strokeLine(
                        resultPoints.get(nVerticesInPolygon - 1).x,
                        resultPoints.get(nVerticesInPolygon - 1).y,
                        resultPoints.get(0).x,
                        resultPoints.get(0).y);

            if (model.isVerticesVisible()) {
                renderVertices(graphicsContext, model, modelViewProjectionMatrix, width, height);
            }
        }
    }

    private static void renderVertices(
            final GraphicsContext graphicsContext,
            final Model model,
            final Matrix4f modelViewProjectionMatrix,
            final int width,
            final int height) {

        final int nVertices = model.vertices.size();

        for (int i = 0; i < nVertices; i++) {
            Vector3f vertex = model.vertices.get(i);

            Point2f screenPoint = vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertex), width, height);

            if (selectedVertexIndices.contains(i)) {
                graphicsContext.setFill(Color.RED);
            } else {
                graphicsContext.setFill(Color.BLUE);
            }

            graphicsContext.fillOval(screenPoint.x - POINT_SIZE / 2, screenPoint.y - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);
        }
    }
    public static void resetSelectedVertices() {
        selectedVertexIndices.clear();
    }

    public static void deleteSelectedVertices(Model model) {
        if (!selectedVertexIndices.isEmpty()) {
            for (int i = selectedVertexIndices.size() - 1; i >= 0; i--) {
                int index = selectedVertexIndices.get(i);
                model.vertices.remove(index);

                for (Polygon polygon : model.polygons) {
                    polygon.getVertexIndices().removeIf(vertexIndex -> vertexIndex == index);
                    for (int j = 0; j < polygon.getVertexIndices().size(); j++) {
                        if (polygon.getVertexIndices().get(j) > index) {
                            polygon.getVertexIndices().set(j, polygon.getVertexIndices().get(j) - 1);
                        }
                    }
                }
            }
            selectedVertexIndices.clear();
        }
    }
    public static void toggleVerticesVisibility(Model selectedModel) {
        if (selectedModel != null) {
            selectedModel.setVerticesVisible(selectedModel.isVerticesVisible() ? false : true);
        }
    }

    public static void toggleVertexSelection(int index) {
        if (selectedVertexIndices.contains(index)) {
            selectedVertexIndices.remove(Integer.valueOf(index));
        } else {
            selectedVertexIndices.add(index);
        }
    }
}