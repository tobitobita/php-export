package dsk.php_export.core.delegate;

import dsk.php_export.core.ExportPath.ChooseState;

public interface DataSelect<E> {
    void setData(E data);

    ChooseState select();

    E getSelectedData();
}
