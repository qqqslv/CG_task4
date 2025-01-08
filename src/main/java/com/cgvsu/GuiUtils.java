package com.cgvsu;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

import com.cgvsu.math.vector.Vector3f;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GuiUtils {
    public static List<Model> models = new ArrayList<>();
    public static List<Camera> cameras = new ArrayList<>();
    public static Model selectedModel;
    public static Camera selectedCamera;
    public static Model loadModelFromFile(File file) {
        Path fileName = Path.of(file.getAbsolutePath());
        try {
            String fileContent = Files.readString(fileName);
            return ObjReader.read(fileContent);
        } catch (IOException exception) {
            System.err.println("Не удалось загрузить модель");
            return null;
        }
    }
    public static Model addModel(File file) {
        if (file == null) {
            return null;
        }

        Model model = loadModelFromFile(file);
        if (model == null) {
            System.err.println("Не удалось загрузить модель из файла");
            return null;
        }
        model.setName(file.getName());
        models.add(model);
        return model;
    }
    public static void deleteModel(Model model) {
        if (model != null && models != null) {
            models.remove(model);
            if (selectedModel == model) {
                selectedModel = null;
            }
        }
    }
    public static void setCamera(Camera camera, float x, float y, float z, float targetX, float targetY, float targetZ, float fov) {
        camera.setPosition(new Vector3f(x, y, z));
        camera.setTarget(new Vector3f(targetX, targetY, targetZ));
        camera.setFov(fov);
    }


    public static Camera createCamera(float x, float y, float z) {
        return new Camera(new Vector3f(x, y, z), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100);
    }
    public static void removeCamera(Camera camera) {
        if (camera == null) {
            return;
        }
        cameras.remove(camera);
    }
}
