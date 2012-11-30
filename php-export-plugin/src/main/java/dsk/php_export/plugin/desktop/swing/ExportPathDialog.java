package dsk.php_export.plugin.desktop.swing;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dsk.common.annotation.AutoLoad;
import dsk.common.annotation.AutoStore;
import dsk.common.annotation.PropertyKey;
import dsk.common.exception.DskRuntimeException;
import dsk.common.util.SystemTools;
import dsk.export.ExportPath;

public class ExportPathDialog implements ExportPath {
	private static final Logger LOG = LoggerFactory.getLogger(ExportPathDialog.class);

	private Component parent;

	private String choosePath;

	public ExportPathDialog() {
		super();
	}

	public ExportPathDialog(Component parent) {
		super();
		this.parent = parent;
	}

	@Override
	public ChooseState choose() {
		String path = this.getChoosePath();
		JFileChooser fileChooser = null;
		// TODO スマートに
		if (SystemTools.isMacOsX()) {
			fileChooser = new JFileChooser(path == null ? null : new File(path + "../"));
		} else {
			fileChooser = new JFileChooser(path);
		}
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (path != null) {
			fileChooser.setSelectedFile(new File(path));
		}
		ChooseState result = ChooseState.CANCEL;
		if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(this.parent)) {
			try {
				this.setChoosePath(fileChooser.getSelectedFile().getCanonicalPath());
				LOG.trace(this.choosePath);
				result = ChooseState.OK;
			} catch (IOException e) {
				throw new DskRuntimeException(e);
			}
		}
		return result;
	}

	@AutoLoad("exportpath")
	@Override
	public String getChoosePath() {
		return this.choosePath;
	}

	@AutoStore
	public void setChoosePath(@PropertyKey("exportpath") String choosePath) {
		this.choosePath = choosePath;
	}
}
