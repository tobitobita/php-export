package dsk.php_export.core.service;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IAssociationClass;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IRequirement;
import com.change_vision.jude.api.inf.model.ISubsystem;
import com.change_vision.jude.api.inf.model.ITestCase;
import com.change_vision.jude.api.inf.model.IUseCase;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import dsk.common.util.IoTools;
import dsk.export.tools.SkeletonCodeTools;
import dsk.php_export.core.ExportPath;
import dsk.php_export.core.ExportPath.ChooseState;
import dsk.php_export.core.ClassExport;
import dsk.php_export.core.delegate.DataSelect;
import dsk.php_export.core.exception.ExportException;

public class PhpExportService implements ClassExport {
    private static final Logger LOG = LoggerFactory.getLogger(PhpExportService.class);

    private ExportPath exportPath;

    private DataSelect<List<IClass>> dataSelect;

    private SkeletonCodeTools tools = new SkeletonCodeTools();

    public PhpExportService() {
        super();
    }

    @Inject
    public PhpExportService(ExportPath exportPath, DataSelect<List<IClass>> dataSelect) {
        super();
        this.exportPath = exportPath;
        this.dataSelect = dataSelect;
    }

    public ExportState export(ProjectAccessor projectAccessor) throws ProjectNotFoundException,
            IOException, ExportException {
        IModel model = projectAccessor.getProject();
        LOG.trace("get package.");
        List<IPackage> packageList = getPackageList(model, new ArrayList<IPackage>());
        for (IPackage p : packageList) {
            LOG.trace(p.getName());
        }
        LOG.trace("get class.");
        List<IClass> classes = getClassList(model, new ArrayList<IClass>());
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

    private void write(String outputDirPath, String thePackagePath, String className, String text)
            throws IOException {
        String fullDirPath = String.format("%s/%s/", outputDirPath, thePackagePath);
        File dir = new File(fullDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filepath = String.format("%s%s.php", fullDirPath, className);
        OutputStream os = null;
        Writer writer = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            writer = new BufferedWriter(new OutputStreamWriter(os, Charset.forName("UTF-8")));
            writer.write(text);
        } finally {
            IoTools.close(writer);
            IoTools.close(os);
        }
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
                this.interfaceTemplate = velocity.getTemplate("php_interface_template.vm");
            }
            return this.interfaceTemplate;
        } else {
            if (null == this.classTemplate) {
                this.classTemplate = velocity.getTemplate("php_class_template.vm");
            }
            return this.classTemplate;
        }
    }

    /**
     * 指定パッケージ配下のパッケージを、再帰的に全て取得する。
     * 
     * @param thePackage
     *            指定パッケージ
     * @param packageList
     *            パッケージ一覧を格納するリスト
     * @return パッケージ一覧を格納したリスト
     */
    private List<IPackage> getPackageList(IPackage model, List<IPackage> packageList) {
        INamedElement[] namedElements = model.getOwnedElements();
        for (INamedElement namedElement : namedElements) {
            if (namedElement instanceof IPackage) {
                IPackage p = (IPackage) namedElement;
                packageList.add(p);
                getPackageList(p, packageList);
            }
        }
        return packageList;
    }

    /**
     * 指定パッケージ配下のクラスを、再帰的に全て取得する。
     * 
     * @param thePackage
     *            指定パッケージ
     * @param classList
     *            パッケージ一覧を格納するリスト
     * @return パッケージ一覧を格納したリスト
     */
    private List<IClass> getClassList(IPackage model, List<IClass> classList) {
        if (null == model) {
            return classList;
        }
        INamedElement[] namedElements = model.getOwnedElements();
        for (INamedElement namedElement : namedElements) {
            if (namedElement instanceof IClass
                    && !(namedElement instanceof IUseCase || namedElement instanceof ITestCase
                            || namedElement instanceof ISubsystem
                            || namedElement instanceof IRequirement || namedElement instanceof IAssociationClass)) {
                IClass c = (IClass) namedElement;
                classList.add(c);
            }
            if (namedElement instanceof IPackage) {
                IPackage p = (IPackage) namedElement;
                getClassList(p, classList);
            }
        }
        return classList;
    }
}
