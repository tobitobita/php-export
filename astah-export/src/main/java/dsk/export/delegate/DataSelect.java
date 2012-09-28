package dsk.export.delegate;

import dsk.export.ExportPath.ChooseState;

public interface DataSelect<E> {
    void setData(E data);

    ChooseState select();

    E getSelectedData();
}
