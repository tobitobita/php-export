package dsk.php_export.core.delegate;

public interface DataBind<E> {
    DataBind<E> setDataBindObject(E dataBindObj);

    void bind();
}
