package dsk.export;

import java.io.IOException;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import dsk.export.exception.ExportException;

public interface ClassExport {
    public enum ExportState {
        ES_SUCCESS, ES_FAILD
    }

    ExportState export(ProjectAccessor projectAccessor) throws ProjectNotFoundException,
            IOException, ExportException;

    String createSkeletonCode(IClass clazz) throws IOException;
}
