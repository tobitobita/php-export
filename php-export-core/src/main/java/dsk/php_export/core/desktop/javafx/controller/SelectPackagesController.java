package dsk.php_export.core.desktop.javafx.controller;

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
import dsk.php_export.core.delegate.DataBind;

public class SelectPackagesController implements Initializable, DataBind<List<String>> {
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
        System.out.println("ButtonAction");
        ObservableList<CheckBox> items = this.classListView.getItems();
        for (CheckBox checkBox : items) {
            if (checkBox.isSelected()) {
                System.out.println(checkBox.getText());
            }
        }
    }

    /* Data binging */

    private List<String> dataBindObject = new ArrayList<String>();

    @Override
    public DataBind<List<String>> setDataBindObject(List<String> dataBindObject) {
        this.dataBindObject = dataBindObject;
        return this;
    }

    @Override
    public void bind() {
        List<CheckBox> list = new ArrayList<CheckBox>();
        for (String data : this.dataBindObject) {
            list.add(new CheckBox(data));
        }
        ObservableList<CheckBox> oList = FXCollections.observableArrayList(list);
        this.classListView.setItems(oList);
    }
}
