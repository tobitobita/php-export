package dsk.php_export.core.desktop.javafx.application;

import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dsk.php_export.core.ExportPath.ChooseState;
import dsk.php_export.core.delegate.DataSelect;
import dsk.php_export.core.desktop.javafx.controller.SelectPackagesController;

public class SelectPackagesApplication extends Application implements DataSelect<List<String>> {
    private Stage stage;
    private SelectPackagesController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.load(getClass().getResourceAsStream("selectPackages.fxml"));
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        this.stage = stage;
        this.controller = loader.getController();
    }

    /* select window */

    @Override
    public void setData(List<String> data) {
        this.controller.setDataBindObject(data).bind();
    }

    @Override
    public ChooseState select() {
        this.stage.show();
        return ChooseState.OK;
    }

    @Override
    public List<String> getSelectedData() {
        // TODO Auto-generated method stub
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
