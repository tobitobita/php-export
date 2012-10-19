package dsk.php_export.plugin.desktop.javafx;

import javafx.scene.control.CheckBox;

import org.apache.commons.lang3.StringUtils;

import com.change_vision.jude.api.inf.model.IClass;

import dsk.export.tools.SkeletonCodeTools;

public class ModelCheckBox extends CheckBox {
    private IClass iClass;

    public ModelCheckBox(IClass clazz) {
        super();
        SkeletonCodeTools tools = new SkeletonCodeTools();
        String namespace = tools.getNamespace(clazz).replace("\\", ".");
        StringBuilder sb = new StringBuilder(clazz.getName());
        if (!StringUtils.isEmpty(namespace)) {
            sb.insert(0, ".");
            sb.insert(0, namespace);
        }
        this.setText(sb.toString());
        this.iClass = clazz;
    }

    public IClass getIClass() {
        return this.iClass;
    }
}
