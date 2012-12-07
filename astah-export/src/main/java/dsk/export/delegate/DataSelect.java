package dsk.export.delegate;

import dsk.common.message.ChooseState;

public interface DataSelect<E> {
	void setData(E data);

	ChooseState select();

	E getSelectedData();
}
