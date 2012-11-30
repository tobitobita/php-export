package dsk.php_export.plugin.module;

import java.util.List;
import java.util.Properties;

import com.change_vision.jude.api.inf.model.IClass;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

import dsk.common.annotation.AutoLoad;
import dsk.common.annotation.AutoStore;
import dsk.common.interceptor.AutoLoadInterceptor;
import dsk.common.interceptor.AutoStoreInterceptor;
import dsk.common.interceptor.LogInterceptor;
import dsk.common.util.DesktopTools;
import dsk.common.util.PropertiesHelper;
import dsk.common.util.PropertiesHelperImpl;
import dsk.export.ClassExport;
import dsk.export.ExportPath;
import dsk.export.delegate.DataSelect;
import dsk.php_export.core.service.PhpExportService;
import dsk.php_export.plugin.desktop.javafx.SelectPackagesDialog4Fx;
import dsk.php_export.plugin.desktop.swing.ExportPathDialog;
import dsk.php_export.plugin.desktop.swing.SelectPackagesDialog;

public class PhpExportModule extends AbstractModule {
	@Override
	protected void configure() {
		this.bind(ClassExport.class).to(PhpExportService.class);
		this.bind(ExportPath.class).to(ExportPathDialog.class);
		// TODO 要動作確認
		try {
			Class.forName("javafx.application.Application");
			this.bind(new TypeLiteral<DataSelect<List<IClass>>>() {
			}).toInstance(new SelectPackagesDialog4Fx());
		} catch (ClassNotFoundException e) {
			this.bind(new TypeLiteral<DataSelect<List<IClass>>>() {
			}).toInstance(new SelectPackagesDialog());
		}

		this.bindInterceptor(Matchers.any(), Matchers.any(), new LogInterceptor());

		// properties
		Properties prop = new Properties();
		PropertiesHelperImpl helper = new PropertiesHelperImpl(prop, String.format("%s%s",
				DesktopTools.getHomeDirectoryPath(), "/.exportplugin"), "exportplugin-system.xml");
		this.bind(PropertiesHelper.class).toInstance(helper);

		AutoStoreInterceptor autoStoreInterceptor = new AutoStoreInterceptor();
		this.requestInjection(autoStoreInterceptor);
		this.bindInterceptor(Matchers.subclassesOf(ExportPath.class), Matchers.annotatedWith(AutoStore.class),
				autoStoreInterceptor);
		AutoLoadInterceptor autoLoadInterceptor = new AutoLoadInterceptor();
		this.requestInjection(autoLoadInterceptor);
		this.bindInterceptor(Matchers.subclassesOf(ExportPath.class), Matchers.annotatedWith(AutoLoad.class),
				autoLoadInterceptor);
	}
}
