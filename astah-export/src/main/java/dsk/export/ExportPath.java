package dsk.export;

import dsk.common.message.ChooseState;

public interface ExportPath {
	ChooseState choose();

	String getChoosePath();
}
