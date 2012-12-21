package dsk.java_export.core.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.change_vision.jude.api.inf.model.IClass;

import dsk.common.message.Message;
import dsk.export.ExportPath;
import dsk.export.delegate.DataSelect;
import dsk.export.service.AbstractExportService;
import dsk.export.utils.AstahModelUtil;

public class JavaExportService extends AbstractExportService {

	public JavaExportService() {
		super();
	}

	@Inject
	public JavaExportService(ExportPath exportPath, DataSelect<List<IClass>> dataSelect,
			@Named("checkbox") Message<Boolean> message, AstahModelUtil astahModelUtil) {
		super(exportPath, dataSelect, message, astahModelUtil);
	}

	@Override
	protected String getPrefix() {
		return "java";
	}

	@Override
	protected String getClassTemplateName() {
		return "java_class_template.vm";
	}

	@Override
	protected String getInterfaceTemplateName() {
		return "java_interface_template.vm";
	}
}
