package dsk.php_export.core.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.change_vision.jude.api.inf.model.IClass;

import dsk.common.message.Message;
import dsk.export.ExportPath;
import dsk.export.delegate.DataSelect;
import dsk.export.service.AbstractExportService;
import dsk.export.utils.AstahModelUtil;

public class PhpExportService extends AbstractExportService {

	public PhpExportService() {
		super();
	}

	@Inject
	public PhpExportService(ExportPath exportPath, DataSelect<List<IClass>> dataSelect,
			@Named("checkbox") Message<Boolean> message, AstahModelUtil astahModelUtil) {
		super(exportPath, dataSelect, message, astahModelUtil);
	}

	@Override
	protected String getPrefix() {
		return "php";
	}

	@Override
	protected String getClassTemplateName() {
		return "php_class_template.vm";
	}

	@Override
	protected String getInterfaceTemplateName() {
		return "php_interface_template.vm";
	}
}
