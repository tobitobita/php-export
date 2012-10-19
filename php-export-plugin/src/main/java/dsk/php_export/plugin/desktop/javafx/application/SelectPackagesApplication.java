package dsk.php_export.plugin.desktop.javafx.application;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dsk.common.exception.DskRuntimeException;
import dsk.php_export.plugin.desktop.javafx.controller.SelectPackagesController;

public class SelectPackagesApplication extends Application {
    private ClassLoader classLoader;

    private SelectPackagesController controller;

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println(System.getProperty("javafx.version"));
        stage.setScene(this.createScene());
        stage.show();
    }

    public Scene createScene() {
        ClassLoader theClassLoader = getClass().getClassLoader();
        if (null != this.classLoader) {
            theClassLoader = this.classLoader;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setClassLoader(theClassLoader);
        try (InputStream is = theClassLoader
                .getResource("dsk/php_export/plugin/desktop/javafx/application/selectPackages.fxml")
                .openConnection().getInputStream();) {
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

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public static void main(String[] args) {
        Application.launch();
    }
}
