package dsk.php_export.core;

public interface ExportPath {
    public enum ChooseState {
        OK, CANCEL
    }

    ChooseState choose();

    String getChoosePath();
}
