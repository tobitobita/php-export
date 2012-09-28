package dsk.php_export.plugin.module;

import java.util.List;

import com.change_vision.jude.api.inf.model.IClass;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

import dsk.common.interceptor.LogInterceptor;
import dsk.export.ClassExport;
import dsk.export.ExportPath;
import dsk.export.delegate.DataSelect;
import dsk.export.desktop.swing.ExportPathDialog;
import dsk.export.desktop.swing.SelectPackagesDialog;
import dsk.php_export.core.service.PhpExportService;

public class PhpExportModule extends AbstractModule {
    @Override
    protected void configure() {
        this.bind(ClassExport.class).to(PhpExportService.class);
        this.bind(ExportPath.class).to(ExportPathDialog.class);
        this.bind(new TypeLiteral<DataSelect<List<IClass>>>() {
        }).toInstance(new SelectPackagesDialog());

        this.bindInterceptor(Matchers.any(), Matchers.any(), new LogInterceptor());
    }
}
