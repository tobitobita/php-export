package dsk.export.delegate;

public interface DataBind<E> {
    DataBind<E> setDataBindObject(E dataBindObj);

    void bind();
}
