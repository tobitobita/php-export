package dsk.php_export.core.desktop.swing;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JFileChooser;

import dsk.php_export.core.ExportPath;

public class ExportPathDialog implements ExportPath {
    private Component parent;

    private String choosePath;

    public ExportPathDialog() {
        super();
    }

    public ExportPathDialog(Component parent) {
        super();
        this.parent = parent;
    }

    @Override
    public ChooseState choose() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        ChooseState result = ChooseState.CANCEL;
        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(this.parent)) {
            try {
                this.choosePath = fileChooser.getSelectedFile().getCanonicalPath();
                System.out.println(this.choosePath);
                result = ChooseState.OK;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public String getChoosePath() {
        return this.choosePath;
    }
}
