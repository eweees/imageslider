package com.example;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

public class Controller {
    @FXML private ImageView imageView;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Button chooseDirButton;

    private ConcreteAggregate slides;
    private List<File> imageFiles;
    private int currentIndex = 0;

    private Timeline timeline;
    private Stage stage;

    @FXML
    public void initialize() {
        // Получаем Stage
        imageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((wObs, oldWin, newWin) -> {
                    if (newWin instanceof Stage s) {
                        stage = s;
                    }
                });
            }
        });

        // Инициализация агрегата
        AbstractAggregateFactory factory = new ImageAggregateFactory();
        slides = (ConcreteAggregate) factory.createAggregate("jpg,png");
        imageFiles = slides.getImageFiles();
        currentIndex = 0;

        // Показываем первое изображение, если есть
        if (!imageFiles.isEmpty()) {
            showImageAt(currentIndex);
        }

        // Timeline для автопоказа
        timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> nextImage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void showImageAt(int index) {
        if (imageFiles.isEmpty() || index < 0 || index >= imageFiles.size()) {
            imageView.setImage(null);
            return;
        }
        currentIndex = index;
        File file = imageFiles.get(index);
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);

        // Плавный fade-in при ручной навигации
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), imageView);
        fadeIn.setFromValue(0.3);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    private void nextImage() {
        if (!imageFiles.isEmpty()) {
            currentIndex = (currentIndex + 1) % imageFiles.size();
            showImageAt(currentIndex);
        }
    }

    @FXML
    private void previousImage() {
        if (!imageFiles.isEmpty()) {
            currentIndex = (currentIndex - 1 + imageFiles.size()) % imageFiles.size();
            showImageAt(currentIndex);
        }
    }

    @FXML
    private void startSlideshow() {
        timeline.play();
    }

    @FXML
    private void stopSlideshow() {
        timeline.stop();
    }

    @FXML
    private void chooseDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(stage);
        if (dir != null) {
            slides.setDirectory(dir.getAbsolutePath());
            imageFiles = slides.getImageFiles();
            currentIndex = 0;
            if (!imageFiles.isEmpty()) {
                showImageAt(currentIndex);
            } else {
                imageView.setImage(null);
            }
        }
    }
}