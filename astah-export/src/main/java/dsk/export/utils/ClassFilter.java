package dsk.export.utils;

import org.apache.commons.lang3.StringUtils;

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IClassifierTemplateParameter;
import com.change_vision.jude.api.inf.model.IElement;

/**
 * Classのみ取得するフィルター
 */
public class ClassFilter implements ModelFilter<IClass> {
	@Override
	public boolean isEnable(IClass model) {
		if (StringUtils.isEmpty(model.getName())) {
			return false;
		}
		IElement owner = model.getOwner();
		if (owner instanceof IClassifierTemplateParameter) {
			return false;
		}
		return true;
	}
}
