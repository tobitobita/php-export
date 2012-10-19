package dsk.php_export.plugin.desktop.javafx;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;

import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.model.IClass;

import dsk.export.ExportPath.ChooseState;
import dsk.export.delegate.DataSelect;
import dsk.php_export.plugin.desktop.javafx.application.SelectPackagesApplication;
import dsk.php_export.plugin.desktop.javafx.controller.SelectPackagesController;

public class SelectPackagesDialog4Fx extends JDialog implements DataSelect<List<IClass>> {
    private static final long serialVersionUID = -4826721813465939774L;
    private static final Logger LOG = LoggerFactory.getLogger(SelectPackagesDialog4Fx.class);

    private ChooseState chooseState = ChooseState.CANCEL;

    private List<IClass> list = new ArrayList<>();
    private SelectPackagesController controller;

    private boolean init;

    public SelectPackagesDialog4Fx() {
        LOG.trace("SelectPackagesDialog");
    }

    private void initUI() {
        if (this.init) {
            return;
        }
        this.setBounds(100, 100, 450, 300);
        // DISPOSEすると、JavaFXスレッドが終了してしまう
        // this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setModal(true);

        final ClassLoader classLoader = getClass().getClassLoader();
        // JavaFXのコンポーネントを貼り付けるPanel
        final JFXPanel fxPanel = new JFXPanel();
        this.add(fxPanel);
        this.init = true;
        // JavaFXのThreadでsetSceneすること
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                SelectPackagesApplication app = new SelectPackagesApplication();
                app.setClassLoader(classLoader);
                fxPanel.setScene(app.createScene());
                controller = app.getController();
                controller.setCloseDialog(new CloseDialog() {
                    @Override
                    public void closeByOk() {
                        chooseState = ChooseState.OK;
                        setVisible(false);
                    }
                });
            }
        });
    }

    /* select window */

    @Override
    public void setData(List<IClass> data) {
        this.list.clear();
        for (IClass clazz : data) {
            this.list.add(clazz);
        }
    }

    @Override
    public ChooseState select() {
        chooseState = ChooseState.CANCEL;
        this.initUI();
        // TODO !!!!
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.setDataBindObject(list).bind();
            }
        });
        this.setVisible(true);
        return this.chooseState;
    }

    @Override
    public List<IClass> getSelectedData() {
        final List<IClass> list = new ArrayList<>();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<ModelCheckBox> items = controller.getClassListView().getItems();
                for (int i = 0; i < items.size(); ++i) {
                    ModelCheckBox modelCheckBox = items.get(i);
                    if (modelCheckBox.isSelected()) {
                        list.add(modelCheckBox.getIClass());
                    }
                }
            }
        });
        // TODO !!!!
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }
}
