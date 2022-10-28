package com.artyommameev.psfsv1unpacker.controller;

import com.artyommameev.psfsv1unpacker.Main;
import com.artyommameev.psfsv1unpacker.domain.Resource;
import com.artyommameev.psfsv1unpacker.unpack.PsFsV1Unpacker;
import com.artyommameev.psfsv1unpacker.util.SimpleAlertCreator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.val;

import java.io.File;
import java.io.IOException;

/**
 * The main JavaFX controller of the application.
 *
 * @author Artyom Mameev
 */
public class MainController {

    @FXML
    private MenuItem openFileMenuItem;
    @FXML
    private MenuItem closeFileMenuItem;
    @FXML
    private MenuItem unpackMenuItem;
    @FXML
    private MenuItem selectAllMenuItem;
    @FXML
    private MenuItem clearSelectionMenuItem;
    @FXML
    private Button openButton;
    @FXML
    private Button unpackButton;
    @FXML
    private TextField pathTextField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ListView<Resource> resourceListView;

    private String fileName;
    private PsFsV1Unpacker psFsV1Unpacker;
    private ObservableList<Resource> resources;

    private boolean isUnpacking = false;

    /**
     * Initializes the main application window.
     */
    public void initialize() {
        resourceListView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE);

        pathTextField.setEditable(false);

        initContextMenu();

        configureButtons(AppState.FILE_CLOSED);
    }

    @FXML
    private void openAction() {
        val resourceFile = resourceFileChooser().showOpenDialog(
                Main.getPrimaryStage());

        if (resourceFile == null) {
            return;
        }

        setNewFileInfo(resourceFile);

        new Thread(openFileRunnable(resourceFile)).start();
    }

    @FXML
    private void closeFileAction() {
        clearFileInfo();

        configureButtons(AppState.FILE_CLOSED);
    }

    @FXML
    private void selectAllAction() {
        Platform.runLater(() -> {
            resourceListView.getSelectionModel().selectAll();
            resourceListView.refresh();
        });
    }

    @FXML
    private void clearSelectionAction() {
        Platform.runLater(() -> {
            resourceListView.getSelectionModel().clearSelection();
            resourceListView.refresh();
        });
    }

    @FXML
    private void unpackAction() {
        if (resourceListView.getSelectionModel().isEmpty()) {
            Platform.runLater(() -> SimpleAlertCreator.createErrorAlert(
                    "Unpack", "Please select files to unpack")
                    .showAndWait());
            return;
        }

        val directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Choose Unpack Directory");

        val unpackDirectory = directoryChooser.showDialog(
                Main.getPrimaryStage());

        if (unpackDirectory == null || psFsV1Unpacker == null) {
            return;
        }

        configureButtons(AppState.UNPACKING);

        new Thread(unpackRunnable(unpackDirectory)).start();
    }

    @FXML
    private void quitAction() {
        if (isUnpacking) {
            val alert = SimpleAlertCreator.createAreYouSureDialog(
                    "Quit?");

            Platform.runLater(() -> {
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    Platform.exit();
                }
            });
        } else {
            Platform.exit();
        }
    }

    private void initContextMenu() {
        val contextMenu = new ContextMenu();

        val selectAllContext = new MenuItem("Select All");
        val clearSelectionContext = new MenuItem("Clear Selection");

        selectAllContext.setOnAction(event -> selectAllAction());
        clearSelectionContext.setOnAction(event -> clearSelectionAction());

        contextMenu.getItems().addAll(selectAllContext, clearSelectionContext);

        resourceListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                contextMenu.show(resourceListView, event.getScreenX(),
                        event.getScreenY());
            }

            if (event.getButton().equals(MouseButton.PRIMARY)) {
                contextMenu.hide();
            }
        });
    }

    private void configureButtons(AppState state) {
        switch (state) {
            case FILE_CLOSED:
                openFileMenuItem.setDisable(false);
                openButton.setDisable(false);
                closeFileMenuItem.setDisable(true);
                unpackMenuItem.setDisable(true);
                unpackButton.setDisable(true);
                selectAllMenuItem.setDisable(true);
                clearSelectionMenuItem.setDisable(true);
                break;
            case FILE_OPENED:
                openFileMenuItem.setDisable(false);
                openButton.setDisable(false);
                closeFileMenuItem.setDisable(false);
                unpackMenuItem.setDisable(false);
                unpackButton.setDisable(false);
                selectAllMenuItem.setDisable(false);
                clearSelectionMenuItem.setDisable(false);
                break;
            case UNPACKING:
                openFileMenuItem.setDisable(true);
                openButton.setDisable(true);
                closeFileMenuItem.setDisable(true);
                unpackMenuItem.setDisable(true);
                unpackButton.setDisable(true);
                selectAllMenuItem.setDisable(true);
                clearSelectionMenuItem.setDisable(true);
                break;
        }
    }

    private Runnable openFileRunnable(File selectedFile) {
        return () -> {
            try {
                psFsV1Unpacker = new PsFsV1Unpacker(selectedFile);

                resources = FXCollections.observableArrayList();

                resources.addAll(psFsV1Unpacker.getAllResources());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();

                Platform.runLater(() -> SimpleAlertCreator.createErrorAlert(
                        "Open File", "Invalid file format")
                        .showAndWait());

                return;
            }

            Platform.runLater(() -> resourceListView.setItems(resources));

            configureButtons(AppState.FILE_OPENED);
        };
    }

    private Runnable unpackRunnable(File unpackDirectory) {
        return () -> {
            try {
                isUnpacking = true;

                initProgressBar();

                val selectedResources = resourceListView
                        .getSelectionModel().getSelectedItems();

                for (int i = 0; i < selectedResources.size(); i++) {
                    psFsV1Unpacker.unpackResource(unpackDirectory,
                            selectedResources.get(i));

                    updateProgressBar(i, selectedResources.size());
                }

                isUnpacking = false;

                configureButtons(AppState.FILE_OPENED);

                Platform.runLater(() -> {
                    SimpleAlertCreator.createInfoAlert("Unpack",
                            "Unpacking completed successfully")
                            .showAndWait();

                    progressBar.setProgress(0);

                    setWindowTitle(Main.APP_NAME + " - " + fileName);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void updateProgressBar(int iteration, int size) {
        float progress = (float) iteration / size;

        Platform.runLater(() -> {
            if (progress >= 0.01) {
                progressBar.setProgress(progress);

                setWindowTitle(Main.APP_NAME + " - " + "Unpacking: " +
                        progress * 100 + "%");
            }
        });
    }

    private FileChooser resourceFileChooser() {
        val fileChooser = new FileChooser();

        fileChooser.setTitle("Open Resource File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Dat Files",
                        "*.dat"),
                new FileChooser.ExtensionFilter("All Files",
                        "*.*"));

        return fileChooser;
    }

    private void setNewFileInfo(File resourceFile) {
        Platform.runLater(() -> {
            pathTextField.setText(String.valueOf(resourceFile));

            fileName = resourceFile.getName();

            setWindowTitle(Main.APP_NAME + " - " + fileName);
        });
    }

    private void clearFileInfo() {
        Platform.runLater(() -> {
            pathTextField.setText("File Path");

            resourceListView.getItems().clear();
        });
    }

    private void initProgressBar() {
        Platform.runLater(() -> {
            progressBar.setProgress(-1.0);
            setWindowTitle(Main.APP_NAME + " - " + "Initialization...");
        });
    }

    private void setWindowTitle(String title) {
        Platform.runLater(() -> Main.getPrimaryStage().setTitle(title));
    }

    private enum AppState {FILE_CLOSED, UNPACKING, FILE_OPENED}
}