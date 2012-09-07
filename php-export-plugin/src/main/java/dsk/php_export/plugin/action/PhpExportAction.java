package dsk.php_export.plugin.action;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

import dsk.php_export.core.PhpExport;
import dsk.php_export.core.PhpExport.ExportState;
import dsk.php_export.core.exception.ExportException;
import dsk.php_export.core.service.PhpExportService;

public class PhpExportAction implements IPluginActionDelegate {

    public Object run(IWindow window) throws UnExpectedException {
        try {
            PhpExport export = new PhpExportService(null, null);
            if (ExportState.ES_SUCCESS == export
                    .export(ProjectAccessorFactory.getProjectAccessor())) {
                JOptionPane.showMessageDialog(window.getParent(), "出力しました", "",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(window.getParent(), "出力しませんでした", "",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (ProjectNotFoundException e) {
            String message = "Project is not opened.Please open the project or create new project.";
            JOptionPane.showMessageDialog(window.getParent(), message, "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new UnExpectedException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnExpectedException();
        } catch (ExportException e) {
            JOptionPane.showMessageDialog(window.getParent(), "失敗しました", "",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
