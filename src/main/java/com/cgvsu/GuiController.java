package com.cgvsu;

import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.RenderEngine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.io.File;
import java.util.Objects;

import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.math.*;


import static com.cgvsu.GuiUtils.*;
import static com.cgvsu.render_engine.RenderEngine.*;
import static com.cgvsu.render_engine.RenderEngine.resetSelectedVertices;

public class GuiController {

    final private float TRANSLATION = 0.5F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;
    @FXML
    private TextField camX;
    @FXML
    private TextField camY;
    @FXML
    private TextField camZ;
    @FXML
    private ListView<Camera> camerasListView;
    @FXML
    private ListView<Model> modelListView;
    @FXML
    private ListView<String> vertexListView;
    @FXML
    private ToggleButton verticesToggleButton;
    private boolean isSelectedCamera = false;

    private Camera mainCamera = new Camera(
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
            mainCamera.setAspectRatio((float) (width / height));

            for (Model model : models) {
                RenderEngine.render(canvas.getGraphicsContext2D(), mainCamera, model, (int) width, (int) height);
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
                } else {
                    setText(model.getName());

                    setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            if (selectedModel == model) {
                                selectedModel = null;
                                updateButtonState();
                                vertexListView.getItems().clear();
                                resetSelectedVertices();
                            } else {
                                selectedModel = model;
                                updateButtonState();
                                updateVertexList();
                                resetSelectedVertices();
                            }
                        }
                    });
                }
            }
        });

        vertexListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        vertexListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                handleVertexSelection();
                event.consume();
            }
        });
        verticesToggleButton.setOnAction(event -> handleToggleVerticesAction());

        cameras = FXCollections.observableArrayList();
        camerasListView.setItems((ObservableList<Camera>) cameras);

        camerasListView.setCellFactory(e -> {
            ListCell<Camera> cell = new ListCell<>() {
                @Override
                protected void updateItem(Camera camera, boolean empty) {
                    super.updateItem(camera, empty);
                    if (empty || camera == null) {
                        setText(null);
                    } else {
                        setText("Камера (" + camera.getPosition().x + ", " + camera.getPosition().y + ", " + camera.getPosition().z + ")");
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && !cell.isEmpty()) {
                    selectedCamera = cell.getItem();
                    mainCamera = new Camera(selectedCamera);
                }
            });

            return cell;
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
//--------------------------------Камеры------------------------------------------
    }
    private void openEditDialog(Camera camera) {
        if (camera == null) return;

        Stage editStage = new Stage();
        editStage.initStyle(StageStyle.TRANSPARENT);

        editStage.setTitle("Редактировать камеру");
        editStage.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Label positionLabel = new Label("Позиция камеры:");
        TextField xField = new TextField(String.valueOf(camera.getPosition().x));
        xField.setPromptText("X координата");
        TextField yField = new TextField(String.valueOf(camera.getPosition().y));
        yField.setPromptText("Y координата");
        TextField zField = new TextField(String.valueOf(camera.getPosition().z));
        zField.setPromptText("Z координата");

        Label targetLabel = new Label("Цель камеры:");
        TextField targetXField = new TextField(String.valueOf(camera.getTarget().x));
        targetXField.setPromptText("X координата цели");
        TextField targetYField = new TextField(String.valueOf(camera.getTarget().y));
        targetYField.setPromptText("Y координата цели");
        TextField targetZField = new TextField(String.valueOf(camera.getTarget().z));
        targetZField.setPromptText("Z координата цели");

        Label fovLabel = new Label("FOV:");
        Slider fovSlider = new Slider(0.1, 180, camera.getFov());
        fovSlider.setShowTickLabels(true);
        fovSlider.setShowTickMarks(true);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Сохранить");
        Button cancelButton = new Button("Отмена");

        buttonBox.getChildren().addAll(saveButton, cancelButton);

        layout.getChildren().addAll(
                new Label("Редактировать параметры камеры"),
                positionLabel,
                new Label("X:"), xField,
                new Label("Y:"), yField,
                new Label("Z:"), zField,
                targetLabel,
                new Label("Цель X:"), targetXField,
                new Label("Цель Y:"), targetYField,
                new Label("Цель Z:"), targetZField,
                fovLabel, fovSlider,
                buttonBox
        );

        Scene scene = new Scene(layout, 350, 600);
        editStage.setScene(scene);

        saveButton.setOnAction(e -> {
            try {
                float x = Float.parseFloat(xField.getText());
                float y = Float.parseFloat(yField.getText());
                float z = Float.parseFloat(zField.getText());

                float targetX = Float.parseFloat(targetXField.getText());
                float targetY = Float.parseFloat(targetYField.getText());
                float targetZ = Float.parseFloat(targetZField.getText());

                float fov = (float) fovSlider.getValue();

                GuiUtils.setCamera(camera, x, y, z, targetX, targetY, targetZ, fov);

                camerasListView.refresh();

                editStage.close();
            } catch (NumberFormatException ex) {
                showExceptionMessage("Ошибка: некорректные значения в полях");
            }
        });

        cancelButton.setOnAction(e -> editStage.close());

        editStage.showAndWait();
    }
    @FXML
    void onAddCamera() {
        try {
            float x = Float.parseFloat(camX.getText());
            float y = Float.parseFloat(camY.getText());
            float z = Float.parseFloat(camZ.getText());

            Camera newCamera = GuiUtils.createCamera(x, y, z);

            camerasListView.getItems().add(newCamera);
            camerasListView.getSelectionModel().select(newCamera);
        } catch (NumberFormatException e) {
            showExceptionMessage("Некорректные координаты камеры");
        }
    }
    @FXML
    void onEditCamera() {
        if (selectedCamera != null) {
            openEditDialog(selectedCamera);
        } else {
            showExceptionMessage("Выберите камеру перед редактированием.");
        }
    }
    @FXML
    void onDeleteCamera() {
        if (selectedCamera != null) {
            GuiUtils.removeCamera(selectedCamera);
        } else {
            showExceptionMessage("Выберите камеру перед удалением.");
        }
    }
//----------------------------------Вершины----------------------------------------
    private void renderScene() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        mainCamera.setAspectRatio((float) (canvasWidth / canvasHeight));

        for (Model model : models) {
            RenderEngine.render(gc, mainCamera, model, (int) canvasWidth, (int) canvasHeight);
        }
    }
    private void updateButtonState() {
        if (selectedModel == null) {
            verticesToggleButton.setSelected(false);
        } else {
            verticesToggleButton.setSelected(selectedModel.isVerticesVisible());
        }
    }
    @FXML
    void onDeleteVerticesButton() {
        if (selectedModel == null) {
            showExceptionMessage("Модель не выбрана");
            return;
        }
        deleteSelectedVertices(selectedModel);
        updateVertexList();
        renderScene();
    }
    public void handleToggleVerticesAction() {
        if (selectedModel == null) {
            showExceptionMessage("Модель не выбрана");
            verticesToggleButton.setSelected(false);
            return;
        }

        RenderEngine.toggleVerticesVisibility(selectedModel);
        renderScene();
    }
    private void handleVertexSelection() {
        ObservableList<Integer> selectedIndices = vertexListView.getSelectionModel().getSelectedIndices();

        for (Integer index : selectedIndices) {
            if (selectedVertexIndices.contains(index)) {
                selectedVertexIndices.remove(index);
            } else {
                selectedVertexIndices.add(index);
            }
        }
        updateVertexList();
    }
    private void updateVertexList() {
        vertexListView.getItems().clear();

        if (selectedModel != null) {
            for (int i = 0; i < selectedModel.vertices.size(); i++) {
                com.cgvsu.math.Vector3f vertex = selectedModel.vertices.get(i);
                vertexListView.getItems().add("Vertex " + i + ": (" + vertex.getX() + ", " + vertex.getY() + ", " + vertex.getZ() + ")");
            }
        }

        vertexListView.setCellFactory(param -> new VertexListCell());
    }
    public class VertexListCell extends ListCell<String> {
        private final CheckBox checkBox;
        private final Text vertexText;

        public VertexListCell() {
            HBox hbox = new HBox(10);
            vertexText = new Text();
            checkBox = new CheckBox();
            hbox.getChildren().addAll(vertexText, checkBox);
            setGraphic(hbox);
        }
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            } else {
                vertexText.setText(item);
                vertexText.setFill(Color.BLACK);

                int index = getIndex();

                checkBox.setSelected(selectedVertexIndices.contains(index));
                checkBox.setOnAction(event -> toggleSelection(index));
                vertexText.setOnMouseClicked(event -> toggleSelection(index));

                HBox hbox = new HBox();
                hbox.setSpacing(10);
                hbox.getChildren().add(checkBox);
                hbox.getChildren().add(vertexText);
                hbox.setAlignment(Pos.CENTER_LEFT);

                setGraphic(hbox);
            }
        }

        private void toggleSelection(int index) {
            if (selectedVertexIndices.contains(index)) {
                selectedVertexIndices.remove(Integer.valueOf(index));
            } else {
                selectedVertexIndices.add(index);
            }

            updateVertexList();
        }
    }
//-----------------------------------Модели----------------------------------------
    @FXML
    void onDeleteModel() {
        if (selectedModel == null) {
            showExceptionMessage("Не выбрана модель для удаления");
            return;
        }
        GuiUtils.deleteModel(selectedModel);
        vertexListView.getItems().clear();
        modelListView.getItems().remove(modelListView.getSelectionModel().getSelectedItem());
        selectedModel = null;
        modelListView.getSelectionModel().clearSelection();
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
    public void handleCameraForward(ActionEvent actionEvent) {
        mainCamera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        mainCamera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        mainCamera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        mainCamera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        mainCamera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        mainCamera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }
}