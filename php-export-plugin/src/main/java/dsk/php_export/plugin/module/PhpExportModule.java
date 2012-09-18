package dsk.php_export.plugin.module;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

import dsk.common.interceptor.LogInterceptor;
import dsk.php_export.core.ExportPath;
import dsk.php_export.core.PhpExport;
import dsk.php_export.core.delegate.DataSelect;
import dsk.php_export.core.desktop.swing.ExportPathDialog;
import dsk.php_export.core.desktop.swing.SelectPackagesDialog;
import dsk.php_export.core.service.PhpExportService;

public class PhpExportModule extends AbstractModule {
    @Override
    protected void configure() {
        this.bind(PhpExport.class).to(PhpExportService.class);
        this.bind(ExportPath.class).to(ExportPathDialog.class);
        // TODO うまくいかない
        this.bind(DataSelect.class).toInstance(new SelectPackagesDialog());

        this.bindInterceptor(Matchers.any(), Matchers.any(), new LogInterceptor());
    }
}
