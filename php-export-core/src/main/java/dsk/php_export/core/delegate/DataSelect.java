package dsk.php_export.core.delegate;

public interface DataSelect<E> {
    void setData(E data);

    void select();

    E getSelectedData();
}
