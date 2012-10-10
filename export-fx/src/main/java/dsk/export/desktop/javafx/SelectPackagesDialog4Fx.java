package dsk.export.desktop.javafx;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.model.IClass;

import dsk.export.ExportPath.ChooseState;
import dsk.export.delegate.DataSelect;
import dsk.export.desktop.javafx.application.SelectPackagesApplication;
import dsk.export.desktop.javafx.controller.SelectPackagesController;

public class SelectPackagesDialog4Fx extends JDialog implements DataSelect<List<IClass>> {
    private static final long serialVersionUID = -4826721813465939774L;
    private static final Logger LOG = LoggerFactory.getLogger(SelectPackagesDialog4Fx.class);

    private ChooseState chooseState = ChooseState.CANCEL;

    private List<IClass> list = new ArrayList<>();

    public SelectPackagesDialog4Fx() {
        LOG.trace("SelectPackagesDialog");
        this.initialize();
    }

    public void initialize() {
        this.initUI();
    }

    private void initUI() {
        this.setBounds(100, 100, 450, 300);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // this.setModal(true);

        // JavaFXのコンポーネントを貼り付けるPanel
        final JFXPanel fxPanel = new JFXPanel();
        this.add(fxPanel);
        // JavaFXのThreadでsetSceneすること
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                SelectPackagesApplication app = new SelectPackagesApplication();
                fxPanel.setScene(app.createScene());
                SelectPackagesController controller = app.getController();
                controller.setDataBindObject(list);
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
        this.setVisible(true);
        return this.chooseState;
    }

    @Override
    public List<IClass> getSelectedData() {
        // TODO
        return null;
    }
}
