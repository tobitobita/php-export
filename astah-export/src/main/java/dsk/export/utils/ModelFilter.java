package dsk.export.utils;

import com.change_vision.jude.api.inf.model.INamedElement;

public interface ModelFilter<M extends INamedElement> {
	boolean isEnable(M model);
}
