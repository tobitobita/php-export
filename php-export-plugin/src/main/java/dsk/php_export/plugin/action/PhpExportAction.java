package dsk.php_export.plugin.action;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import dsk.php_export.core.PhpExport;
import dsk.php_export.core.PhpExport.ExportState;
import dsk.php_export.core.exception.ExportException;
import dsk.php_export.plugin.module.PhpExportModule;

public class PhpExportAction implements IPluginActionDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(PhpExportAction.class);

    public Object run(IWindow window) throws UnExpectedException {
        try {
            Injector injector = Guice.createInjector(Stage.PRODUCTION, new PhpExportModule());
            PhpExport export = injector.getInstance(PhpExport.class);
            if (ExportState.ES_SUCCESS == export
                    .export(ProjectAccessorFactory.getProjectAccessor())) {
                JOptionPane.showMessageDialog(window.getParent(), "出力しました", "",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (ProjectNotFoundException e) {
            LOG.warn(e.getMessage());
            String message = "Project is not opened.Please open the project or create new project.";
            JOptionPane.showMessageDialog(window.getParent(), message, "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new UnExpectedException();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new UnExpectedException();
        } catch (ExportException e) {
            LOG.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(window.getParent(), "失敗しました", "",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
