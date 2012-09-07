package dsk.php_export.core.service;

import java.awt.HeadlessException;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

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

import dsk.php_export.core.ExportPath;
import dsk.php_export.core.ExportPath.ChooseState;
import dsk.php_export.core.PhpExport;
import dsk.php_export.core.delegate.DataSelect;
import dsk.php_export.core.exception.ExportException;
import dsk.php_export.core.utils.SkeletonCodeTools;

public class PhpExportService implements PhpExport {
    private ExportPath exportPath;

    private DataSelect<List<IClass>> dataSelect;

    public PhpExportService(ExportPath exportPath, DataSelect<List<IClass>> dataSelect) {
        super();
        this.exportPath = exportPath;
        this.dataSelect = dataSelect;
    }

    public ExportState export(ProjectAccessor projectAccessor) throws ProjectNotFoundException,
            IOException, ExportException {
        IModel model = projectAccessor.getProject();
        System.out.println("get package.");
        List<IPackage> packageList = getPackageList(model, new ArrayList<IPackage>());
        for (IPackage p : packageList) {
            System.out.println(p.getName());
        }
        System.out.println("get class.");
        List<IClass> classes = getClassList(model, new ArrayList<IClass>());
        // ファイル選択
        this.dataSelect.setData(classes);
        this.dataSelect.select();
        List<IClass> selectedClasses = this.dataSelect.getSelectedData();
        if (null == selectedClasses) {
            throw new ExportException("null object.");
        }
        if (selectedClasses.isEmpty()) {
            return ExportState.ES_FAILD;
        }
        // 保存する場所を選択
        if (ChooseState.OK == this.exportPath.choose()) {
            for (IClass clazz : selectedClasses) {
                System.out.println(clazz.getName());
                if (!"".equals(clazz.getName())) {
                    printSkeletonCode(this.exportPath.getChoosePath(), clazz);
                }
            }
        }
        return ExportState.ES_SUCCESS;
    }

    private SkeletonCodeTools tools = new SkeletonCodeTools();

    private void printSkeletonCode(String exportDirPath, IClass clazz) {
        try {
            VelocityContext context = new VelocityContext();
            context.put("tools", tools);
            context.put("clazz", clazz);

            StringWriter sw = new StringWriter();
            Template template = this.getTemplate(tools.getClassTypeString(clazz));
            template.merge(context, sw);
            // System.out.println(sw.toString());
            this.write(exportDirPath, tools.getNamespace(clazz).replace("\\", "/"),
                    clazz.getName(), sw.toString());
            sw.flush();
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
        } catch (ParseErrorException e) {
            e.printStackTrace();
        } catch (MethodInvocationException e) {
            e.printStackTrace();
        } catch (HeadlessException e) {
            e.printStackTrace();
        }
    }

    private void write(String outputDirPath, String thePackagePath, String className, String text) {
        String fullDirPath = String.format("%s/%s", outputDirPath, thePackagePath);
        File dir = new File(fullDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filepath = String.format("%s%s.php", fullDirPath, className);
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
                Writer writer = new BufferedWriter(new OutputStreamWriter(os,
                        Charset.forName("UTF-8")))) {
            writer.write(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Template interfaceTemplate;
    private Template classTemplate;

    private Template getTemplate(String type) {
        try {
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
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
        } catch (ParseErrorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
