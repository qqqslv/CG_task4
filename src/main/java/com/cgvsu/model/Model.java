package com.cgvsu.model;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

import java.util.*;

public class Model {

    public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
    public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();
    public String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void computeNormals() { //https://github.com/SysoevaSvetlana/CGtask3/blob/main/ObjReaderInitial/src/com/cgvsu/model/Model.java

        Map<Integer, Vector3f> vertexNormals = new HashMap<>();
        Map<Integer, Integer> vertexNormalsCount = new HashMap<>();


        for (Polygon polygon : polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            if (vertexIndices.size() < 3) {
                continue;
            }

            Vector3f v0 = vertices.get(vertexIndices.get(0));
            Vector3f v1 = vertices.get(vertexIndices.get(1));
            Vector3f v2 = vertices.get(vertexIndices.get(2));

            Vector3f edge1 = v1.subtract(v0);
            Vector3f edge2 = v2.subtract(v0);
            Vector3f faceNormal = edge1.cross(edge2).normalize();

            for (int index : vertexIndices) {
                vertexNormals.compute(index, (k, v) -> {
                    if (v == null) {
                        return faceNormal.copy();
                    } else {
                        return v.add(faceNormal);
                    }
                });
            }

            for (int index : vertexIndices) {

                if (vertexNormalsCount.containsKey(index)) {
                    vertexNormalsCount.put(index, vertexNormalsCount.get(index) + 1);
                } else {
                    vertexNormalsCount.put(index,1);
                }

            }
        }


        for (Integer index : vertexNormals.keySet()) {
            vertexNormals.put(index, vertexNormals.get(index).divide(vertexNormalsCount.get(index)));
        }


        normals = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            normals.add(vertexNormals.getOrDefault(i, new Vector3f(0, 0, 0)));
        }
    }
}
