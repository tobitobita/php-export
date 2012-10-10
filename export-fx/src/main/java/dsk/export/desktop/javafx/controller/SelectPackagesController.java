package dsk.export.desktop.javafx.controller;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.model.IClass;

import dsk.export.delegate.DataBind;

/**
 * TODO JavaFX
 * 
 */
public class SelectPackagesController implements Initializable, DataBind<List<IClass>> {
    private static final Logger LOG = LoggerFactory.getLogger(SelectPackagesController.class);

    @FXML
    private ListView<CheckBox> classListView;

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        this.initUI();
    }

    private void initUI() {
        this.classListView.getSelectionModel().setSelectionMode(SINGLE);
        this.classListView.setCellFactory(new Callback<ListView<CheckBox>, ListCell<CheckBox>>() {
            @Override
            public ListCell<CheckBox> call(ListView<CheckBox> listView) {
                return new ListCell<CheckBox>() {
                    @Override
                    public void updateItem(CheckBox item, boolean empty) {
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
        ObservableList<CheckBox> items = this.classListView.getItems();
        for (CheckBox checkBox : items) {
            if (checkBox.isSelected()) {
                LOG.debug(checkBox.getText());
            }
        }
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
        List<CheckBox> list = new ArrayList<CheckBox>();
        for (IClass data : this.dataBindObject) {
            list.add(new CheckBox(data.getName()));
        }
        ObservableList<CheckBox> oList = FXCollections.observableArrayList(list);
        this.classListView.setItems(oList);
    }
}
