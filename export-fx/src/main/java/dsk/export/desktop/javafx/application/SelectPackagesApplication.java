package dsk.export.desktop.javafx.application;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dsk.common.exception.DskRuntimeException;
import dsk.export.desktop.javafx.controller.SelectPackagesController;

public class SelectPackagesApplication extends Application {
    private SelectPackagesController controller;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(this.createScene());
        stage.show();
    }

    public Scene createScene() {
        FXMLLoader loader = new FXMLLoader();
        try {
            InputStream is = getClass().getResourceAsStream("selectPackages.fxml");
            loader.load(is);
            this.controller = loader.getController();
        } catch (IOException e) {
            throw new DskRuntimeException("fxmlの指定が不正です", e);
        }
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        return scene;
    }

    /* getter, setter */

    public SelectPackagesController getController() {
        return this.controller;
    }

    public static void main(String[] args) {
        Application.launch();
    }
}
