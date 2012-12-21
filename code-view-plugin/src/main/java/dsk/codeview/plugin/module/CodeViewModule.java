package dsk.codeview.plugin.module;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

import dsk.codeview.plugin.view.Constants;
import dsk.export.ClassExport;
import dsk.export.tools.SkeletonCodeTools;
import dsk.java_export.core.service.JavaExportService;
import dsk.php_export.core.service.PhpExportService;

public class CodeViewModule extends AbstractModule {
	@Override
	protected void configure() {
		Map<String, ClassExport> exports = new HashMap<String, ClassExport>();
		exports.put(Constants.PHP, new PhpExportService());
		exports.put(Constants.JAVA, new JavaExportService());

		this.bind(new TypeLiteral<Map<String, ClassExport>>() {
		}).toInstance(exports);
		this.bind(SkeletonCodeTools.class).in(Scopes.SINGLETON);
	}
}
