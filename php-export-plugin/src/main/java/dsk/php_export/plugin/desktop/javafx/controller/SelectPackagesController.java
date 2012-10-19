package dsk.php_export.plugin.desktop.javafx.controller;

import static javafx.scene.control.SelectionMode.SINGLE;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.model.IClass;

import dsk.export.delegate.DataBind;
import dsk.php_export.plugin.desktop.javafx.CloseDialog;
import dsk.php_export.plugin.desktop.javafx.ModelCheckBox;

/**
 * TODO JavaFX
 * 
 */
public class SelectPackagesController implements Initializable, DataBind<List<IClass>> {
    private static final Logger LOG = LoggerFactory.getLogger(SelectPackagesController.class);

    // TODO delegate
    private CloseDialog closeDialog;

    @FXML
    private ListView<ModelCheckBox> classListView;

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        this.initUI();
    }

    private void initUI() {
        this.classListView.getSelectionModel().setSelectionMode(SINGLE);
        this.classListView
                .setCellFactory(new Callback<ListView<ModelCheckBox>, ListCell<ModelCheckBox>>() {
                    @Override
                    public ListCell<ModelCheckBox> call(ListView<ModelCheckBox> listView) {
                        return new ListCell<ModelCheckBox>() {
                            @Override
                            public void updateItem(ModelCheckBox item, boolean empty) {
                                super.updateItem(item, empty);
                                this.setGraphic(item);
                            }
                        };
                    }
                });
    }

    @FXML
    private void handleButtonAction(ActionEvent e) {
        LOG.trace("ButtonAction");
        this.closeDialog.closeByOk();
    }

    /* Data binging */

    private List<IClass> dataBindObject = new ArrayList<IClass>();

    @Override
    public DataBind<List<IClass>> setDataBindObject(List<IClass> dataBindObject) {
        this.dataBindObject = dataBindObject;
        return this;
    }

    @Override
    public void bind() {
        List<ModelCheckBox> list = new ArrayList<>();
        for (IClass data : this.dataBindObject) {
            list.add(new ModelCheckBox(data));
        }
        ObservableList<ModelCheckBox> oList = FXCollections.observableArrayList(list);
        this.classListView.setItems(oList);
    }

    /* setter, getter */

    public ListView<ModelCheckBox> getClassListView() {
        return classListView;
    }

    public void setCloseDialog(CloseDialog dialog) {
        this.closeDialog = dialog;
    }
}
