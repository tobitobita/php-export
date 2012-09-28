package dsk.php_export.plugin.desktop;

import java.util.List;

import com.change_vision.jude.api.inf.model.IClass;

import dsk.export.delegate.DataSelect;
import dsk.php_export.plugin.desktop.swing.SelectPackagesDialog;

public class SelectWindowFactory {
    private static final SelectWindowFactory FACTORY = new SelectWindowFactory();

    public static SelectWindowFactory getFactory() {
        return FACTORY;
    }

    public DataSelect<List<IClass>> createSwingDataSelect() {
        return new SelectPackagesDialog();
    }
}
