package dsk.php_export.plugin.desktop.swing;

import javax.swing.JCheckBox;

public class ModelCheckBox<E> extends JCheckBox {
    private static final long serialVersionUID = -8129049180979005450L;

    private E object;

    public E getObject() {
        return object;
    }

    public void setObject(E object) {
        this.object = object;
    }
}
