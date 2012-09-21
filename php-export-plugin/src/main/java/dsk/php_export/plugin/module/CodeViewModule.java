package dsk.php_export.plugin.module;

import com.google.inject.AbstractModule;

import dsk.php_export.core.PhpExport;
import dsk.php_export.core.service.PhpExportService;

public class CodeViewModule extends AbstractModule {
    @Override
    protected void configure() {
        this.bind(PhpExport.class).toInstance(new PhpExportService());
    }
}
