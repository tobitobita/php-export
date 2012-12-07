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

import dsk.common.util.R;
import dsk.export.ClassExport;
import dsk.export.ClassExport.ExportState;
import dsk.export.exception.ExportException;
import dsk.php_export.plugin.module.PhpExportModule;

public class PhpExportAction implements IPluginActionDelegate {
	private static final Logger LOG = LoggerFactory.getLogger(PhpExportAction.class);

	public Object run(final IWindow window) throws UnExpectedException {
		execute(window);
		return null;
	}

	private void execute(final IWindow window) throws UnExpectedException {
		try {
			Injector injector = Guice.createInjector(Stage.PRODUCTION, new PhpExportModule());
			ClassExport export = injector.getInstance(ClassExport.class);
			if (ExportState.ES_SUCCESS == export.export(ProjectAccessorFactory.getProjectAccessor())) {
				JOptionPane.showMessageDialog(window.getParent(), R.m("出力しました"), "", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (ProjectNotFoundException e) {
			LOG.warn(e.getMessage());
			String message = "Project is not opened.Please open the project or create new project.";
			JOptionPane.showMessageDialog(window.getParent(), message, "Warning", JOptionPane.WARNING_MESSAGE);
		} catch (ClassNotFoundException e) {
			LOG.error(e.getMessage(), e);
			throw new UnExpectedException();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new UnExpectedException();
		} catch (ExportException e) {
			LOG.error(e.getMessage(), e);
			JOptionPane.showMessageDialog(window.getParent(), R.m("失敗しました"), "", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new UnExpectedException();
		}
	}
}
