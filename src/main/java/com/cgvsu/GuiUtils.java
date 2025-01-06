package com.cgvsu;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GuiUtils {
    public static List<Model> models = new ArrayList<>();
    public static Model selectedModel;
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
}
