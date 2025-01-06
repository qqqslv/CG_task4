package com.cgvsu;

import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.RenderEngine;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.IOException;
import java.io.File;
import javax.vecmath.Vector3f;

import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;

import static com.cgvsu.GuiUtils.models;
import static com.cgvsu.GuiUtils.selectedModel;

public class GuiController {

    final private float TRANSLATION = 0.5F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;
    @FXML
    private ListView<Model> modelListView;
    @FXML
    private TitledPane selectionModelsPane;

    private Camera camera = new Camera(
            new Vector3f(0, 0, 200),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 200);

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            for (Model model : models) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, model, (int) width, (int) height);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        modelListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Model model, boolean empty) {
                super.updateItem(model, empty);

                if (empty || model == null) {
                    setText(null);
                    setContextMenu(null);
                } else {
                    setText(model.getName());

                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem deleteItem = new MenuItem("Удалить");
                    deleteItem.setOnAction(e -> {
                        ///Прописать удаление
                    });
                }
            }
        });
    }

    private void showExceptionMessage(String text) {
        Stage excStage = new Stage();
        excStage.setTitle("Уведомление");

        Text excText = new Text(text);
        excText.getStyleClass().add("exception-text");

        TextArea excTextArea = new TextArea(text);
        excTextArea.setWrapText(true);
        excTextArea.setEditable(false);
        excTextArea.getStyleClass().add("exception-textarea");

        Button closeButton = new Button("Закрыть");
        closeButton.setOnAction(e -> excStage.close());
        closeButton.getStyleClass().add("close-button");

        StackPane root = new StackPane();
        root.getChildren().addAll(excText, closeButton);
        root.getStyleClass().add("exception-window");

        StackPane.setAlignment(excTextArea, Pos.CENTER);
        StackPane.setAlignment(closeButton, Pos.BOTTOM_CENTER);

        Scene scene = new Scene(root, 800, 100);
        excStage.setScene(scene);

        excStage.show();

    }
    @FXML
    private void onAddModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Загрузить модель");

        File file = fileChooser.showOpenDialog(modelListView.getScene().getWindow());
        Model model = GuiUtils.addModel(file);
        if (model != null) {
            modelListView.getItems().add(model);
        }
    }

    @FXML
    private void onSaveModelMenuItemClick() {
        if (selectedModel == null) {
            showExceptionMessage("Необходимо выбрать модель");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Сохранить модель");

        File file = fileChooser.showSaveDialog(canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            ObjWriter.write(selectedModel, file.getAbsolutePath());
            showExceptionMessage("Модель сохранена");
        } catch (IOException exception) {
            showExceptionMessage("Не удалось сохранить модель: " + exception.getMessage());
        }
    }
    @FXML
    void setSelectionModelsPane() {
        System.out.println("lol");
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }
}