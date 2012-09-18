package dsk.php_export.core.desktop.swing;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dsk.common.exception.DskRuntimeException;
import dsk.php_export.core.ExportPath;

public class ExportPathDialog implements ExportPath {
    private static final Logger LOG = LoggerFactory.getLogger(ExportPathDialog.class);

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
        // TODO JFileDialogへ変えること
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        ChooseState result = ChooseState.CANCEL;
        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(this.parent)) {
            try {
                this.choosePath = fileChooser.getSelectedFile().getCanonicalPath();
                LOG.trace(this.choosePath);
                result = ChooseState.OK;
            } catch (IOException e) {
                throw new DskRuntimeException(e);
            }
        }
        return result;
    }

    @Override
    public String getChoosePath() {
        return this.choosePath;
    }
}
