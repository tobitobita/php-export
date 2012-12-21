package dsk.export.service;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import dsk.common.message.ChooseState;
import dsk.common.message.Message;
import dsk.common.util.IoTools;
import dsk.export.ClassExport;
import dsk.export.ExportPath;
import dsk.export.delegate.DataSelect;
import dsk.export.exception.ExportException;
import dsk.export.tools.SkeletonCodeTools;
import dsk.export.utils.AstahModelUtil;
import dsk.export.utils.ClassFilter;

public abstract class AbstractExportService implements ClassExport {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractExportService.class);

	private ExportPath exportPath;

	private DataSelect<List<IClass>> dataSelect;

	private Message<Boolean> message;

	private SkeletonCodeTools tools = new SkeletonCodeTools();

	private AstahModelUtil astahModelUtil;

	public AbstractExportService() {
		super();
	}

	public AbstractExportService(ExportPath exportPath, DataSelect<List<IClass>> dataSelect, Message<Boolean> message,
			AstahModelUtil astahModelUtil) {
		super();
		this.exportPath = exportPath;
		this.dataSelect = dataSelect;
		this.message = message;
		this.astahModelUtil = astahModelUtil;
	}

	@Override
	public ExportState export(ProjectAccessor projectAccessor) throws ProjectNotFoundException, IOException,
			ExportException {
		LOG.trace("get class.");
		List<IClass> classes = this.astahModelUtil.getClasses(projectAccessor.getProject(), new ClassFilter());
		// ファイル選択
		this.dataSelect.setData(classes);
		if (ChooseState.CANCEL == this.dataSelect.select()) {
			return ExportState.ES_FAILD;
		}
		List<IClass> selectedClasses = this.dataSelect.getSelectedData();
		if (null == selectedClasses) {
			throw new ExportException("null object.");
		}
		if (selectedClasses.isEmpty()) {
			return ExportState.ES_FAILD;
		}
		// 保存する場所を選択
		if (ChooseState.CANCEL == this.exportPath.choose()) {
			return ExportState.ES_FAILD;
		}
		for (IClass clazz : selectedClasses) {
			LOG.trace(clazz.getName());
			if (!"".equals(clazz.getName())) {
				printSkeletonCode(this.exportPath.getChoosePath(), clazz);
			}
		}
		return ExportState.ES_SUCCESS;
	}

	private void printSkeletonCode(String exportDirPath, IClass clazz) throws IOException {
		this.write(exportDirPath, tools.getNamespace(clazz).replace("\\", "/"), clazz.getName(),
				this.createSkeletonCode(clazz));
	}

	@Override
	public String createSkeletonCode(IClass clazz) throws IOException {
		VelocityContext context = new VelocityContext();
		context.put("tools", tools);
		context.put("clazz", clazz);
		// 継承インターフェイス
		context.put("generalizations", this.tools.createGeneralizationsStr(clazz));

		String sourceStr = null;
		StringWriter sw = null;
		try {
			sw = new StringWriter();
			Template template = this.getTemplate(tools.getClassTypeString(clazz));
			template.merge(context, sw);
			sourceStr = sw.toString();
			sw.flush();
		} finally {
			IoTools.close(sw);
		}
		return sourceStr;
	}

	private void write(String outputDirPath, String thePackagePath, String className, String text) throws IOException {
		String fullDirPath = String.format("%s/%s/", outputDirPath, thePackagePath);
		File dir = new File(fullDirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String filepath = String.format("%s%s.%s", fullDirPath, className, getPrefix());
		OutputStream os = null;
		Writer writer = null;
		File f = new File(filepath);
		ChooseState fileOverWrite = ChooseState.OK;
		if (f.exists() && this.isShowMessage()) {
			fileOverWrite = this.message.showMessage(f.getCanonicalPath());
		}
		if (fileOverWrite == ChooseState.OK) {
			try {
				os = new BufferedOutputStream(new FileOutputStream(f));
				writer = new BufferedWriter(new OutputStreamWriter(os, Charset.forName("UTF-8")));
				writer.write(text);
			} finally {
				IoTools.close(writer);
				IoTools.close(os);
			}
		}
	}

	protected abstract String getPrefix();

	private boolean isShowMessage() {
		return !this.message.getValue();
	}

	private Template interfaceTemplate;
	private Template classTemplate;

	private Template getTemplate(String type) throws IOException {
		Properties p = new Properties();
		p.load(getClass().getClassLoader().getResourceAsStream("velocity.properties"));
		VelocityEngine velocity = new VelocityEngine();
		velocity.init(p);
		if ("interface".equals(type)) {
			if (null == this.interfaceTemplate) {
				this.interfaceTemplate = velocity.getTemplate(this.getInterfaceTemplateName());
			}
			return this.interfaceTemplate;
		} else {
			if (null == this.classTemplate) {
				this.classTemplate = velocity.getTemplate(this.getClassTemplateName());
			}
			return this.classTemplate;
		}
	}

	protected abstract String getClassTemplateName();

	protected abstract String getInterfaceTemplateName();
}
