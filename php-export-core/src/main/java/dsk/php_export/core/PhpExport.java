package dsk.php_export.core;

import java.io.IOException;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import dsk.php_export.core.exception.ExportException;

public interface PhpExport {
    public enum ExportState {
        ES_SUCCESS, ES_FAILD
    }

    ExportState export(ProjectAccessor projectAccessor) throws ProjectNotFoundException,
            IOException, ExportException;
}
